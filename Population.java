import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class Population
{
    private static final int dim = 10;

    private Population.Unit[] units;
    private Function<double[], Object> evaluationFunction;

    public Population()
    {
    }

    public Population(
            int size, Function<double[], Object> evaluationFunction_,
            Random rnd)
    {
        units = new Unit[size];
        for (int i = 0; i < size; i++)
        {
            units[i] = this.new Unit(rnd);
        }
        evaluationFunction = evaluationFunction_;
    }

    public Population(
            Unit[] parents, int[] N,
            Function<double[], Object> evaluationFunction_, Random rnd)
    {
        int i = 0;
        for (int n : N)
        {
            i += n;
        }
        units = new Unit[i];
        
        i = 0;
        for (int j = 0; j < N.length; j++)
        {
            for (int k = 0; k < N[j]; k++)
            {
                units[i] = this.new Unit(parents[j].getGenome());
                i++;
            }
        }
        
        evaluationFunction = evaluationFunction_;
    }

    public Unit[] getUnits()
    {
        return units.clone();
    }

    public int evaluate(int evals, int evaluations_limit)
    {
        for (int i = 0; i < units.length; i++)
        {
            if (evals >= evaluations_limit)
            {
                if (i == 0)
                {
                    units = new Unit[0];
                }
                else
                {
                    units = Arrays.copyOfRange(units, 0, i);
                }
                break;
            }
            units[i].fitness = (double) evaluationFunction.apply(
                    units[i].getGenome());
            evals++;
        }
        return evals;
    }

    public Unit[] selectParents(int n )
    {
        sort();
        Unit[] parents = new Unit[n];

        for (int i=0; i<n; i++)
        {
          parents[i] = units[i];
        }
        return parents;
    }

    public void mutate(Random rnd, double factor)
    {
        for (Unit unit : units)
        {
            unit.mutate(1.0, rnd, factor);
        }
    }

    public void survival(Population childPopulation)
    {
        Unit[] childUnits = childPopulation.getUnits();
        for (int i = 0; i < childUnits.length; i++)
        {
            units[i] = childUnits[i];
        }
    }

    public double getMaxFitness()
    {
        if (units.length > 0)
        {
            sort();
            return units[0].fitness;
        }
        return 0.0;
    }

    public void sort()
    {
        Arrays.sort(units, (u1, u2) -> Double.compare(u2.fitness, u1.fitness));
    }

    public int size()
    {
        return units.length;
    }

    public void print()
    {
//        System.out.println("--PRINTING POPULATION--");
        for (int i = 0; i < units.length; i++)
        {
            System.out.println(units[i]);
        }
    }

    public class Unit implements Comparable<Unit>
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

        public Unit(Random rnd)
        {
            init();
            genome = Mutation.normal(new double[dim], rnd, R, minR, maxR, 1);
        }

        public Unit(double[] genome_)
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
        public int compareTo(Unit unit)
        {
            return Double.compare(fitness, unit.fitness);
        }
    }
}
