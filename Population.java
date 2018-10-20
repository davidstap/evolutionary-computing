import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Random;
import java.util.function.Function;
import java.util.HashMap;

public class Population
{

    public enum PARAM {
        SIZE, NCHILDREN
    }

    // Genome length.
    public static int dim = 10;

    protected Population.Individual[] individuals;
    private Function<double[], Object> evaluationFunction;

    /**************************************************************************
        Constructors
    **************************************************************************/

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

    /**************************************************************************
        ???
    **************************************************************************/

    // TODO move to Selection.java.
    // Selects n parents based on Roulette Wheel and ranking selection.
    public Individual[] parentSelectionRouletteWheel(int n, double s)
    {
        sort();
        // start at highest rank
        int currentRank = individuals.length - 1;
        // Adds to every member of population the correct rank
        // (based on fitness score).
        // Lowest rank = 0
        // Highest rank = mu-1
        // assumption: population is sorted before setRanks() is called
        for (int i=0; i<individuals.length; i++)
        {
            individuals[i].rank = currentRank;
            currentRank -= 1;
        }
        // Adds correct value for selectionRank to every individual in population.
        for (int i=0; i<individuals.length; i++)
        {
            // formula for selectionRank (see book p82)
            double mu = (double) individuals.length;
            double sr = (2.0-s)/(mu) + (2.0*individuals[i].rank*(s-1)) /
                    (mu*(mu-1.0));
            // System.out.println(sr);
            individuals[i].selectionRanking = sr;
        }
        
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
    // Select parents based on the tournamenet selection method found on pg 84-85
    public Individual[] selectParentsTournament(int n, int k, int c)
    {
        sort();
        Individual[] parents = new Individual[n];

        // Numbers list for random sampling
        List<Integer> numbers = new ArrayList<Integer>();
        for(int i = 0; i < n; i++)
        {
            numbers.add(i);
        }
        
        // Compute mean and sd for proportional fitness function
        double mean = 0.0;
        for (int i = 0; i < individuals.length; i++)
        {
            mean += individuals[i].get_fitness();
        }
        mean /= individuals.length;
        double sd = 0.0;
        for (int i = 0; i < individuals.length; i++)
        {
            sd += Math.pow(individuals[i].get_fitness() - mean, 2);
        }
        sd = Math.sqrt(sd/individuals.length);
        for (int i = 0; i < individuals.length; i++)
        {
            // Function found in book pg: 81.
            double prop_fitness = individuals[i].get_fitness() - (mean - c*sd);
            // Assign prop fitness to individual
            individuals[i].prop_fitness = Math.max(0, prop_fitness);
        }
        
        // Repeat tournament selection for amount of parents needed.
        for (int i=0; i<n; i++)
        {
            // Create candidates for tournament by random sampling k candidates with replacement.
            Individual[] candidates = new Individual[k];
            // Shuffle to get random numbers
            Collections.shuffle(numbers);
            for (int j = 0; j < k; j++)
            {
                // Get random individual
                candidates[j] = individuals[numbers.get(j)];
            }
            // Sort highest to lowest based on prop fitness.
            Arrays.sort(candidates, (u1, u2) -> Double.compare(
                    u2.prop_fitness, u1.prop_fitness));
            //  Assign best candidate to parents
            parents[i] = candidates[0];
        }
        return parents;
    }

    /**************************************************************************
        Main evolution steps
    **************************************************************************/

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
            individuals[i].set_fitness((double) evaluationFunction.apply(
                    individuals[i].getGenome()));
            evals++;
        }
        return evals;
    }

    public Individual[] parentSelection(Random rnd,
            Selection.TYPE selectionType,
            HashMap<String, Double> selectionParams)
    {
//        TODO tournament exceeds population size / parameters not hardcoded!!!
        Individual[] selection;
        switch(selectionType)
        {
            case UNIFORM:
                selection = Selection.uniform(
                        this.getIndividuals(), rnd, selectionParams);
                break;
            case GREEDY:
                selection = Selection.greedy(
                        this.getIndividuals(), selectionParams);
                break;
            case ROUNDROBIN:
                selection = Selection.roundRobin(
                        this.getIndividuals(), rnd, selectionParams);
                break;
            // FIXME move to Selection.java.
            case ROULETTE:
                String param1 = Selection.PARAM.PARENT_K.toString();
                int k1 = selectionParams.containsKey(param1) ?
                        selectionParams.get(param1).intValue() :
                        individuals.length;
                double S = 2.0;
                selection = parentSelectionRouletteWheel(k1, S);
                break;
            // FIXME move to Selection.java.
            case TOURNAMENT:
                /*
                String param2 = Selection.PARAM.PARENT_K.toString();
                int k2 = selectionParams.containsKey(param2) ?
                        selectionParams.get(param2).intValue() :
                        individuals.length;
                int the_real_k = 6;
                int c = 2;
                selection = selectParentsTournament(k2, the_real_k, c);
                */
                selection = Selection.tournament(
                        this.getIndividuals(), rnd, selectionParams);
                break;
            default:
                throw new IllegalArgumentException("\n\t" +
                        Population.class.getName() +
                        ": parent selection type not recognized\n");
        }
        /* TODO create better selection for nParents < nChildren
        String str = Selection.PARAM.PARENT_K.toString();
        int nChildren = selectionParams.containsKey(str) ?
                        selectionParams.get(str).intValue() :
                        individuals.length;
        if (selection.length <= nChildren)
        {
            selection = Selection.individualsAND(
                    selection, Selection.uniform(
                    selection, rnd, selectionParams));
        }
        */
        return selection;
    }

    public Individual[] recombination(Individual[] individuals, Random rnd,
            Recombination.TYPE recombinationType,
            HashMap<String, Double> params)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        return Recombination.recombination(
                individuals, rnd, recombinationType, params);
    }

    // Calls mutation with default parameters.
    public void mutate(Random rnd, Mutation.TYPE mutationType)
    {
        mutate(rnd, mutationType, new HashMap<String, Double>());
    }

    // Mutates all individuals in the population.
    public void mutate(Random rnd, Mutation.TYPE mutationType,
            HashMap<String, Double> params)
    {
        for (Individual individual : individuals)
        {
            individual.mutate(rnd, mutationType, params);
        }
    }

    // Applies survival selection onto population.
    public void survival(Population childPopulation, Random rnd,
            Selection.TYPE selectionType,
            HashMap<String, Double> selectionParams)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        Individual elite = Selection.getElitism(this.getIndividuals());
        Individual[] selection;
        switch(selectionType) {
            case GENITOR:
                selection = Selection.genitor(
                    this.getIndividuals(), childPopulation.getIndividuals());
                break;
            case ROUNDROBIN:
                selection = Selection.survivalRoundRobin(
                    this.getIndividuals(), childPopulation.getIndividuals(),
                    rnd, selectionParams);
                break;
            case MUPLUSLAMBDA:
                selection = Selection.mu_plus_lambda(
                    this.getIndividuals(), childPopulation.getIndividuals());
                break;
            case MUCOMMALAMBDA:
                selection = Selection.mu_comma_lambda(
                    this.getIndividuals(), childPopulation.getIndividuals());
                break;
            case TOURNAMENT:
                selection = Selection.tournament(
                        this.getIndividuals(), childPopulation.getIndividuals(),
                        rnd, selectionParams);
                break;
            default:
                throw new IllegalArgumentException("\n\t" +
                        Population.class.getName() +
                        ": survival selection type not recognized\n");
        }
        individuals = Selection.applyElitism(elite, selection);
    }

    /**************************************************************************
        Sorting
    **************************************************************************/

    // Sorts list of individuals from largest fitness value to smallest.
    public static Individual[] sort(Individual[] individuals_)
    {
        Arrays.sort(individuals_, (i1, i2) -> Double.compare(
                i2.get_fitness(), i1.get_fitness()));
        return individuals_;
    }

    // Sorts list of individuals from smallest fitness value to largest.
    public static Individual[] reverseSort(Individual[] individuals_)
    {
        Arrays.sort(individuals_, (i1, i2) -> Double.compare(
                i1.get_fitness(), i2.get_fitness()));
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

    /**************************************************************************
        Other
    **************************************************************************/

    // Returns copied list of individuals in population.
    public Individual[] getIndividuals()
    {
        return individuals.clone();
    }

    // Returns maximal fitness value found in the population.
    public double getMaxFitness()
    {
        if (individuals.length > 0)
        {
            sort();
            return individuals[0].get_fitness();
        }
        return 0.0;
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

    public void setSharedFitness(double sigma, double alpha)
    {
        setSharedFitness(individuals, sigma, alpha);
    }

    public static void setSharedFitness(
            Individual[] individuals_, double sigma, double alpha)
    {
        for (Individual individual : individuals_)
        {
            individual.setSharedFitness(individuals_, sigma, alpha);
        }
    }

    // Subclass to contain one individual of the population.
    public static class Individual implements Comparable<Individual>
    {

        // Ranges in which genome values fall.
        public double[] minR;
        public double[] maxR;

        protected double[] genome;
        protected double[] sigmas;
        protected double fitness;
        protected double shared_fitness;
        public double prop_fitness;
        
        // Used for ranking selection (see p81 book)
        public double selectionRanking;
        // ranking of individual. Note: worst rank = 0, best rank = mu-1
        public int rank;

        /**********************************************************************
            Constructors
        **********************************************************************/

        // Basic setting of default variables.
        public Individual()
        {
            minR = new double[dim];
            maxR = new double[dim];
            Arrays.fill(minR, -5.0);
            Arrays.fill(maxR, 5.0);
            
            genome = new double[dim];
            sigmas = new double[dim];
            Arrays.fill(sigmas, 1.0);
            
            set_fitness(0.0);
            selectionRanking = 0.0;
            prop_fitness =0.0;
            rank = 0;
        }
        // Creates individual with random genome and default sigma values
        // (filled with 1's).
        public Individual(Random rnd)
        {
            this();
            for (int i = 0; i < genome.length; i++)
            {
                genome[i] = (minR[i] + maxR[i]) / 2.0 +
                        (rnd.nextDouble() - 0.5) * (maxR[i] - minR[i]);
            }
            /* XXX not using normal any more
            genome = Mutation.normal(
                    new double[dim], rnd, maxR[0] - minR[0],
                    minR[0], maxR[0], 1);
            */
        }

        // Creates individual with given genome values.
        public Individual(double[] genome_)
        {
            this();
            genome = genome_;
        }

        // Creates individual with given genome and sigma values.
        public Individual(double[] genome_, double[] sigmas_)
        {
            this();
            genome = genome_;
            sigmas = sigmas_;
        }

        /**********************************************************************
            Mutation
        **********************************************************************/

        public void mutate(Random rnd, Mutation.TYPE mutationType,
                HashMap<String, Double> params)
                throws IllegalArgumentException
        {
            switch(mutationType)
            {
            
                case GAUSSIAN:
                    genome = Mutation.gaussian(
                            getGenome(), rnd, minR, maxR, params);
                    break;
                case UNCORRELATED:
                    double[][] res = Mutation.uncorrelated(
                            getGenome(), getSigmas(), rnd, minR, maxR, params);
                    genome = res[0];
                    sigmas = res[1];
                    break;
                default:
                    throw new IllegalArgumentException("\n\t" +
                            Population.class.getName() +
                            ": mutation type not recognized\n");
            }
        }

        /**********************************************************************
            Other
        **********************************************************************/

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

        // Rounds value v on n decimals.
        private double round(double v, int n)
        {
            return Math.round(v * Math.pow(10, n)) / Math.pow(10, n);
        }

        // Sets fitness value using relative genome distances.
        // XXX Seems to have no effect on outcomes.
        public void setSharedFitness(
                Individual[] individuals_, double sigma, double alpha)
        {
            double sum = 0.0;
            for (Individual individual : individuals_)
            {
                double[] genome_ = individual.getGenome();
                double d = 0.0;
                for (int i = 0; i < genome.length; i++)
                {
                    d += genome[i] - genome_[i];
                }
                d = Math.sqrt(Math.abs(sum));
                if (d <= sigma)
                {
                    sum += 1 - Math.pow((d / sigma), alpha);
                }
            }
            shared_fitness = fitness / sum;
        }

        public void set_fitness(double value, double shared_value)
        {
            fitness = value;
            shared_fitness = shared_value;
        }

        public void set_fitness(double value)
        {
            fitness = value;
            shared_fitness = value;
        }

        public double get_fitness()
        {
            return shared_fitness;
        }

        @Override
        public int compareTo(Individual individual)
        {
            return Double.compare(get_fitness(), individual.get_fitness());
        }





/*
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
*/



        public String toString()
        {
            String s = Double.toString(
                    get_fitness())
                    + " [";
            for (int i = 0; i < genome.length; i++)
            {
                s += "(" + Double.toString(genome[i]) +
                    ", " + Double.toString(sigmas[i]) +
                    "), ";
            }
            return s.substring(0, s.length() - 2) + "]";
        }




    }

}
