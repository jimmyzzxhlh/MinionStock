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

ema_dataframe = pd.read_csv("../data/EOD_20180119_features.csv", sep=",")

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
  output_targets = ema_dataframe["Profit4"]
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

