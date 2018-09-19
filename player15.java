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
        
        Set keys = props.keySet();
        Iterator itr = keys.iterator();
        String propStr;
        System.out.println(" Properties:");
        while (itr.hasNext()){
            propStr = (String)itr.next();
            System.out.println(" -- | " + propStr +
                    " = " + props.getProperty(propStr));
        }
        
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
        double mutationFactor = .1;
//        evaluations_limit_ = 10000;
        
        // init population
        Population myPop = new Population(popSize, evaluation_::evaluate, rnd_);
//        myPop.print();
        
        // calculate fitness
        while(evals<evaluations_limit_){    //XXX
            // Select parents
            evals = myPop.evaluate(evals, evaluations_limit_);
            if (evals == evaluations_limit_) { break; }
//            myPop.print();
            
            Population.Unit[] parents = myPop.selectParent();
            
            // Apply crossover / mutation operators
            Population.Unit child = parents[0];
            
            // Check fitness of unknown fuction
//            System.out.println("evals> " + Double.toString(evals));
            
            // Select survivors
            myPop.evolve(child, rnd_, mutationFactor);
        }
//        myPop.print();
        Double fitness = myPop.getMaxFitness();
    }
}
