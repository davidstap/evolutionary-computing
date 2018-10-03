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

    public Individual[] selectParents(int n )
    {
        sort();
        Individual[] parents = new Individual[n];

        for (int i=0; i<n; i++)
        {
          parents[i] = individuals[i];
        }
        return parents;
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

        private void init()
        {
            fitness = 0.0;
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
