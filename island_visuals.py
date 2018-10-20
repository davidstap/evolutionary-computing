import numpy as np
from sklearn.decomposition import PCA
import matplotlib.pyplot as plt
import math

# For every individual:
# [n_island, score_1, score_2, score_3, max_island_fitness, min_island_fitness, mean_island_fitness, variance_fitness]

# Load data
# bentcigar = np.genfromtxt('stats_bentcigar.csv', delimiter=',')
# # For katsuura and schaffers ignore 1st column, since we already know n_island
# katsuura = np.genfromtxt('stats_katsuura.csv', delimiter=',')[:,1:]
# schaffers = np.genfromtxt('stats_schaffers.csv', delimiter=',')[:,1:]
# data = np.concatenate((np.concatenate((bentcigar, katsuura),axis=1), schaffers), axis=1)


data = np.genfromtxt('stats_bentcigar.csv', delimiter=',')
# data = np.genfromtxt('stats_katsuura.csv', delimiter=',')
# data = np.genfromtxt('stats_schaffers.csv', delimiter=',')



# X are the features, y are the island names (simply ints)
X = data[:,1:]
y = [str('island {}'.format(int(x))) for x in data[:,0]]

# Do PCA on data
pca = PCA(n_components=2)
X_r = pca.fit(X).transform(X)

# 2D visualization of X
colordict = {'island 1': 'blue', 'island 2': 'red', 'island 3': 'green'}
plt.figure()
points = []
for i, x in enumerate(X_r):
    # Use mean of square of scores of all 3 functions to calculate scale
    # Bigger points are fitter individuals
    # scale = np.mean(X[i][[0,5,10]]**2)*5
    
    scale = (X[i][0]**2)*5
    
    plt.scatter(x[0], x[1], color=colordict[y[i]], s=scale, alpha=0.2)

plt.show()





