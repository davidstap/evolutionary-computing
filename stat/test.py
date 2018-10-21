from parse import parse_file

import sys
import numpy as np
import matplotlib.pyplot as plt

# Gets given indices in each row in given 2+D array.
def apply_index(arr, ind):
    return np.array([a[i] for a,i in zip(arr, ind)])

def main(infile):
    # Get fitness, genomes and sigmas from input file.
    run = parse_file(infile)
    fitness = run.get_fitness()
    genome = run.get_genome()
    sigmas = run.get_sigmas()
    
    # Only take data from best performing genome in each generation.
    index = np.argmax(fitness, axis=1)
    max_fitness = apply_index(fitness, index)
    max_genome = apply_index(genome, index)
    max_sigmas = apply_index(sigmas, index)
    
    # Plot fitness.
    plt.figure()
    plt.title('fitness')
    plt.plot(range(len(max_fitness)), max_fitness)
    
    # Plot genome dimensions.
    plt.figure()
    plt.title('genome')
    for dimension in max_genome.T:
        plt.plot(range(len(dimension)), dimension)
    
    # Plot sigma dimensions.
    plt.figure()
    plt.title('sigmas')
    for dimension in max_sigmas.T:
        plt.plot(range(len(dimension)), dimension)
    
    plt.show()

# RUN: python main.py <input-file>
# input-file = file with one copy-pasted "make test{c,k,s}" run
if __name__ == '__main__':
    main(sys.argv[1])

