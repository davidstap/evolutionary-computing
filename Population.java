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

    public int evaluate(int evals, int evaluations_limit)
    {
        for (Unit unit : units)
        {
            if (evals >= evaluations_limit) { break; }
            if (unit.evaluated) { continue; }
            unit.fitness = (double) evaluationFunction.apply(unit.getGenome());
            unit.evaluated = true;
            evals++;
        }
        return evals;
    }

    public double[][] selectParents(int n )
    {
        sort();


        double[][] parents = new double[n][units[0].getGenome().length];

        for (int i=0; i<n; i++)
        {
          parents[i] = units[units.length-1-i].getGenome();
        }
        return parents;


        // return new double[][] { units[units.length - 1].getGenome(), units[units.length - 2].getGenome() };


    }

    public Unit[] selectParent()
    {
        sort();
        return new Unit[] { units[units.length - 1] };
    }

    public void evolve(Unit unit, Random rnd, double factor)
    {
        for (int i = 0; i < units.length; i++)
        {
            if (units[i] != unit) {
                units[i] = new Unit(unit.mutate(rnd, factor));
            }
        }
    }

    public double getMaxFitness()
    {
        sort();
        if (units[0].evaluated) {
            return units[0].fitness;
        }
        else {
            return 0.0;
        }
    }

    public void sort()
    {
        Arrays.sort(units);
    }

    public void print()
    {
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

        private final double[] genome;
        public double fitness;
        public boolean evaluated;

        private void init()
        {
            fitness = 0.0;
            evaluated = false;
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

        public double[] mutate(Random rnd, double factor)
        {
            return Mutation.normal(getGenome(), rnd, factor, minR, maxR);
        }

        public String toString()
        {
            String s = "";
            if (evaluated) { s += "o "; } else { s += "x "; }
            s += Double.toString(
                    Math.round(fitness * 10) / 10.0) + " [";
            for (int i = 0; i < genome.length; i++)
            {
                s += Double.toString(
                        Math.round(genome[i] * 10) / 10.0) + ", ";
            }
            return s.substring(0, s.length() - 2) + "]";
        }

        @Override
        public int compareTo(Unit unit)
        {
            if (!evaluated) {
                return -1;
            }
            else if (!unit.evaluated) {
                return 1;
            }
            else {
                return Double.compare(fitness, unit.fitness);
            }
        }
    }
}
