import numpy as np
from sklearn.decomposition import PCA
import matplotlib.pyplot as plt

# Load data
# data = np.array([[1,3,4,8],[1,9.9,9.9,9.9], [1, 9.9, 9, 8],[2, 8, 4, 2],[2, 9, 4.5, 1]])
data = np.genfromtxt('stats.csv', delimiter=',')

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
    # Scale is mean fitness on all 3 functions
    # Bigger points are fitter individuals
    scale = np.mean(X[i])*10
    plt.scatter(x[0], x[1], color=colordict[y[i]], s=scale, alpha=0.3)


plt.show()





