import re
import numpy as np

# Class to save run information.
# Holds Run > Population > Unit,
# each with their own add_<subclass>,
# get_{fitness, genome, sigmas}
# and __str__() method.
class Run:
    class Population:
        class Unit:
            def __init__(self, fitness, genome, sigmas):
                self.fitness = fitness
                self.genome = np.array(genome)
                self.sigmas = np.array(sigmas)
            def __str__(self):
                s = str(self.fitness) + ' ['
                for gene, sigma in zip(self.genome, self.sigmas):
                    s += '(' + str(gene) + ', ' + str(sigma) + '), '
                return s[:-2] + ']'
            def get_fitness(self):
                return self.fitness
            def get_genome(self):
                return self.genome.copy()
            def get_sigmas(self):
                return self.sigmas.copy()
        def __init__(self):
            self.units = np.array([])
        def add_unit(self, fitness, genome, sigmas):
            self.units = np.append(
                    self.units, [self.Unit(fitness, genome, sigmas)])
        def __str__(self):
            s = ''
            for unit in self.units:
                s += str(unit) + '\n'
            return s[:-1]
        def get_fitness(self):
            return np.array([unit.get_fitness() for unit in self.units])
        def get_genome(self):
            return np.array([unit.get_genome() for unit in self.units])
        def get_sigmas(self):
            return np.array([unit.get_sigmas() for unit in self.units])
    def __init__(self):
        self.populations = np.array([])
    def add_population(self):
        self.populations = np.append(self.populations, [self.Population()])
    def add_unit(self, fitness, genome, sigmas):
        self.populations[-1].add_unit(fitness, genome, sigmas)
    def __str__(self):
        s = ''
        for population in self.populations:
            s += str(population) + '\n\n'
        return s[:-2]
    def get_fitness(self):
        return np.array(
                [population.get_fitness() for population in self.populations])
    def get_genome(self):
        return np.array(
                [population.get_genome() for population in self.populations])
    def get_sigmas(self):
        return np.array(
                [population.get_sigmas() for population in self.populations])

# Parses a given line.
# FORMAT: <float> [(<float>, <float>), ...]
def parse_line(s):
    fitness, arr = s.strip().split(' ', 1)
    nums = re.sub('[\[\]\(\)]', '', arr.strip())
    floats = [float(x.strip()) for x in nums.split(', ')]
    return (float(fitness), np.array(floats[::2]), np.array(floats[1::2]))

# Parses a given file.
def parse_file(infile):
    run = Run()
    with open(infile) as f:
        # Remove makefile logging.
        for l in f:
            if l.strip() == '':
                run.add_population()
                break
        # Parse output.
        for l in f:
            # Stop parsing at line that gives final run score.
            if (l[0] == 'S'):
                run.populations = np.delete(run.populations, -1)
                break
            if (l.strip() == ""):
                run.add_population()
            else:
                run.add_unit(*parse_line(l))
    return run

