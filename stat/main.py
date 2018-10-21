from parse import parse_file

from os import listdir

import sys
import numpy as np
import matplotlib.pyplot as plt

def main(d):
    types = ['cigar', 'schaffers', 'katsuura']
    for subd, i in zip(types, range(len(types))):
        arr = []
        fs = np.sort([f for f in listdir(d + '/' + subd)])
        for f_ in fs:
            with open(d + '/' + subd + '/' + f_) as f:
                print(subd + '/' + f_)
                arr.append(np.max(parse_file(d + '/' + subd + '/' + f_
                        ).get_fitness(), axis=1))
        mean = np.mean(arr, axis=0)
        std = np.std(arr, axis=0)
        interval = int(len(mean) / 20)
        plt.plot(range(len(mean)), mean,label=subd, c='C' + str(i))
        plt.errorbar(np.arange(len(mean))[::interval], mean[::interval],
                std[::interval], linestyle='None', marker='None', c='C' + str(i))
    plt.xlabel('Generation')
    plt.ylabel('Best fitness')
    plt.grid(True)
    plt.legend()
    plt.show()

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('No input directory given.')
    else:
        main(sys.argv[1])

