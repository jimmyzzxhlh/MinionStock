import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from scipy import stats

sns.set(color_codes=True)
data_set = pd.read_csv('../data/spy_volatility_features.csv')
sns.distplot(data_set['ST1'])

plt.show()
