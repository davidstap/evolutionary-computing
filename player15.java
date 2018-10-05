import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;

import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;
import java.util.function.Function;

public class player15 implements ContestSubmission
{
    Random rnd_;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;

    public player15()
    {
        rnd_ = new Random();
    }

    public void setSeed(long seed)
    {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
    }

    public void setEvaluation(ContestEvaluation evaluation)
    {
        // Set evaluation problem used in the run
        evaluation_ = evaluation;


        // Get evaluation properties
        Properties props = evaluation.getProperties();

        /*
        Set keys = props.keySet();
        Iterator itr = keys.iterator();
        String propStr;
        System.out.println(" Properties:");
        while (itr.hasNext()){
            propStr = (String)itr.next();
            System.out.println(" -- | " + propStr +
                    " = " + props.getProperty(propStr));
        }
        */

        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }

    // Splits list of individuals
    // into list of even entries and list of uneven entries.
    public Population.Individual[][] splitIndividuals(
            Population.Individual[] individuals_)
    {
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

    public void printException(Exception e)
    {
        System.out.print("\u001B[31m");
        System.out.print(e);
        System.out.print("\u001B[0m");
    }

    public void run()
    {
        // Run your algorithm here

        boolean print = false;

        // EA Parameters
        int evals = 0;
        int popSize = 10;
//        int nParents = popSize / 2;
        // 2 parents produce 2 children
        int nChildren = popSize / 2;
        // Roulette wheel parameter S. Range: 1.0 < s ≤ 2.0
        double sRW = 2.0;

        evaluations_limit_ = 10000;

        // Initialize population
        Population myPop = new Population(popSize, evaluation_::evaluate, rnd_);
        evals = myPop.evaluate(evals, evaluations_limit_);

        if (myPop.size() == popSize) {
            // calculate fitness
            while(evals < evaluations_limit_){

                if (print == true)
                {
                    myPop.sort();
                    myPop.print();
                }

                // TODO: add tournament selection
                // ---------- Parent Selection ----------
                Population.Individual[] parents =
                        myPop.parentSelection(nChildren);

                Population.Individual[][] splitParents =
                        splitIndividuals(parents);


                // ---------- Recombination ----------
                Population.Individual[] children;
                try
                {
                    children = myPop.recombination(
                                    splitParents[0], splitParents[1], rnd_);
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    printException(e);
                    break;
                }
                catch (IllegalArgumentException e)
                {
                    printException(e);
                    break;
                }

                Population childPop = new Population(
//                        parents, new int[]{nChildren}, evaluation_::evaluate);
                        children, evaluation_::evaluate);


                // ---------- Mutation ----------  
                childPop.mutate(rnd_);

                evals = childPop.evaluate(evals, evaluations_limit_);


                // TODO: add several survival mechanisms
                // ---------- Survivor selection
                try
                {
                    myPop.survival(childPop, rnd_);
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    printException(e);
                    break;
                }

            }
        }

        if (print == true)
        {
            myPop.sort();
            myPop.print();
            System.out.println();
        }

    }
}
