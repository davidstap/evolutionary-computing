import java.util.Random;

public class Mutation {

    public static double[] normal(
            double[] genome, Random rnd, double factor, double min, double max)
    {
        return normal(genome, rnd, factor, min, max, 8);
    }

    public static double normal_(
            double gene, Random rnd, double factor, double min, double max)
    {
        return normal_(gene, rnd, factor, min, max, 8);
    }

    public static double normal_(
            double gene, Random rnd, double factor,
            double min, double max, int n)
    {
        double r;
        double tmp;
        do {
            r = 0.0;
            tmp = gene;
            for (int i = 0; i < n; i++)
            {
                r += rnd.nextDouble() -0.5;
            }
            tmp += r / n * factor;
        } while (tmp <= min && tmp >= max);
        return tmp;
    }

    public static double[] normal(
            double[] genome, Random rnd, double factor,
            double min, double max, int n)
    {
        double r;
        double tmp;
        for (int i = 0; i < genome.length; i++)
        {
            genome[i] = normal_(genome[i], rnd, factor, min, max, n);
        }
        return genome;
    }

    /*
    s_ = s * e(t_N+tN
    x_=x+s_N
    t_~1/sqrt(2n))
    t~1/sqrt(2sqrt(n))
    etha
    */
    public static double[] uncorrelated(
            )
    {
        return new double[10];
    }

}

