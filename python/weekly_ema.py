# All right this is exciting! Time to write my first python script to do machine learning!
# This will aim to train a neural network model to predict stock price movement based on various values from EMA.
# The features that we consider are:
#
# - Relative open/high/low/close price compared to the last week's close price. 
# - How far is the current close price from the EMA trending line.
# - Slope of the EMA line.
# - Current week's volume / previous X weeks average volume (excluding the current week)
#
# The targets we are training for are basically maximum profit/loss in the next X weeks.
# The maximum profit uses the highest price and the maximum loss uses the lowest price, therefore
# the results might be more volatile. Our goal is to identify those candles such that they have
# a profit as large as possible while minimizing the loss in the near future (ideally, 0 loss :) ) 

import math

from IPython import display
from matplotlib import cm
from matplotlib import gridspec
from matplotlib import pyplot as plt
import numpy as np
import pandas as pd
from sklearn import metrics
import tensorflow as tf
from tensorflow.python.data import Dataset

tf.logging.set_verbosity(tf.logging.ERROR)
pd.options.display.max_rows = 10
pd.options.display.float_format = '{:.4f}'.format

ema_dataframe = pd.read_csv(
    "https://s3-us-west-2.amazonaws.com/stock-data-daily/EOD_20180119_features.csv",
    dtype={
        "Open": np.float64,
        "High": np.float64,
        "Low": np.float64,
        "Close": np.float64,
        "Dist4": np.float64,
        "Dist12": np.float64,
        "Dist26": np.float64,
        "Dist52": np.float64,        
        "Slope4": np.float64,
        "Slope12": np.float64,
        "Slope26": np.float64,
        "Slope52": np.float64,
        "Volume4": np.float64,
        "Volume12": np.float64,
        "Volume26": np.float64,
        "Volume52": np.float64,
        "Profit4": np.float64,
        "Profit12": np.float64,
        "Profit26": np.float64,
        "Loss4": np.float64,
        "Loss12": np.float64,
        "Loss26": np.float64
    }, sep=",")
# ema_dataframe = pd.read_csv("../data/EOD_20180119_features.csv", dtype=np.float64, sep=",")

ema_dataframe = ema_dataframe.reindex(
    np.random.permutation(ema_dataframe.index))

def preprocess_features(ema_dataframe):
  """Prepares input features from the EMA data set.

  Args:
    ema_dataframe: A Pandas DataFrame expected to contain data
      from the EMA data set.
  Returns:
    A DataFrame that contains the features to be used for the model, including
    synthetic features.
  """
  selected_features = ema_dataframe[
    ["Open", "High", "Low", "Close",     
     "Dist4", "Dist12", "Dist26", "Dist52",
     "Slope4", "Slope12", "Slope26", "Slope52",
     "Volume4", "Volume12", "Volume26", "Volume52"]
  ]
  return selected_features
  
#   processed_features = selected_features.copy()
#   # Create a synthetic feature.
#   processed_features["rooms_per_person"] = (
#     california_housing_dataframe["total_rooms"] /
#     california_housing_dataframe["population"])
#   return processed_features

def preprocess_targets(ema_dataframe):
  """Prepares target features (i.e., labels) from EMA data set.

  Args:
    ema_dataframe: A Pandas DataFrame expected to contain data
      from the EMA data set.
  Returns:
    A DataFrame that contains the target feature.
  """
  output_targets = pd.DataFrame()
  output_targets["Profit4"] = (ema_dataframe["Profit4"])
#   # Scale the target to be in units of thousands of dollars.
#   output_targets["median_house_value"] = (
#     california_housing_dataframe["median_house_value"] / 1000.0)
  return output_targets

# Choose the first 12000 (out of 17000) examples for training.
training_examples = preprocess_features(ema_dataframe.head(12000))
training_targets = preprocess_targets(ema_dataframe.head(12000))

# Choose the last 5000 (out of 17000) examples for validation.
validation_examples = preprocess_features(ema_dataframe.tail(5000))
validation_targets = preprocess_targets(ema_dataframe.tail(5000))

# Double-check that we've done the right thing.
print("Training examples summary:")
display.display(training_examples.describe())
print("Validation examples summary:")
display.display(validation_examples.describe())

print("Training targets summary:")
display.display(training_targets.describe())
print("Validation targets summary:")
display.display(validation_targets.describe())

def construct_feature_columns(input_features):
  """Construct the TensorFlow Feature Columns.

  Args:
    input_features: The names of the numerical input features to use.
  Returns:
    A set of feature columns
  """ 
  return set([tf.feature_column.numeric_column(my_feature)
              for my_feature in input_features])

