import java.util.Random;

public class Mutation {

    public static double[] mutate(double[] genome, Random rnd, double factor)
    {
        for (int i = 0; i < genome.length; i++)
        {
            genome[i] += (rnd.nextDouble() - 0.5) * factor;
        }
        return genome;
    }
}
