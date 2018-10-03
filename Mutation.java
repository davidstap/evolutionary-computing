import java.util.Random;

public class Mutation {

    // Applies mutation to a single gene using given random number generator.
    // Adds random value to gene multiplied with given factor,
    // value is created using default (8) randomly generated numbers.
    // Resulting gene is kept inside given (min<->max) range.
    public static double normal_(
            double gene, Random rnd, double factor, double min, double max)
    {
        return normal_(gene, rnd, factor, min, max, 8);
    }

    // Applies mutation to genome using given random number generator.
    // Adds random values to genes multiplied with given factor,
    // value is created using default (8) randomly generated numbers.
    // Resulting genes are kept inside given (min<->max) range.
    public static double[] normal(
            double[] genome, Random rnd, double factor, double min, double max)
    {
        return normal(genome, rnd, factor, min, max, 8);
    }

    // Applies mutation to a single gene using given random number generator.
    // Adds random value to gene multiplied with given factor,
    // value is created using n randomly generated numbers.
    // Resulting gene is kept inside given (min<->max) range.
    public static double normal_(
            double gene, Random rnd, double factor,
            double min, double max, int n)
    {
        double r;
        double tmp;
        do {
            r = 0.0;
            tmp = gene;
            // Create random change in gene.
            for (int i = 0; i < n; i++)
            {
                r += rnd.nextDouble() -0.5;
            }
            // Apply change.
            tmp += r / n * factor;
        }
        // Keep resulting gene in given range.
        while (tmp <= min || tmp >= max);
        return tmp;
    }

    // Applies mutation to genome using given random number generator.
    // Adds random values to genes multiplied with given factor,
    // value is created using n randomly generated numbers.
    // Resulting genes are kept inside given (min<->max) range.
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

    // Returns random value from gaussian distribution with given mean
    // and standard deviation, generated using given random number generator.
    public static double N(Random rnd, double mean, double std)
    {
        return mean + rnd.nextGaussian() * std;
    }

    // Returns random value from gaussian distribution with given mean
    // and standard deviation, generated using given random number generator.
    // Generated value is kept between given lower and upper bound.
    public static double Nr(
        Random rnd, double mean, double std,
        double lowerBound, double upperBound)
    {
        double n;
        do
        {
            n = N(rnd, mean, std);
        }
        while (n < lowerBound || n > upperBound);
        return n;
    }

    // Applies uncorrelated mutation with n step sizes (book page 60).
    // Mutation is applied to genome and step sizes in sigmas.
    // Randomly generated values are created using the given random generator.
    // Genome values are kept between given lower and upper bound.
    public static double[][] uncorrelated(
            double[] genome, double[] sigmas, Random rnd,
            double lowerBound, double upperBound)
    {
        // Set parameters (so far recommended, might be changed).
        final int n = genome.length;
        final double p = 1.0 / n;
        final double theta = 1.0 / Math.sqrt(2 * Math.sqrt(n));
        final double theta_ = 1.0 / Math.sqrt(2 * n);
        // Etha is arbitrarily chosen.
        final double etha = 0.01;
        
        for (int i = 0; i < n; i++)
        {
            // Decide to apply mutation to current gene.
            if (p > rnd.nextDouble())
            {
                double sValue, gValue;
                do
                {
                    // Mutate step size.
                    sValue = sigmas[i] * Math.pow(Math.E,
                            theta_ * N(rnd, 0, 1) +
                            theta * N(rnd, 0, 1));
                    // Correct for step sizes that are too small.
                    if (sValue < etha)
                    {
                        sValue = etha;
                    }
                    // Mutate genome.
                    gValue = genome[i] + sValue * N(rnd, 0, 1);
                }
                // Keep genome within given range.
                while (gValue < lowerBound || gValue > upperBound);
                sigmas[i] = sValue;
                genome[i] = gValue;
            }
        }
        return new double[][] { genome, sigmas };
    }

}

