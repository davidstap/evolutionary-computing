import java.util.Random;
import java.util.HashMap;

public class Mutation {

    public enum TYPE {
        GAUSSIAN, UNCORRELATED
    }
    public enum PARAM {
        MUTATIONRATE,
        GAUSSIAN_MU, GAUSSIAN_SIGMA,
        UNCORRELATED_THETA, UNCORRELATED_THETA_, UNCORRELATED_ETHA
    }

// TODO replace normal wit a gaussian (params: MU, SIGMA instead of FACTOR)

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
    // Generated value is kept between given lower and upper bound.
    private static double Nr(
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

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

    /**************************************************************************
        Private functions
    **************************************************************************/

    // Returns random value from gaussian distribution with given mean
    // and standard deviation, generated using given random number generator.
    private static double N(Random rnd, double mu, double sigma)
    {
        return mu + rnd.nextGaussian() * sigma;
    }

    /**************************************************************************
        Gaussian mutation
    **************************************************************************/

    public static double gaussian_(
            double gene, Random rnd, double lowerBound, double upperBound,
            double mu, double sigma)
    {
        double tmp;
        if (lowerBound < upperBound)
        {
            do
            {
                tmp = gene + N(rnd, mu, sigma);
            }
            // Keep genome within given range.
            while (tmp < lowerBound || tmp > upperBound);
        }
        else
        {
            tmp = gene + N(rnd, mu, sigma);
        }
        return tmp;
    }

    public static double[] gaussian(
            double[] genome, Random rnd, double lowerBound, double upperBound,
            HashMap<String, Double> params)
    {
        String param = PARAM.MUTATIONRATE.toString();
        double mutationRate = params.containsKey(param) ?
                params.get(param) :
                1.0;
        param = PARAM.GAUSSIAN_MU.toString();
        double mu = params.containsKey(param) ?
                params.get(param) :
                0.0;
        param = PARAM.GAUSSIAN_SIGMA.toString();
        double sigma = params.containsKey(param) ?
                params.get(param) :
                0.1;
        return gaussian(genome, rnd, lowerBound, upperBound,
                mutationRate, mu, sigma);
    }

    public static double[] gaussian(
            double[] genome, Random rnd, double lowerBound, double upperBound,
            double mutationRate, double mu, double sigma)
    {
        for (int i = 0; i < genome.length; i++)
        {
            if (mutationRate > rnd.nextDouble())
            {
                genome[i] = gaussian_(genome[i], rnd, lowerBound, upperBound,
                        mu, sigma);
            }
        }
        return genome;
    }

    /**************************************************************************
        Uncorrelated mutation
    **************************************************************************/

    public static double[] uncorrelated__(
            double gene, double sigma, Random rnd,
            double theta, double theta_, double etha)
    {
        double sValue, gValue;
        // Mutate step size.
        sValue = sigma * Math.pow(Math.E,
                theta_ * N(rnd, 0, 1) +
                theta * N(rnd, 0, 1));
        // Correct for step sizes that are too small.
        if (sValue < etha)
        {
            sValue = etha;
        }
        // Mutate genome.
        gValue = gene + sValue * N(rnd, 0, 1);
        return new double[] { gValue, sValue };
    }

    public static double[] uncorrelated_(
            double gene, double sigma, Random rnd,
            double lowerBound, double upperBound,
            double theta, double theta_, double etha)
    {
        double[] tmp;
        if (lowerBound < upperBound)
        {
            do
            {
                tmp = uncorrelated__(gene, sigma, rnd,
                        theta, theta_, etha);
            }
            // Keep genome within given range.
            while (tmp[0] < lowerBound || tmp[0] > upperBound);
        }
        else
        {
            tmp = uncorrelated__(gene, sigma, rnd,
                    theta, theta_, etha);
        }
        return tmp;
    }

    public static double[][] uncorrelated(
            double[] genome, double[] sigmas, Random rnd,
            double lowerBound, double upperBound,
            HashMap<String, Double> params)
    {
        String param = PARAM.MUTATIONRATE.toString();
        double mutationRate = params.containsKey(param) ?
                params.get(param) :
                1.0;
        param = PARAM.UNCORRELATED_THETA.toString();
        double theta = params.containsKey(param) ?
                params.get(param) :
                1.0 / Math.sqrt(2 * Math.sqrt(genome.length));
        param = PARAM.UNCORRELATED_THETA_.toString();
        double theta_ = params.containsKey(param) ?
                params.get(param) :
                1.0 / Math.sqrt(2 * genome.length);
        param = PARAM.UNCORRELATED_ETHA.toString();
        double etha = params.containsKey(param) ?
                params.get(param) :
                0.01;
        return uncorrelated(genome, sigmas, rnd, lowerBound, upperBound,
                mutationRate, theta, theta_, etha);
    }

    // Applies uncorrelated mutation with n step sizes (book page 60).
    // Mutation is applied to genome and step sizes in sigmas.
    // Randomly generated values are created using the given random generator.
    // Genome values are kept between given lower and upper bound.
    public static double[][] uncorrelated(
            double[] genome, double[] sigmas, Random rnd,
            double lowerBound, double upperBound,
            double mutationRate,
            double theta, double theta_, double etha)
    {
        double[] tmp;
        for (int i = 0; i < genome.length; i++)
        {
            // Decide to apply mutation to current gene.
            if (mutationRate > rnd.nextDouble())
            {
                tmp = uncorrelated_(genome[i], sigmas[i], rnd,
                        lowerBound, upperBound,
                        theta, theta_, etha);
                sigmas[i] = tmp[1];
                genome[i] = tmp[0];
            }
        }
        return new double[][] { genome, sigmas };
    }

}

