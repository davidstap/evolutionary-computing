import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class Population
{
    private static final int dim = 10;

    private Population.Individual[] individuals;
    private Function<double[], Object> evaluationFunction;

    public Population()
    {
    }

    public Population(
            int size, Function<double[], Object> evaluationFunction_,
            Random rnd)
    {
        individuals = new Individual[size];
        for (int i = 0; i < size; i++)
        {
            individuals[i] = this.new Individual(rnd);
        }
        evaluationFunction = evaluationFunction_;
    }

    public Population(
            Individual[] parents, int[] N,
            Function<double[], Object> evaluationFunction_, Random rnd)
    {
        int i = 0;
        for (int n : N)
        {
            i += n;
        }
        individuals = new Individual[i];
        
        i = 0;
        for (int j = 0; j < N.length; j++)
        {
            for (int k = 0; k < N[j]; k++)
            {
                individuals[i] = this.new Individual(parents[j].getGenome());
                i++;
            }
        }
        
        evaluationFunction = evaluationFunction_;
    }

    public Individual[] getIndividuals()
    {
        return individuals.clone();
    }

    public int evaluate(int evals, int evaluations_limit)
    {
        for (int i = 0; i < individuals.length; i++)
        {
            if (evals >= evaluations_limit)
            {
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
            individuals[i].fitness = (double) evaluationFunction.apply(
                    individuals[i].getGenome());
            evals++;
        }
        return evals;
    }

    // Selects n best individuals as parents
    public Individual[] parentSelectionGreedy(int n)
    {
        sort();
        Individual[] parents = new Individual[n];

        for (int i=0; i<n; i++)
        {
          parents[i] = individuals[i];
        }
        return parents;
    }
    
    // Selects n parents based on Roulette Wheel and ranking selection
    public Individual[] parentSelectionRouletteWheel(int n, double s)
    {
      sort();
      setRanks();
      setSelectionRank(s); 
      
      Individual[] parents = new Individual[n];
      
      Random rnd = new Random();
      int currentMember = 0;
      
      for (int j=0; j<n; j++)
      {
        double r = rnd.nextDouble();
        int i = 0;
        double a_i = individuals[0].selectionRanking;
        // stop when cumulative probability exceeds r, then use i for individual.
        while (a_i < r)
        {
          i+=1;
          // Update cumulative probability distribution
          a_i += individuals[i].selectionRanking;
        }
        parents[j] = individuals[i];
      }
      return parents;
    }
    
    // Adds to every member of population the correct rank (based on fitness score)
    // lowest rank = 0, highest rank = mu-1
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
    
    // Adds correct value for selectionRank to every individual in population
    private void setSelectionRank(double s)
    {
      for (int i=0; i<individuals.length; i++)
      {
        // formula for selectionRank (see book p82)
        double mu = (double) individuals.length;
        double sr = (2.0-s)/(mu) + (2.0*individuals[i].rank*(s-1))/(mu*(mu-1.0));
        // System.out.println(sr);
        individuals[i].selectionRanking = sr;
      }
    }

    public void mutate(Random rnd, double factor)
    {
        for (Individual individual : individuals)
        {
            individual.mutate(1.0, rnd, factor);
        }
    }

    public void survival(Population childPopulation)
    {
        Individual[] childIndividuals = childPopulation.getIndividuals();
        for (int i = 0; i < childIndividuals.length; i++)
        {
            individuals[i] = childIndividuals[i];
        }
    }

    public double getMaxFitness()
    {
        if (individuals.length > 0)
        {
            sort();
            return individuals[0].fitness;
        }
        return 0.0;
    }

    public void sort()
    {
        Arrays.sort(individuals, (u1, u2) -> Double.compare(u2.fitness, u1.fitness));
    }

    public int size()
    {
        return individuals.length;
    }

    public void print()
    {
//        System.out.println("--PRINTING POPULATION--");
        for (int i = 0; i < individuals.length; i++)
        {
            System.out.println(individuals[i]);
        }
    }

    public class Individual implements Comparable<Individual>
    {

        public static final double minR = -5.0;
        public static final double maxR = 5.0;
        public static final double R = maxR - minR;

        private double[] genome;
//        private double[] sigmas;
        public double fitness;
        
        // Used for ranking selection (see p81 book)
        public double selectionRanking;
        // ranking of individual. Note: worst rank = 0, best rank = mu-1
        public int rank;
        

        private void init()
        {
            fitness = 0.0;
            selectionRanking = 0.0;
            rank = 0;
        }

        public Individual(Random rnd)
        {
            init();
            genome = Mutation.normal(new double[dim], rnd, R, minR, maxR, 1);
        }

        public Individual(double[] genome_)
        {
            init();
            genome = genome_;
        }

        public double[] getGenome()
        {
            return genome.clone();
        }

        public void mutate(double pMutate, Random rnd, double factor)
        {
            for (int i = 0; i < genome.length; i++)
            {
                if (pMutate > rnd.nextDouble()) {
                    genome[i] = Mutation.normal_(
                            genome[i], rnd, factor, minR, maxR);
                }
            }
        }

        public String toString()
        {
            String s = Double.toString(
                    Math.round(fitness * 1e3) / 1e3) + " [";
            for (int i = 0; i < genome.length; i++)
            {
                s += Double.toString(
                        Math.round(genome[i] * 1e2) / 1e2) + ", ";
            }
            return s.substring(0, s.length() - 2) + "]";
        }

        @Override
        public int compareTo(Individual individual)
        {
            return Double.compare(fitness, individual.fitness);
        }
    }
}
