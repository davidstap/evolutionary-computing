import java.util.Random;

public class Mutation {

    public static double[] normal(
            double[] genome, Random rnd, double factor, double min, double max)
    {
        return normal(genome, rnd, factor, min, max, 8);
    }

    public static double[] normal(
            double[] genome, Random rnd, double factor,
            double min, double max, int n)
    {
        double r;
        double tmp;
        for (int i = 0; i < genome.length; i++)
        {
            do {
                r = 0.0;
                tmp = genome[i];
                for (int j = 0; j < n; j++)
                {
                    r += rnd.nextDouble() -.5;
                }
                tmp += r / n * factor;
            } while (tmp <= min && tmp >= max);
            genome[i] = tmp;
        }
        return genome;
    }

}
