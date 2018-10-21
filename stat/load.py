from parse import parse_file

from os import listdir

import sys
import numpy as np
import matplotlib.pyplot as plt

def main(d):
    types = ['katsuura']#, 'cigar', 'schaffers', 'katsuura']
    for subd in types:
        mean = np.load("tmparr/" + subd + "_mean.arr.npy")
        std = np.load("tmparr/" + subd + "_std.arr.npy")
#        imean = np.load("tmparr/i" + subd + "_mean.arr.npy")
#        istd = np.load("tmparr/i" + subd + "_std.arr.npy")
        arr = np.load("tmparr/testing.npy")
        imean = np.mean(arr, axis=0)
        istd = np.std(arr, axis=0)

        fig = plt.figure()
        ax1 = fig.add_subplot(111)
        ax2 = ax1.twiny()

        interval = int(len(mean) / 20)
        ln1 = ax1.plot(range(len(mean)), mean,
                label='Self-adaptive model', c='C0')
        ax1.errorbar(np.arange(len(mean))[::interval], mean[::interval],
                std[::interval], linestyle='None', marker='None', c='C0')
        ax1.set_xlabel('Generation (self-adaptive model)')
        ax1.grid(True)

        interval = int(len(imean) / 20)
        ln2 = ax2.plot(range(len(imean)), imean,
                label='Island model', c='C1')
        ax2.errorbar(np.arange(len(imean))[::interval], imean[::interval],
                istd[::interval], linestyle='None', marker='None', c='C1')
        ax2.set_xlabel('Generation (island model)')
        ax2.grid(True)

        ax1.set_ylabel('Best fitness')
        plt.title(subd.capitalize(), y=1.15)
        lns = ln1 + ln2
        labs = [l.get_label() for l in lns]
        ax1.legend(lns, labs, bbox_to_anchor=(1.0,0.4))
        plt.tight_layout()
    plt.show()

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('No input directory given.')
    else:
        main(sys.argv[1])

