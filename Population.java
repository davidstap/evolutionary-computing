import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


import java.util.Random;
import java.util.function.Function;

public class Population
{

    // Genome length.
    private static final int dim = 10;

    private Population.Individual[] individuals;
    private Function<double[], Object> evaluationFunction;

    // XXX should probably be temporary?
    public Population()
    {
    }

    // Create population of given size using given evaluation-function
    // and random number generator.
    public Population(
            int size,
            Function<double[], Object> evaluationFunction_, Random rnd)
    {
        individuals = new Individual[size];
        for (int i = 0; i < size; i++)
        {
            individuals[i] = new Individual(rnd);
        }
        evaluationFunction = evaluationFunction_;
    }

    // Create population of given individuals using given evaluation-function
    // and random number generator.
    public Population(
            Individual[] individuals_,
            Function<double[], Object> evaluationFunction_)
    {
        individuals = Arrays.copyOfRange(individuals_, 0, individuals_.length);
        evaluationFunction = evaluationFunction_;
    }

    // Create population of n (<- N) copies of each given parent.
    // Uses given evaluation function and random number generator.
    public Population(
            Individual[] parents, int[] N,
            Function<double[], Object> evaluationFunction_)
    {
        // Count total population size.
        int i = 0;
        for (int n : N)
        {
            i += n;
        }
        individuals = new Individual[i];
        
        // Create copies of parents.
        i = 0;
        for (int j = 0; j < N.length; j++)
        {
            for (int k = 0; k < N[j]; k++)
            {
                individuals[i] = new Individual(
                        parents[j].getGenome(), parents[j].getSigmas());
                i++;
            }
        }
        
        evaluationFunction = evaluationFunction_;
    }

    // Returns copied list of individuals in population.
    public Individual[] getIndividuals()
    {
        return individuals.clone();
    }

    // Evaluates members of population keeping track of number of total
    // evaluations, returns new total number of evaluations. If evaluations
    // limit is exceeded, deletes not-evaluated individuals from population.
    public int evaluate(int evals, int evaluations_limit)
    {
        for (int i = 0; i < individuals.length; i++)
        {
            // Check for exceeding of evaluations limit.
            if (evals >= evaluations_limit)
            {
                // Discard not-evaluated part of population.
                if (i == 0)
                {
                    individuals = new Individual[0];
                }
                else
                {
                    individuals = Arrays.copyOfRange(individuals, 0, i);
                }
                break;
            }
            // Evaluate individual.
            individuals[i].fitness = (double) evaluationFunction.apply(
                    individuals[i].getGenome());
            evals++;
        }
        return evals;
    }

    public Individual[] parentSelection(int k)
    {
        return Selection.greedy(this.getIndividuals(), k);
    }

