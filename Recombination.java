import java.util.Random;

public class Recombination
{
    private double[][] parents;
    private double alpha = 0.5;
    private Random rand = new Random();

    private double probability = 0.5;

    private Population.Individual[] children;

    private Population childPop;

    public Recombination(Population.Individual[] parents)
    {
        if ((parents.length & 1) == 0)
        {
            double[][] individuals = new double[parents.length][parents[0].getGenome().length];
            int i = 0;
            for (Population.Individual parent : parents)
            {
                individuals[i] = parent.getGenome();
                i++;
            }
            this.parents = individuals;
            this.children = new Population.Individual[parents.length];
            this.childPop = new Population();
        }
        else 
        {
            System.out.println("Parents length must be an even number!");
        }

    }


    public Population.Individual[] discreteRecombination()
    {
      // Mutate and add to children
      double[][] childGenomes = new double[parents.length][parents[0].length];
      
      // Parent loop
      for (int j=0; j<parents.length; j+=2)
      {
        // Genome loop
        for (int i=0; i<parents[0].length; i++)
        {
          double random = Math.random();
          if (random > 0.5)
          {
            childGenomes[j][i] = parents[j][i];
            childGenomes[j+1][i] = parents[j+1][i];
          }
          else
          {
            childGenomes[j][i] = parents[j+1][i];
            childGenomes[j+1][i] = parents[j][i];
          }
        }
        // Add to children Individual
        children[j] = childPop.new Individual(childGenomes[j]);
        children[j+1] = childPop.new Individual(childGenomes[j+1]);
      }
      return children;
    }



    public Population.Individual[] simpleArithmetic()
    {
        double[][] childGenomes = new double[parents.length][parents[0].length];

        // Get random number
        Random rand = new Random();
        int k = rand.nextInt(10);

        // Parent loop
        for (int j=0; j < parents.length; j+=2)
        {
            // Genome loop
            for (int i=0; i < parents[0].length; i++)
            {
                if (i < k)
                {
                    childGenomes[j][i] = parents[j][i];
                    childGenomes[j+1][i] = parents[j+1][i];
                }
                else
                {
                    childGenomes[j][i] = arithmeticFunction(parents[j][i], parents[j+1][i]);
                    childGenomes[j+1][i] = arithmeticFunction(parents[j][i], parents[j+1][i]);
                }

            }
            // Add to children Individual
            children[j] = childPop.new Individual(childGenomes[j]);
            children[j+1] = childPop.new Individual(childGenomes[j+1]);
        }

        return children;
    }

    private double arithmeticFunction(double p1, double p2){
        double z = alpha*p1 + (1- alpha)*p2;
        return z;

    }

    public Population.Individual[] singleArithmeticRecom()
    {   
        // Mutate and add to children
        double[][] childGenomes = new double[parents.length][parents[0].length];

        // Get random number        
        int k = rand.nextInt(10);

        // Parent loop
        for (int j=0; j < parents.length; j+=2)
        {
            // Genome loop
            for (int i=0; i < parents[0].length; i++)
            {
                double prob = rand.nextDouble();
                if (i == k)
                {
                    childGenomes[j][i] = arithmeticFunction(parents[j][i], parents[j+1][i]);
                    childGenomes[j+1][i] = arithmeticFunction(parents[j][i], parents[j+1][i]);
                }
                else if (prob > probability)
                {
                    childGenomes[j][i] = parents[j][i];
                    childGenomes[j+1][i] = parents[j+1][i];
                }
                else
                {
                    childGenomes[j][i] = parents[j+1][i];
                    childGenomes[j+1][i] = parents[j][i];
                }

            }
            // Add to children Individual
            children[j] = childPop.new Individual(childGenomes[j]);
            children[j+1] = childPop.new Individual(childGenomes[j+1]);
        }
        return children;
    }


    public Population.Individual[] wholeArithmeticRecom()
    {
        // Mutate and add to children
        double[][] childGenomes = new double[parents.length][parents[0].length];

        // Parent loop
        for (int j=0; j < parents.length; j+=2)
        {
            // Genome loop
            for (int i=0; i < parents[0].length; i++)
            {
                childGenomes[j][i] = arithmeticFunction(parents[j][i], parents[j+1][i]);
                childGenomes[j+1][i] = arithmeticFunction(parents[j][i], parents[j+1][i]);

            }
            // Add to children Individual
            children[j] = childPop.new Individual(childGenomes[j]);
            children[j+1] = childPop.new Individual(childGenomes[j+1]);
        }
        return children;
    }

}

