from parse import parse_file

from os import listdir

import sys
import numpy as np
import matplotlib.pyplot as plt

def main():
    types = ['cigar', 'schaffers', 'katsuura']
    for subd in types:
        mean = np.load("tmparr/" + subd + "_mean.arr.npy")
        std = np.load("tmparr/" + subd + "_std.arr.npy")
        print(subd, mean[-1], std[-1])

if __name__ == '__main__':
    main()
