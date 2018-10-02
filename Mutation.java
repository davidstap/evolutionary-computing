import java.util.Random;

public class Mutation {

    public static double normal_(
            double gene, Random rnd, double factor, double min, double max)
    {
        return normal_(gene, rnd, factor, min, max, 8);
    }

    public static double[] normal(
            double[] genome, Random rnd, double factor, double min, double max)
    {
        return normal(genome, rnd, factor, min, max, 8);
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
        } while (tmp <= min || tmp >= max);
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

    public static double N(Random rnd, double m, double s)
    {
        return m + rnd.nextGaussian() * s;
    }

    public static double Nr(
        Random rnd, double m, double s, double rl, double ru)
    {
        double n;
        do
        {
            n = N(rnd, m, s);
        }
        while (n < rl || n > ru);
        return n;
    }

    /*
    s_  =  s * e(t_N+tN)
    x_  =  x+s_N
    t_ =~= 1/sqrt(2n)
    t  =~= 1/sqrt(2sqrt(n))
    sigma < etha  -->  sigma = etha
    */
    
    public static double[][] uncorrelated(
            double[] genome, double[] sigmas, Random rnd,
            double lowerBound, double upperBound)
    {
        final int n = genome.length;
        final double p = 1.0 / n;
        final double theta = 1.0 / Math.sqrt(2 * Math.sqrt(n));
        final double theta_ = 1.0 / Math.sqrt(2 * n);
        final double etha = 0.01;
        
        for (int i = 0; i < n; i++)
        {
            if (p > rnd.nextDouble())
            {
                double sValue, gValue;
                do
                {
                sValue = sigmas[i] * Math.pow(
                        Math.E, theta_ * N(rnd, 0, 1) + theta * N(rnd, 0, 1));
                if (sValue < etha)
                {
                    sValue = etha;
                }
                gValue = genome[i] + sValue * N(rnd, 0, 1);
                }
                while (gValue < lowerBound || gValue > upperBound);
                sigmas[i] = sValue;
                genome[i] = gValue;
            }
        }
        return new double[][] { genome, sigmas };
    }

}