def my_input_fn(features, targets, batch_size=1, shuffle=True, num_epochs=None):
  """Trains a neural network model.

  Args:
    features: pandas DataFrame of features
    targets: pandas DataFrame of targets
    batch_size: Size of batches to be passed to the model
    shuffle: True or False. Whether to shuffle the data.
    num_epochs: Number of epochs for which data should be repeated. None = repeat indefinitely
  Returns:
    Tuple of (features, labels) for next data batch
  """
  
  # Convert pandas data into a dict of np arrays.
  features = {key:np.array(value) for key,value in dict(features).items()}                                           

  # Construct a dataset, and configure batching/repeating.
  ds = Dataset.from_tensor_slices((features,targets)) # warning: 2GB limit
  ds = ds.batch(batch_size).repeat(num_epochs)
  
  # Shuffle the data, if specified.
  if shuffle:
    ds = ds.shuffle(10000)
  
  # Return the next batch of data.
  features, labels = ds.make_one_shot_iterator().get_next()
  return features, labels

def train_nn_regression_model(
    my_optimizer,
    steps,
    batch_size,
    hidden_units,
    training_examples,
    training_targets,
    validation_examples,
    validation_targets):
  """Trains a neural network regression model.
  
  In addition to training, this function also prints training progress information,
  as well as a plot of the training and validation loss over time.
  
  Args:
    my_optimizer: An instance of `tf.train.Optimizer`, the optimizer to use.
    steps: A non-zero `int`, the total number of training steps. A training step
      consists of a forward and backward pass using a single batch.
    batch_size: A non-zero `int`, the batch size.
    hidden_units: A `list` of int values, specifying the number of neurons in each layer.
    training_examples: A `DataFrame` containing one or more columns from
      `ema_dataframe` to use as input features for training.
    training_targets: A `DataFrame` containing exactly one column from
      `ema_dataframe` to use as target for training.
    validation_examples: A `DataFrame` containing one or more columns from
      `ema_dataframe` to use as input features for validation.
    validation_targets: A `DataFrame` containing exactly one column from
      `ema_dataframe` to use as target for validation.
      
  Returns:
    A tuple `(estimator, training_losses, validation_losses)`:
      estimator: the trained `DNNRegressor` object.
      training_losses: a `list` containing the training loss values taken during training.
      validation_losses: a `list` containing the validation loss values taken during training.
  """

  periods = 10
  steps_per_period = steps / periods
  
  # Create a DNNRegressor object.
  my_optimizer = tf.contrib.estimator.clip_gradients_by_norm(my_optimizer, 5.0)
  dnn_regressor = tf.estimator.DNNRegressor(
      feature_columns=construct_feature_columns(training_examples),
      hidden_units=hidden_units,
      optimizer=my_optimizer
  )
  
  # Create input functions.
  training_input_fn = lambda: my_input_fn(training_examples, 
                                          training_targets["Profit4"], 
                                          batch_size=batch_size)
  predict_training_input_fn = lambda: my_input_fn(training_examples, 
                                                  training_targets["Profit4"], 
                                                  num_epochs=1, 
                                                  shuffle=False)
  predict_validation_input_fn = lambda: my_input_fn(validation_examples, 
                                                    validation_targets["Profit4"], 
                                                    num_epochs=1, 
                                                    shuffle=False)

  # Train the model, but do so inside a loop so that we can periodically assess
  # loss metrics.
  print("Training model...")
  print("RMSE (on training data):")
  training_rmse = []
  validation_rmse = []
  for period in range (0, periods):
    # Train the model, starting from the prior state.
    dnn_regressor.train(
        input_fn=training_input_fn,
        steps=steps_per_period
    )
    # Take a break and compute predictions.
    training_predictions = dnn_regressor.predict(input_fn=predict_training_input_fn)
    training_predictions = np.array([item['predictions'][0] for item in training_predictions])
    
    validation_predictions = dnn_regressor.predict(input_fn=predict_validation_input_fn)
    validation_predictions = np.array([item['predictions'][0] for item in validation_predictions])
    
    # Compute training and validation loss.
    training_root_mean_squared_error = math.sqrt(
        metrics.mean_squared_error(training_predictions, training_targets))
    validation_root_mean_squared_error = math.sqrt(
        metrics.mean_squared_error(validation_predictions, validation_targets))
    # Occasionally print the current loss.
    print("  period %02d : %0.2f" % (period, training_root_mean_squared_error))
    # Add the loss metrics from this period to our list.
    training_rmse.append(training_root_mean_squared_error)
    validation_rmse.append(validation_root_mean_squared_error)
  print("Model training finished.")

  # Output a graph of loss metrics over periods.
  plt.ylabel("RMSE")
  plt.xlabel("Periods")
  plt.title("Root Mean Squared Error vs. Periods")
  plt.tight_layout()
  plt.plot(training_rmse, label="training")
  plt.plot(validation_rmse, label="validation")
  plt.legend()

  print("Final RMSE (on training data):   %0.2f" % training_root_mean_squared_error)
  print("Final RMSE (on validation data): %0.2f" % validation_root_mean_squared_error)

  return dnn_regressor, training_rmse, validation_rmse

_ = train_nn_regression_model(
    my_optimizer=tf.train.GradientDescentOptimizer(learning_rate=0.0007),
    steps=5000,
    batch_size=100,
    hidden_units=[16, 16],
    training_examples=training_examples,
    training_targets=training_targets,
    validation_examples=validation_examples,
    validation_targets=validation_targets)