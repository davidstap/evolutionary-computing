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

        evaluations_limit_ = 10000;

        String recomb_method = "discrete";
        String mutation_method = "uncorrelated";

        // Test island
        Island island = new Island(recomb_method, mutation_method, popSize, evaluation_::evaluate, rnd_);
        island.evolutionCycle(nChildren);

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
                        myPop.selectParentsTournament(nChildren, k, c);
//                Population.Individual[] parents =
//                        myPop.parentSelectionRouletteWheel(nChildren, sRW);

                // ---------- Recombination ----------
                // choose: discreteRecombination, simpleArithmetic, singleArithmeticRecom, wholeArithmeticRecom
                // TODO: look at relation recombination and nChildren
                //   --> Paretns length must be an even number
                // TODO: add recombination of sigmas
                // TODO: make random in recombination use rnd_
//                Recombination recomb = new Recombination(parents);
//                Population.Individual[] recombChildren =
//                        recomb.wholeArithmeticRecom();
                int[] N = {nChildren};
                Population childPop = new Population(
//                        recomb, N, evaluation_::evaluate, rnd_);
                        parents, N, evaluation_::evaluate, rnd_);

                // ---------- Mutation ----------  
                childPop.mutate(rnd_, mutation_method);

                evals = childPop.evaluate(evals, evaluations_limit_);

                // TODO: add several survival mechanisms
                // ---------- Survivor selection
                try
                {
                    myPop.survival(childPop);
                }
                catch (Exception e)
                {
                    System.out.println(e);
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
