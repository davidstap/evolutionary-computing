import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;

public class Selection
{

// TODO add roulette selection from parents (in Population)
// TODO reduce as many selections to working with only one Individual[]

    // FIXME ELITISM?
    // Implemented types of selection.
    public enum TYPE {
        UNIFORM, GREEDY, ROUNDROBIN, ROULETTE, TOURNAMENT,
        GENITOR, MUPLUSLAMBDA, MUCOMMALAMBDA
    }

    // Potential selection parameters.
    public enum PARAM {
        PARENT_K,
        ROUNDROBIN_Q,
        SURVIVALROUNDROBIN_Q
    }

    /**************************************************************************
       Private functions
    **************************************************************************/

    // Combines two lists of individuals.
    private static Population.Individual[] individualsAND(
            Population.Individual[] list1, Population.Individual[] list2)
    {
        Population.Individual[] individuals = new Population.Individual[
                list1.length + list2.length];
        for (int i = 0; i < list1.length; i++)
        {
            individuals[i] = list1[i];
        }
        for (int i = 0; i < list2.length; i++)
        {
            individuals[list1.length + i] = list2[i];
        }
        return individuals;
    }

    // TODO NEEDS TESTING
    // Determines whether given individual_ is present in individuals
    // using genome comparison.
    private static boolean isDuplicate(
            Population.Individual individual_,
            Population.Individual[] individuals)
    {
        double[] genome_ = individual_.getGenome();
        for (Population.Individual individual : individuals)
        {
            boolean duplicate = true;
            double[] genome =  individual.getGenome();
            for (int i = 0; i < genome.length; i++)
            {
                if (genome[i] != genome_[i])
                {
                    duplicate = false;
                    break;
                }
            }
            if (duplicate == true)
            {
                return true;
            }
        }
        return false;
    }

    // Sorts individuals on array of integers from highest to lowest.
    private static Population.Individual[] individualsBubbleSort(
            Population.Individual[] individuals, int[] values)
    {
        boolean changed;
        do
        {
            changed = false;
            for (int i = 0; i < individuals.length; i++)
            {
                for (int j = i + 1; j < individuals.length; j++)
                {
                    if (values[i] < values[j])
                    {
                        Population.Individual individual = individuals[i];
                        int value = values[i];
                        individuals[i] = individuals[j];
                        values[i] = values[j];
                        individuals[j] = individual;
                        values[j] = value;
                        changed = true;
                        break;
                    }
                }
            }
        }
        while (changed == true);
        return individuals;
    }

    /**************************************************************************
       Parent selection
    **************************************************************************/

    /* TODO
    // Selects k individuals based on Roulette Wheel and ranking selection.
    public static Population.Individual[] rouletteWheel(
            Population.Individuals[] individuals, int k, double s)
    */

    // Calls uniform after unpacking parameters.
    public static Population.Individual[] uniform(
            Population.Individual[] individuals, Random rnd,
            HashMap<String, Double> params)
    {
        String param = PARAM.PARENT_K.toString();
        int k = params.containsKey(param) ?
                params.get(param).intValue() :
                individuals.length;
        return uniform(individuals, rnd, k);
    }

    // Uniformly picks k individuals from the population.
    public static Population.Individual[] uniform(
            Population.Individual[] individuals_, Random rnd, int k)
    {
        Population.Individual[] individuals = new Population.Individual[k];
        for (int i = 0; i < k; i++)
        {
            int P = (int)(rnd.nextDouble() * individuals_.length);
            individuals[i] =
                    individuals_[P];
        }
        return individuals;
    }

    // Calls greedy after unpacking parameters.
    public static Population.Individual[] greedy(
            Population.Individual[] individuals,
            HashMap<String, Double> params)
    {
        String param = PARAM.PARENT_K.toString();
        int k = params.containsKey(param) ?
                params.get(param).intValue() :
                individuals.length;
        return greedy(individuals, k);
    }

    // Selects k best individuals.
    public static Population.Individual[] greedy(
            Population.Individual[] individuals_, int k)
    {
        individuals_ = Population.sort(individuals_);
        Population.Individual[] individuals = new Population.Individual[k];
        for (int i = 0; i < k; i++)
        {
            individuals[i] = individuals_[i % individuals_.length];
        }
        return individuals;
    }

    // Calls roundRobin after unpacking parameters.
    public static Population.Individual[] roundRobin(
            Population.Individual[] individuals, Random rnd,
            HashMap<String, Double> params)
    {
        String param = PARAM.PARENT_K.toString();
        int k = params.containsKey(param) ?
                params.get(param).intValue() :
                individuals.length;
        param = PARAM.ROUNDROBIN_Q.toString();
        int q = params.containsKey(param) ?
                params.get(param).intValue() :
                individuals.length;
        return roundRobin(individuals, rnd, k, q);
    }

