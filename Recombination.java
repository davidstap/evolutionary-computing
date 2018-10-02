import java.util.Random;

public class Recombination
{
    private double[][] parents;
    private double alpha = 0.5;
    private Random rand = new Random();

    private double probability = 0.5;

    public Recombination(double[][] parents)
    {
        this.parents = parents;
    }

    public double[] discreteRecombination()
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

      return child1;
    }


    //TODO: simple arithmetic recombination (p65)
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
                // Verander in 1
                child1[i] = arithmeticFunction(parents[0][i], parents[1][i]);
                child2[i] = arithmeticFunction(parents[0][i], parents[1][i]);
            }

        }
        Children children = new Children(child1, child2);
        return children;
    }

    private double arithmeticFunction(double p1, double p2){
        double z = alpha*p1 + (1- alpha)*p2;
        return z;

    }

    //TODO: arithmetic recombination (p66)
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
        Children children = new Children(child1, child2);

        return children;
    }


    //TODO: whole arithmetic recombination (p66)
    public Children wholeArithmeticRecom()
    {
        double[] child1 = new double[parents[0].length];
        double[] child2 = new double[parents[1].length];

        for (int i=0; i < parents[0].length; i++)
        {
            child1[i] = arithmeticFunction(parents[0][i], parents[1][i]);
            child2[i] = arithmeticFunction(parents[0][i], parents[1][i]);

        }
        Children children = new Children(child1, child2);

        return children;
    }

}

