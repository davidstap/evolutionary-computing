public class Recombination
{
    private double[][] parents;

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
    public double[] simpleArithmetic()
    {
      return parents[0];
    }

    //TODO: arithmetic recombination (p66)
    public double[] arithmeticRecom()
    {
      return parents[0];
    }

    //TODO: whole arithmetic recombination (p66)
    public double[] wholeArithmeticRecom()
    {
      return parents[0];
    }
}
