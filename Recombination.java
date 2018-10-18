import java.util.Random;
import java.util.HashMap;

public class Recombination
{

    /*
    TODO add n-point crossover
    TODO add blend crossover
    */

    // Implemented types of crossover.
    public enum TYPE {
        ONEPOINT, UNIFORM, SIMPLEARITHMETIC, SINGLEARITHMETIC, WHOLEARITHMETIC
    }
    
    // Potential crossover parameters.
    public enum PARAM {
        ALPHA
    }

    // Splits list of individuals
    // into list of even entries and list of uneven entries.
    private static Population.Individual[][] splitIndividuals(
            Population.Individual[] individuals_)
    {
        if (individuals_.length == 1)
        {
            return new Population.Individual[][]{
                    individuals_, individuals_};
        }
        int nPairs = (int)(individuals_.length / 2);
        Population.Individual[][] individuals =
                new Population.Individual[2][nPairs];
        for (int i = 0; i < nPairs; i++)
        {
            individuals[0][i] = individuals_[2 * i];
            individuals[1][i] = individuals_[2 * i + 1];
        }
        return individuals;
    }

    // Calls method that unpacks parameter hashmap, but first splits parents.
    public static Population.Individual[] recombination(
            Population.Individual[] individuals, Random rnd, TYPE type,
            HashMap<String, Double> params)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        Population.Individual[][] splitParents = splitIndividuals(individuals);
        return recombination(
                splitParents[0], splitParents[1], rnd, type, params);
    }

    // Calls main recombination method, but first splits parents.
    public static Population.Individual[] recombination(
            Population.Individual[] individuals, Random rnd, TYPE type,
            double alpha)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        Population.Individual[][] splitParents = splitIndividuals(individuals);
        return recombination(
                splitParents[0], splitParents[1], rnd, type, alpha);
    }

    // Calls main recombination method after unpacking hashmap of parameters.
    public static Population.Individual[] recombination(
            Population.Individual[] list1, Population.Individual[] list2,
            Random rnd, TYPE type, HashMap<String, Double> params)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        String param = PARAM.ALPHA.toString();
        double alpha = params.containsKey(param) ?
                params.get(param) :
                0.5;
        return recombination(list1, list2, rnd, type, alpha);
    }

    // Main recombination method.
    // Applies recombination on pairs of individuals (one from each list),
    // each recombination results in two children,
    // and returns list of newly created individuals.
    public static Population.Individual[] recombination(
            Population.Individual[] list1, Population.Individual[] list2,
            Random rnd, TYPE type, double alpha)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        // Check whether two parents are given for each recombination.
        if (list1.length != list2.length)
        {
            throw new ArrayIndexOutOfBoundsException("\n\t" +
                    Recombination.class.getName() +
                    ": input arrays of unequal length\n");
        }
        
        Population.Individual[] individuals =
            new Population.Individual[2 * list1.length];
        
        for (int i = 0; i < list1.length; i++)
        {
            // Set genomes and sigma-lists for current pair.
            //FIXME Still crashes NULLPOINTEREXCEPTION, but only when fitting
            //      Also, loopin through list1 or list2 is endless?
            double[] g1 = list1[i].getGenome();
            double[] s1 = list1[i].getSigmas();
            double[] g2 = list2[i].getGenome();
            double[] s2 = list2[i].getSigmas();
            double[] cg1 = new double[g1.length];
            double[] cs1 = new double[g1.length];
            double[] cg2 = new double[g1.length];
            double[] cs2 = new double[g1.length];
            
            int k = rnd.nextInt(g1.length);
            
            // Apply recombination for each gene and sigma.
            for (int j = 0; j < g1.length; j++)
            {
                switch (type) {
                    case ONEPOINT:
                        if (j < k)
                        {
                            setSame(j, g1, s1, g2, s2, cg1, cs1, cg2, cs2);
                        }
                        else
                        {
                            setOpposite(j, g1, s1, g2, s2, cg1, cs1, cg2, cs2);
                        }
                        break;
                    case UNIFORM:
                        if (rnd.nextDouble() < alpha)
                        {
                            setSame(j, g1, s1, g2, s2, cg1, cs1, cg2, cs2);
                        }
                        else
                        {
                            setOpposite(j, g1, s1, g2, s2, cg1, cs1, cg2, cs2);
                        }
                        break;
                    case SIMPLEARITHMETIC:
                        if (j < k)
                        {
                            setSame(j, g1, s1, g2, s2, cg1, cs1, cg2, cs2);
                        }
                        else
                        {
                            setArithmetic(
                                    j, g1, s1, g2, s2, cg1, cs1, cg2, cs2,
                                    alpha);
                        }
                        break;
                    case SINGLEARITHMETIC:
                        if (j == k)
                        {
                            setArithmetic(
                                    j, g1, s1, g2, s2, cg1, cs1, cg2, cs2,
                                    alpha);
                        }
                        else
                        {
                            setSame(j, g1, s1, g2, s2, cg1, cs1, cg2, cs2);
                        }
                        break;
                    case WHOLEARITHMETIC:
                        setArithmetic(j, g1, s1, g2, s2, cg1, cs1, cg2, cs2,
                                alpha);
                        break;
                    // Throw exception if given recombination type is invalid.
                    default:
                        throw new IllegalArgumentException("\n\t" +
                                Recombination.class.getName() +
                                ": recombination type not recognized\n");
                }
            }
            // Create new individual.
            individuals[2 * i] =
                    new Population.Individual(cg1, cs1);
            individuals[2 * i + 1] =
                    new Population.Individual(cg2, cs2);
        }
        return individuals;
    }

    // Sets gene and sigma of child to that of its respective parent.
    private static void setSame(
            int k,
            double[] g1, double[] s1, double[] g2, double[] s2,
            double[] cg1, double[] cs1, double[] cg2, double[] cs2)
    {
        cg1[k] = g1[k];
        cs1[k] = s1[k];
        cg2[k] = g2[k];
        cs2[k] = s2[k];
    }

    // Sets gene and sigma of child to that of its opposite parent.
    private static void setOpposite(
            int k,
            double[] g1, double[] s1, double[] g2, double[] s2,
            double[] cg1, double[] cs1, double[] cg2, double[] cs2)
    {
        cg1[k] = g2[k];
        cs1[k] = s2[k];
        cg2[k] = g1[k];
        cs2[k] = s1[k];
    }

    private static double arithmeticFunction(
            double p1, double p2, double alpha)
    {
        return alpha * p1 + (1.0 - alpha) * p2;
    }

    // Sets gene and sigma of child to a combination of the parent-values.
    private static void setArithmetic(
            int k,
            double[] g1, double[] s1, double[] g2, double[] s2,
            double[] cg1, double[] cs1, double[] cg2, double[] cs2,
            double alpha)
    {
        cg1[k] = arithmeticFunction(g1[k], g2[k], alpha);
        cs1[k] = arithmeticFunction(s1[k], s2[k], alpha);
        cg2[k] = arithmeticFunction(g2[k], g1[k], alpha);
        cs2[k] = arithmeticFunction(s2[k], s1[k], alpha);
    }

}

