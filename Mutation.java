import java.util.Random;
import java.util.HashMap;

public class Mutation {

    // Implemented types of mutation.
    public enum TYPE {
        GAUSSIAN, UNCORRELATED
    }

    // Potential mutation parameters.
    public enum PARAM {
        MUTATIONRATE,
        GAUSSIAN_MU, GAUSSIAN_SIGMA,
        UNCORRELATED_TAU, UNCORRELATED_TAU_, UNCORRELATED_EPSILON
    }

/*

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

*/

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

    /**************************************************************************
        Private functions
    **************************************************************************/

    // Returns random value from a gaussian distribution with given mean (mu)
    // and standard deviation (simga).
    // Values are generated using given random number generator.
    private static double N(Random rnd, double mu, double sigma)
    {
        return mu + rnd.nextGaussian() * sigma;
    }

    /**************************************************************************
        Gaussian mutation
    **************************************************************************/

    // Calls main gaussian method after first unpacking parameter hashmap.
    public static double[] gaussian(
            double[] genome, Random rnd,
            double[] lowerBound, double[] upperBound,
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

    // Main gaussian method.
    // Applies mutation adding values to genes along a gaussian distribution
    // of given mean (mu) and standard deviation (sigma).
    // Randomly generated values are created using the given random generator.
    // Genome values are kept between given lower and upper bound.
    public static double[] gaussian(
            double[] genome, Random rnd,
            double[] lowerBound, double[] upperBound,
            double mutationRate, double mu, double sigma)
    {
        for (int i = 0; i < genome.length; i++)
        {
            // Decide to apply mutation to current gene.
            if (mutationRate > rnd.nextDouble())
            {
                genome[i] = gaussian_(genome[i], rnd,
                        lowerBound[i], upperBound[i], mu, sigma);
            }
        }
        return genome;
    }

    // Step in gaussian for one gene and sigma.
    public static double gaussian_(
            double gene, Random rnd, double lowerBound, double upperBound,
            double mu, double sigma)
    {
        double tmp;
        // Keep genome within given range if proper range is given.
        if (lowerBound < upperBound)
        {
            do
            {
                tmp = gene + N(rnd, mu, sigma);
            }
            while (tmp < lowerBound || tmp > upperBound);
        }
        // Ignore upper bound if lower = upper.
        else if (lowerBound == upperBound)
        {
            do
            {
                tmp = gene + N(rnd, mu, sigma);
            }
            while (tmp < lowerBound);
        }
        // Ignore lower and upper bound if lower > upper.
        else
        {
            tmp = gene + N(rnd, mu, sigma);
        }
        return tmp;
    }

    /**************************************************************************
        Uncorrelated mutation
    **************************************************************************/

    // Calls main uncorrelated method after first unpacking parameter hashmap.
    public static double[][] uncorrelated(
            double[] genome, double[] sigmas, Random rnd,
            double[] lowerBound, double[] upperBound,
            HashMap<String, Double> params)
    {
        String param = PARAM.MUTATIONRATE.toString();
        double mutationRate = params.containsKey(param) ?
                params.get(param) :
                1.0;
        param = PARAM.UNCORRELATED_TAU.toString();
        double tau = params.containsKey(param) ?
                params.get(param) :
                1.0 / Math.sqrt(2 * Math.sqrt(genome.length));
        param = PARAM.UNCORRELATED_TAU_.toString();
        double tau_ = params.containsKey(param) ?
                params.get(param) :
                1.0 / Math.sqrt(2 * genome.length);
        param = PARAM.UNCORRELATED_EPSILON.toString();
        double epsilon = params.containsKey(param) ?
                params.get(param) :
                0.01;
        return uncorrelated(genome, sigmas, rnd, lowerBound, upperBound,
                mutationRate, tau, tau_, epsilon);
    }

    // Main uncorrelated method.
    // Applies uncorrelated mutation with n step sizes (book page 60).
    // Mutation is applied to genome and step sizes in sigmas.
    // Randomly generated values are created using the given random generator.
    // Genome values are kept between given lower and upper bound.
    public static double[][] uncorrelated(
            double[] genome, double[] sigmas, Random rnd,
            double[] lowerBound, double[] upperBound,
            double mutationRate,
            double tau, double tau_, double epsilon)
    {
        double[] tmp;
        for (int i = 0; i < genome.length; i++)
        {
            // Decide to apply mutation to current gene and sigma.
            if (mutationRate > rnd.nextDouble())
            {
                tmp = uncorrelated_(genome[i], sigmas[i], rnd,
                        lowerBound[i], upperBound[i],
                        tau, tau_, epsilon);
                sigmas[i] = tmp[1];
                genome[i] = tmp[0];
            }
        }
        return new double[][] { genome, sigmas };
    }

    // Step in uncorrelated for one gene and sigma.
    public static double[] uncorrelated_(
            double gene, double sigma, Random rnd,
            double lowerBound, double upperBound,
            double tau, double tau_, double epsilon)
    {
        double[] tmp;
        // Keep genome within given range if proper range is given.
        if (lowerBound < upperBound)
        {
            do
            {
                tmp = uncorrelated__(gene, sigma, rnd,
                        tau, tau_, epsilon);
            }
            while (tmp[0] < lowerBound || tmp[0] > upperBound);
        }
        // Ignore upper bound if lower = upper.
        else if (lowerBound == upperBound)
        {
            do
            {
                tmp = uncorrelated__(gene, sigma, rnd,
                        tau, tau_, epsilon);
            }
            while (tmp[0] < lowerBound);
        }
        // Ignore lower and upper bound if lower > upper.
        else
        {
            tmp = uncorrelated__(gene, sigma, rnd,
                    tau, tau_, epsilon);
        }
        return tmp;
    }

    // Step in uncorrelated calculating new gene and sigma.
    public static double[] uncorrelated__(
            double gene, double sigma, Random rnd,
            double tau, double tau_, double epsilon)
    {
        double sValue, gValue;
        // Mutate step size.
        sValue = sigma * Math.pow(Math.E,
                tau_ * N(rnd, 0, 1) +
                tau * N(rnd, 0, 1));
        // Correct for step sizes that are too small.
        if (sValue < epsilon)
        {
            sValue = epsilon;
        }
        // Mutate genome.
        gValue = gene + sValue * N(rnd, 0, 1);
        return new double[] { gValue, sValue };
    }

}

