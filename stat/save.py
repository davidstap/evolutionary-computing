from parse import parse_file

from os import listdir

import sys
import numpy as np
import matplotlib.pyplot as plt

def main(d):
    types = ['katsuura']#, 'schaffers', 'katsuura']
    for subd, i in zip(types, range(len(types))):
        arr = []
        fs = np.sort([f for f in listdir(d + '/' + subd)])
        fs = np.append(fs[80:], fs[:80])
        for f_ in fs:
            with open(d + '/' + subd + '/' + f_) as f:
                print(subd + '/' + f_)
                arr.append(np.max(parse_file(d + '/' + subd + '/' + f_
                        ).get_fitness(), axis=1))
            np.save("tmparr/tmpkat", np.array(arr))
        mean = np.mean(arr, axis=0)
        std = np.std(arr, axis=0)
        tmpf = "tmparr/i" + subd + "_mean.arr"
        np.save(tmpf, mean)
        tmpf = "tmparr/i" + subd + "_std.arr"
        np.save(tmpf, std)

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('No input directory given.')
    else:
        main(sys.argv[1])

