import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;

import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;
import java.util.function.Function;
import java.util.HashMap;

public class player15 implements ContestSubmission
{
    Random rnd_;
    Random islandRnd_;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;

    public player15()
    {
        rnd_ = new Random();
        islandRnd_ = new Random();
    }

    public void setSeed(long seed)
    {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
        islandRnd_.setSeed(seed);
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
        int popSize = 12;
        // Tournament selection candidates
        int k = 4;
        // constant for proportional fitness
        int c = 2;
//        int nParents = popSize / 2;
        // 2 parents produce 2 children
        int nChildren = popSize / 2;
        // Roulette wheel parameter S. Range: 1.0 < s  2.0
        double sRW = 2.0;

/*
        popSize = 4;
        nChildren = popSize;
*/

        evaluations_limit_ = 10000;


        // recombination = uniform
        // parent selection = greedy
        // mutation = uncorrelated
        // survival selection = (mu,lambda)
        // make testc ==> 9.999938929480702

        // Setting recombination type and parameters.
        Recombination.TYPE recombinationType = Recombination.TYPE.UNIFORM;
        HashMap<String, Double> recombinationParams =
                new HashMap<String, Double>();

        // TODO Setting parent selection type and parameters.
        String parentSelectionType = "greedy";

        // Setting mutation type and parameters.
        Mutation.TYPE mutationType = Mutation.TYPE.UNCORRELATED;
        HashMap<String, Double> mutationParams = new HashMap<String, Double>();
        mutationParams.put(Mutation.PARAM.MUTATIONRATE.toString(),
                1.0 / Population.dim);

        // TODO Setting survival selection type and parameters.
        



        // XXX Parameters used in Island model (for now split from rest).
        Recombination.TYPE recomb_method = Recombination.TYPE.SIMPLEARITHMETIC;
        Mutation.TYPE mutation_method = Mutation.TYPE.UNCORRELATED;
        String selection_method = "tournament";
        String survival_method = "";


        // Test island
        // (XXX Uses different random so values in myPop stay the same
        //      while Island.java develops.)


        Island island = new Island(recomb_method, mutation_method, selection_method, survival_method, popSize, evaluation_::evaluate, islandRnd_);
        island.evolutionCycle(nChildren);



        // Initialize population
        Population myPop = new Population(popSize, evaluation_::evaluate, rnd_);
        evals = myPop.evaluate(evals, evaluations_limit_);

        if (myPop.size() == popSize) {
            // calculate fitness
            while(evals < evaluations_limit_){

                if (print)
                {
                    myPop.sort();
                    myPop.print();
                }

                // TODO: add tournament selection
                // ---------- Parent Selection ----------
                Population.Individual[] parents =
                        myPop.parentSelection(nChildren, rnd_,
                        parentSelectionType);

//                myPop.selectParentsTournament(nChildren, k, c);
//                Population.Individual[] parents =
//                        myPop.parentSelectionRouletteWheel(nChildren, sRW);

                // ---------- Recombination ----------
                Population.Individual[] children;
                try
                {
                    children = myPop.recombination(parents, rnd_,
                            recombinationType, recombinationParams);
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
//                        new Population.Individual[]{myPop.getIndividuals()[0]}, new int[]{nChildren}, evaluation_::evaluate);


                // ---------- Mutation ----------  
                childPop.mutate(rnd_, mutationType, mutationParams);

                evals = childPop.evaluate(evals, evaluations_limit_);

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



//                break;




            }
        }

        if (print)
        {
            myPop.sort();
            myPop.print();
            System.out.println();
        }

    }
}
