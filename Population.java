import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class Population
{

    private static final int dim = 10;

    private Population.Unit[] units;
    private Function<double[], Object> evaluationFunction;

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
                units[i] = this.new Unit(
                        parents[j].getGenome(), parents[j].getSigmas());
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

    public double[][] selectParents(int n )
    {
        sort();
        double[][] parents = new double[n][dim];
        for (int i = 0; i < n; i++)
        {
          parents[i] = units[i].getGenome();
        }
        return parents;
    }

    public Unit[] selectParent()
    {
        sort();
        return new Unit[] { units[0] };
    }

    public void mutate(Random rnd)
    {
        for (Unit unit : units)
        {
            unit.mutate(rnd);
        }
    }

    public void survival_mu_plus_lambda(Population childPopulation)
    {
        Unit[] parentUnits = getUnits();
        Unit[] childUnits = childPopulation.getUnits();
        
        units = new Unit[parentUnits.length + childUnits.length];
        for (int i = 0; i < parentUnits.length; i++)
        {
            units[i] = parentUnits[i];
        }
        for (int i = 0; i < childUnits.length; i++)
        {
            units[parentUnits.length + i] = childUnits[i];
        }
        sort();
        
        units = Arrays.copyOfRange(units, 0, parentUnits.length);
    }

    public void survival(Population childPopulation)
    {
        survival_mu_plus_lambda(childPopulation);
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
        System.out.println();
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
        private double[] sigmas;
        public double fitness;

        public Unit(Random rnd)
        {
            fitness = 0.0;
            genome = Mutation.normal(new double[dim], rnd, R, minR, maxR, 1);
            sigmas = new double[dim];
            Arrays.fill(sigmas, 1.0);
        }

        public Unit(double[] genome_, double[] sigmas_)
        {
            fitness = 0.0;
            genome = genome_;
            sigmas = sigmas_;
        }

        public double[] getGenome()
        {
            return genome.clone();
        }

        public double[] getSigmas()
        {
            return sigmas.clone();
        }

        public void mutate(Random rnd)
        {
            double[][] res = Mutation.uncorrelated(
                    getGenome(), getSigmas(), rnd, minR, maxR);
            genome = res[0];
            sigmas = res[1];
        }

        private double round(double v, int n)
        {
            return Math.round(v * Math.pow(10, n)) / Math.pow(10, n);
        }

        public String toString()
        {
            String s = Double.toString(
                    fitness)
                    + " [";
            for (int i = 0; i < genome.length/2; i++)
            {
                s += "(" + Double.toString(genome[i]) +
                    ", " + Double.toString(sigmas[i]) +
                    "), ";
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
