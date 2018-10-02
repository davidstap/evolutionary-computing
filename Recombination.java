import java.util.Random;

public class Recombination
{
    private double[][] parents;
    private double alpha = 0.5;
    private Random rand = new Random();

    private double probability = 0.5;

    public Recombination(Population.Unit[] parents)
    {
        double[][] units = new double[parents.length][parents[0].getGenome().length];
        int i = 0;
        for (Population.Unit parent : parents)
        {
            units[i] = parent.getGenome();
            i++;
        }
        this.parents = units;
    }

    public Children discreteRecombination()
    {
      double[] child1 = new double[parents[0].length];
      double[] child2 = new double[parents[0].length];

      for (int i=0; i<parents[0].length; i++)
      {
        double random = Math.random();
        if (random > 0.5)
        {
          child1[i] = parents[0][i];
          child2[i] = parents[1][i];
        }
        else
        {
          child1[i] = parents[1][i];
          child2[i] = parents[0][i];
        }
      }

      return new Children(child1, child2);
    }


    public Children simpleArithmetic()
    {
        double[] child1 = new double[parents[0].length];
        double[] child2 = new double[parents[1].length];

        Random rand = new Random();
        int k = rand.nextInt(10);
        for (int i=0; i < parents[0].length; i++)
        {
            if (i < k)
            {
                child1[i] = parents[0][i];
                child2[i] = parents[1][i];
            }
            else
            {
                child1[i] = arithmeticFunction(parents[0][i], parents[1][i]);
                child2[i] = arithmeticFunction(parents[0][i], parents[1][i]);
            }

        }
        return new Children(child1, child2);
    }

    private double arithmeticFunction(double p1, double p2){
        double z = alpha*p1 + (1- alpha)*p2;
        return z;

    }

    public Children singleArithmeticRecom()
    {   
        double[] child1 = new double[parents[0].length];
        double[] child2 = new double[parents[1].length];
        int k = rand.nextInt(10);
        for (int i=0; i < parents[0].length; i++)
        {
            double prob = rand.nextDouble();
            if (i == k)
            {
                child1[i] = arithmeticFunction(parents[0][i], parents[1][i]);
                child2[i] = arithmeticFunction(parents[0][i], parents[1][i]);
            }
            else if (prob > probability)
            {
                child1[i] = parents[0][i];
                child2[i] = parents[1][i];
            }
            else
            {
                child1[i] = parents[1][i];
                child2[i] = parents[0][i];
            }

        }
        return new Children(child1, child2);
    }


    public Children wholeArithmeticRecom()
    {
        double[] child1 = new double[parents[0].length];
        double[] child2 = new double[parents[1].length];

        for (int i=0; i < parents[0].length; i++)
        {
            child1[i] = arithmeticFunction(parents[0][i], parents[1][i]);
            child2[i] = arithmeticFunction(parents[0][i], parents[1][i]);

        }
        return new Children(child1, child2);
    }

}