    public Individual[] recombination(
            Individual[] individuals, Random rnd)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        return Recombination.recombination(
                individuals, rnd, Recombination.TYPE.ONEPOINT);
    }

    // TODO move to Selection.java.
    // Selects n parents based on Roulette Wheel and ranking selection.
    public Individual[] parentSelectionRouletteWheel(int n, double s)
    {
      sort();
      setRanks();
      setSelectionRank(s); 
      
      Individual[] parents = new Individual[n];
      
      Random rnd = new Random();
      int currentMember = 0;
      
      for (int j = 0; j < n; j++)
      {
        double r = rnd.nextDouble();
        int i = 0;
        double a_i = individuals[0].selectionRanking;
        
        // stop when cumulative probability exceeds r,
        // then use i for individual.
        while (a_i < r)
        {
          i+=1;
          // Update cumulative probability distribution.
          a_i += individuals[i].selectionRanking;
        }
        parents[j] = individuals[i];
      }
      return parents;
    }

    // TODO move to Selection.java.
    // Adds to every member of population the correct rank
    // (based on fitness score).
    // Lowest rank = 0
    // Highest rank = mu-1
    // assumption: population is sorted before setRanks() is called
    private void setRanks()
    {
      // start at highest rank
      int currentRank = individuals.length - 1;
      
      for (int i=0; i<individuals.length; i++)
      {
        individuals[i].rank = currentRank;
        currentRank -= 1;
      }
    }
    
    // TODO move to Selection.java.
    // Adds correct value for selectionRank to every individual in population.
    private void setSelectionRank(double s)
    {
      for (int i=0; i<individuals.length; i++)
      {
        // formula for selectionRank (see book p82)
        double mu = (double) individuals.length;
        double sr = (2.0-s)/(mu) + (2.0*individuals[i].rank*(s-1)) /
                (mu*(mu-1.0));
        // System.out.println(sr);
        individuals[i].selectionRanking = sr;
      }
    }

    // TODO move to Selection.java.
    // Select parents based on the tournamenet selection method found on pg 84-85
    public Individual[] selectParentsTournament(int n, int k, int c)
    {
        sort();
        Individual[] parents = new Individual[n];

        // Numbers list for random sampling
        List<Integer> numbers = ListOfRange(n);

        // Compute mean and sd for prop fitness function
        double mean = calculateAverageFitness();
        double sd = calculateSDFitness(mean);
        computePropFitness(mean, sd, c);
        
        // Repeat tournament selection for amount of parents needed.
        for (int i=0; i<n; i++)
        {
            // Create candidates for tournament by random sampling k candidates with replacement.
            Individual[] candidates = pickNRandom(k, numbers);
            // Sort highest to lowest based on prop fitness.
            candidates = sort_prop(candidates);
            //  Assign best candidate to parents
            parents[i] = candidates[0];
        }
        return parents;
    }

    // Calculates the Mean of the population fitnesses
    public double calculateAverageFitness()
    {
        double fit_sum = 0.0;
        for (int i = 0; i < individuals.length; i++)
        {
            fit_sum += individuals[i].fitness;
        }
        return fit_sum/individuals.length;
    }

    // Calculates the standard deviation of the population fitnesses
    public double calculateSDFitness(double mean) 
    {
        double temp = 0.0;
        for (int i = 0; i < individuals.length; i++)
        {
            double diff_to_mean = Math.pow(individuals[i].fitness - mean, 2);
            temp += diff_to_mean;
        }
        return Math.sqrt(temp/individuals.length);

    }

    // Computes the proportional fitness for the population
    public void computePropFitness(double mean, double sd, int c)
    {
        for (int i = 0; i < individuals.length; i++)
        {
            // Function found in book pg: 81.
            double prop_fitness = individuals[i].fitness - (mean - c*sd);

            // Assign prop fitness to individual
            individuals[i].prop_fitness = Math.max(0, prop_fitness);
        }
    }

    // Create list of numbers in range 0 to n
    public List<Integer> ListOfRange(int n)
    {
        List<Integer> numbers = new ArrayList<Integer>();
        for(int i = 0; i < n; i++)
        {
            numbers.add(i);        
        }
        return numbers;
    }


    // Randomly sample over the individuals with the use of a list in range 0 to k
    public Individual[] pickNRandom(int n, List<Integer> numbers) 
    {
        Individual[] candidates = new Individual[n];

        // Shuffle to get random numbers
        Collections.shuffle(numbers);
        for (int i = 0; i < n; i++)
        {
            // Get random individual
            candidates[i] = individuals[numbers.get(i)];
        }
        return candidates;
    }

    // Sort the population based on the proportional fitness
    public Individual[] sort_prop(Individual[] candidates)
    {
        Arrays.sort(candidates, (u1, u2) -> Double.compare(u2.prop_fitness, u1.prop_fitness));
        return candidates;
    }

    // Mutates all individuals in the population.
    // Added mutation method to give control over type of mutation
    // TODO difference normal_ and normal?
    public void mutate(Random rnd, String mutation_method)
    {
        switch(mutation_method)
        {
            case "normal":
                for (Individual individual : individuals)
                {
                    individual.mutate_normal(rnd);
                }
                break;
            case "uncorrelated":
                for (Individual individual : individuals)
                {
                    individual.mutate(rnd);
                }
                break;
        }
    }

    // Applies survival selection onto population.
    public void survival(Population childPopulation, Random rnd)
            throws ArrayIndexOutOfBoundsException
    {
        Individual[] selected = Selection.mu_plus_lambda(
            this.getIndividuals(), childPopulation.getIndividuals());
        individuals = selected;
    }

    // Returns maximal fitness value found in the population.
    public double getMaxFitness()
    {
        if (individuals.length > 0)
        {
            sort();
            return individuals[0].fitness;
        }
        return 0.0;
    }

    // Sorts list of individuals from largest fitness value to smallest.
    public static Individual[] sort(Individual[] individuals_)
    {
        Arrays.sort(individuals_, (i1, i2) -> Double.compare(
                i2.fitness, i1.fitness));
        return individuals_;
    }

    // Sorts list of individuals from smallest fitness value to largest.
    public static Individual[] reverseSort(Individual[] individuals_)
    {
        Arrays.sort(individuals_, (i1, i2) -> Double.compare(
                i1.fitness, i2.fitness));
        return individuals_;
    }

    // Sorts population from largest fitness value to smallest.
    public void sort()
    {
        individuals = sort(individuals);
    }

    // Sorts population from smallest fitness value to largest.
    public void reverseSort()
    {
        individuals = reverseSort(individuals);
    }

    public int size()
    {
        return individuals.length;
    }

    public void print()
    {
        System.out.println();
        for (int i = 0; i < individuals.length; i++)
        {
            System.out.println(individuals[i]);
        }
    }

    // Subclass to contain one individual of the population.
    public static class Individual implements Comparable<Individual>
    {

        // Ranges in which genome values fall.
        public static final double minR = -5.0;
        public static final double maxR = 5.0;
        public static final double R = maxR - minR;

        private double[] genome;
        private double[] sigmas;
        public double fitness;
        public double prop_fitness;
        
        // Used for ranking selection (see p81 book)
        public double selectionRanking;
        // ranking of individual. Note: worst rank = 0, best rank = mu-1
        public int rank;
        

        // Basic setting of default variables.
        private void init()
        {
            fitness = 0.0;
            selectionRanking = 0.0;
            prop_fitness =0.0;
            rank = 0;
        }

        // Creates individual with random genome and default sigma values
        // (filled with 1's).
        public Individual(Random rnd)
        {
            init();
            genome = Mutation.normal(new double[dim], rnd, R, minR, maxR, 1);
            sigmas = new double[dim];
            Arrays.fill(sigmas, 1.0);
        }

        // FIXME ONLY TEMPORARY TO GET RID OF ERRORS
        public Individual(double[] genome_)
        {
            init();
            genome = genome_;
            sigmas = new double[dim];
            Arrays.fill(sigmas, 1.0);
        }

        // Creates individual with given genome and sigma values.
        public Individual(double[] genome_, double[] sigmas_)
        {
            init();
            genome = genome_;
            sigmas = sigmas_;
        }

        // Returns clone of individuals genome.
        public double[] getGenome()
        {
            return genome.clone();
        }

        // Returns clone of individuals sigma values.
        public double[] getSigmas()
        {
            return sigmas.clone();
        }

        // Applies mutation on individual.
        public void mutate(Random rnd)
        {
            double[][] res = Mutation.uncorrelated(
                    getGenome(), getSigmas(), rnd, minR, maxR);
            genome = res[0];
            sigmas = res[1];
        }

        //TODO Don't hardcode pMutate
        public void mutate_normal(Random rnd)
        {
            double factor = 1;
            double pMutate = 0.1;
            for (int i = 0; i < genome.length; i++)
            {
                if (pMutate > rnd.nextDouble())
                {
                    genome[i] = Mutation.normal_(genome[i], rnd, factor, minR, maxR);
                }
            }
        }

        // Rounds value v on n decimals.
        private double round(double v, int n)
        {
            return Math.round(v * Math.pow(10, n)) / Math.pow(10, n);
        }

        public String toString()
        {
            String s = Double.toString(
                    Math.round(fitness*1e3)/1e3)
                    + " [";
            // XXX prints only half for convenience.
            for (int i = 0; i < genome.length/2; i++)
            {
                s += "(" + Double.toString(Math.round(genome[i]*1e3)/1e3) +
                    ", " + Double.toString(Math.round(sigmas[i]*1e3)/1e3) +
                    "), ";
            }
            return s.substring(0, s.length() - 2) + "]";
        }
/*
        public String toString()
        {
            String s = Double.toString(
                    fitness)
                    + " [";
            for (int i = 0; i < genome.length; i++)
            {
                s += "(" + Double.toString(genome[i]) +
                    ", " + Double.toString(sigmas[i]) +
                    "), ";
            }
            return s.substring(0, s.length() - 2) + "]";
        }
*/
        @Override
        public int compareTo(Individual individual)
        {
            return Double.compare(fitness, individual.fitness);
        }

    }

}
