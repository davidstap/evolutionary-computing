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

        int evals = 0;
        int popSize = 4;
        int nChildren = popSize;
        double mutationFactor = .1;
        evaluations_limit_ = 10000;

        // init population
        Population myPop = new Population(popSize, evaluation_::evaluate, rnd_);
        evals = myPop.evaluate(evals, evaluations_limit_);

        if (myPop.size() == popSize) {
            // calculate fitness
            while(evals < evaluations_limit_){

//                Population.Unit[] parent = myPop.selectParents(1);

                Recombination recomb = new Recombination(myPop.selectParents(2));
                Children children = recomb.discreteRecombination();

                Population.Unit[] recomb_children = {children.getChild1(), children.getChild2()};

                int[] N = {nChildren};
                Population chPop = new Population(
                        recomb_children, N, evaluation_::evaluate, rnd_);

                chPop.mutate(rnd_, mutationFactor);

                evals = chPop.evaluate(evals, evaluations_limit_);

                myPop.survival(chPop);

            }
        }
    }
}
