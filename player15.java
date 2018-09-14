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
        double mutationFactor = 1.0;
        
        // init population
        Population myPop = new Population(popSize, evaluation_::evaluate);
        myPop.init(rnd_);
        
        // calculate fitness
        while(evals<1000){//evaluations_limit_){    //XXX
            // Select parents
            evals += myPop.evaluate();
//            myPop.print();
            myPop.sort();
            double[][] parents = myPop.selectParents();
            
            // Apply crossover / mutation operators
            double[] child = parents[0];
            
            // Check fitness of unknown fuction
            Double fitness = (double) evaluation_.evaluate(child);
            evals++;
//            System.out.println("fitness> " + Double.toString(fitness));
            
            // Select survivors
            myPop.evolve(child, rnd_, mutationFactor);
        }
    }
}
