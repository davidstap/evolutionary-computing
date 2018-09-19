import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class Population
{

    private static final int dim = 10;

    private Population.Unit[] units;
    private Function<double[], Object> evaluationFunction;

    public Population(int size, Function<double[], Object> evaluationFunction_)
    {
        units = new Unit[size];
        for (int i = 0; i < size; i++)
        {
            units[i] = this.new Unit();
        }
        evaluationFunction = evaluationFunction_;
    }

    public void init(Random rnd)
    {
        for (Unit unit : units)
        {
            unit.init(rnd);
        }
    }

    public int evaluate()
    {
        for (Unit unit : units)
        {
            unit.fitness = (double) evaluationFunction.apply(unit.getGenome());
        }
        return units.length;
    }

    public double[][] selectParents()
    {
        sort();
        return new double[][]{ units[units.length - 1].getGenome() };
    }

    public void evolve(double[] genome, Random rnd, double factor)
    {
        for (int i = 0; i < units.length; i++)
        {
            units[i].setGenome(genome);
            if (i != 0) {
                units[i].mutate(rnd, factor);
            }
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

    private class Unit implements Comparable<Unit>
    {
    
        public static final double minR = -5.0;
        public static final double maxR = 5.0;
        public static final double R = maxR - minR;
    
        private double[] genome;
        public double fitness;
    
        public Unit()
        {
            genome = new double[dim];
            for (int i = 0; i < dim; i++)
            {
                genome[i] = 0.0;
            }
            fitness = 0.0;
        }
    
        public double[] getGenome()
        {
            return genome.clone();
        }
    
        public void setGenome(double[] genome_)
        {
            genome = genome_.clone();
        }
    
        public void init(Random rnd)
        {
            genome = Mutation.normal(genome, rnd, R, minR, maxR, 1);
        }
    
        public void mutate(Random rnd, double factor)
        {
            genome = Mutation.normal(genome, rnd, factor, minR, maxR);
        }
    
        public String toString()
        {
            String s = Double.toString(
                    Math.round(fitness * 100) / 100.0) + " [";
            for (int i = 0; i < genome.length; i++)
            {
                s += Double.toString(
                        Math.round(genome[i] * 100) / 100.0) + ", ";
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