    // Compare individual each individual to q other individuals,
    // return in order of best to worst scoring individuals.
    public static Population.Individual[] roundRobin(
            Population.Individual[] individuals_, Random rnd, int k, int q)
    {
        Population.Individual[] individuals = new Population.Individual[k];
        for (int l = 0; l < k; l++)
        {
            int[] wins = new int[individuals_.length];
            for (int i = 0; i < individuals_.length; i++)
            {
                for (int j = 0; j < q; j++)
                {
                    if (individuals_[i].compareTo(individuals_[
                            rnd.nextInt(individuals_.length)]) == 1)
                    {
                        wins[i]++;
                    }
                }
            }
            individuals_ = individualsBubbleSort(individuals_, wins);
            for (int i = 0; i < individuals_.length && i + l < k; i++)
            {
                individuals[i + l] = individuals_[i];
            }
        }
        return individuals;
    }


    /**************************************************************************
       Survival selection
    **************************************************************************/

    // TODO NEEDS TESTING
    // Survival selection Genitor.
    // Replaces worst parents for children.
    public static Population.Individual[] genitor(
            Population.Individual[] parents, Population.Individual[] children)
            throws ArrayIndexOutOfBoundsException
    {
        if (children.length > parents.length)
        {
            throw new ArrayIndexOutOfBoundsException("\n\t" +
                    Selection.class.getName() + "::genitor: " +
                    "children.length > parents.length\n");
        }
        parents = Population.sort(parents);
        Population.Individual[] individuals = Arrays.copyOfRange(
                parents, 0, parents.length - children.length);
        return individualsAND(individuals, children);
    }


    // Calls survivalRoundRobin after unpacking parameters.
    public static Population.Individual[] survivalRoundRobin(
            Population.Individual[] parents, Population.Individual[] children, 
            Random rnd, HashMap<String, Double> params)
    {
        String param = PARAM.SURVIVALROUNDROBIN_Q.toString();
        int q = params.containsKey(param) ?
                params.get(param).intValue() :
                parents.length + children.length;
        return survivalRoundRobin(parents, children, rnd, q);
    }

    // TODO NEEDS TESTING
    // Compare individual each individual to q other individuals,
    // return best scoring individuals.
    public static Population.Individual[] survivalRoundRobin(
            Population.Individual[] parents, Population.Individual[] children,
            Random rnd, int q)
    {
        Population.Individual[] individuals = individualsAND(parents, children);
        individuals = roundRobin(individuals, rnd, parents.length, q);
        return Arrays.copyOfRange(individuals, 0, parents.length);
    }

    // Survival selection (mu + lambda).
    // Picks best individuals from entire pool of parents + children
    // and sets them as the new population.
    public static Population.Individual[] mu_plus_lambda(
            Population.Individual[] parents, Population.Individual[] children)
    {
        Population.Individual[] individuals = individualsAND(
            parents, children);
        // Return best individuals.
        individuals = Population.sort(individuals);
        return Arrays.copyOfRange(individuals, 0, parents.length);
    }

    // Survival selection (mu, lambda)
    // Picks best individuals from children
    public static Population.Individual[] mu_comma_lambda(
            Population.Individual[] parents, Population.Individual[] children)
            throws ArrayIndexOutOfBoundsException
    {
        if (children.length < parents.length)
        {
            return mu_plus_lambda(parents, children);
            /*
            throw new ArrayIndexOutOfBoundsException("\n\t" +
                    Selection.class.getName() + "::mu_comma_lambda: " +
                    "children.length < parents.length\n");
            */
        }
        children = Population.sort(children);
        return Arrays.copyOfRange(children, 0, parents.length);
    }

    /**************************************************************************
       Other selection
    **************************************************************************/

    // Returns best individual from given list of individuals.
    public static Population.Individual getElitism(
            Population.Individual[] individuals)
    {
        individuals = Population.sort(individuals);
        return individuals[0];
    }

    // TODO NEEDS TESTING
    // Inserts given elite into list of individuals, replacing worst indiviudal.
    public static Population.Individual[] applyElitism(
            Population.Individual elite, Population.Individual[] individuals)
    {
        if (!isDuplicate(elite, individuals))
        {
            individuals = Population.reverseSort(individuals);
            individuals[0] = elite;
        }
        return individuals;
    }

    // TODO NEEDS TESTING
    // Inserts given elite into list of individuals, replacing worst indiviudal.
    // Makes sure not to replace any individual in given children.
    public static Population.Individual[] applyElitism(
            Population.Individual elite, Population.Individual[] individuals,
            Population.Individual[] children)
    {
        if (!isDuplicate(elite, individuals))
        {
            individuals = Population.reverseSort(individuals);
            for (int i = 0; i < individuals.length; i++)
            {
                if (!isDuplicate(individuals[i], children))
                {
                    individuals[i] = elite;
                    break;
                }
            }
        }
        return individuals;
    }

}

