import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;

import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;
import java.util.function.Function;
import java.util.HashMap;
import java.util.HashSet;

import java.io.IOException;

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
        System.err.print("\u001B[31m");
        System.err.print(e);
        System.err.print("\u001B[0m");
    }

    public void handleParams(HashMap<String, Double> params,
            HashMap<String, Double> parentSelectionParams,
            HashMap<String, Double> recombinationParams,
            HashMap<String, Double> mutationParams,
            HashMap<String, Double> survivalSelectionParams)
    {
        double value;
        HashSet<String> parentSelectionParamsSet = new HashSet<String>();
        for (Selection.PARAM param : Selection.PARAM.values())
        {
            if (param != Selection.PARAM.SURVIVALROUNDROBIN_Q)
            {
                parentSelectionParamsSet.add(param.toString());
            }
        }
        HashSet<String> recombinationParamsSet = new HashSet<String>();
        for (Recombination.PARAM param : Recombination.PARAM.values())
        {
            recombinationParamsSet.add(param.toString());
        }
        HashSet<String> mutationParamsSet = new HashSet<String>();
        for (Mutation.PARAM param : Mutation.PARAM.values())
        {
            mutationParamsSet.add(param.toString());
        }
        HashSet<String> survivalSelectionParamsSet = new HashSet<String>();
        survivalSelectionParamsSet.add(
                Selection.PARAM.SURVIVALROUNDROBIN_Q.toString());
        for (String key : params.keySet())
        {
            value = params.get(key);
            if (parentSelectionParamsSet.contains(key))
            {
                parentSelectionParams.put(key, value);
            }
            else if (recombinationParamsSet.contains(key))
            {
                recombinationParams.put(key, value);
            }
            else if (mutationParamsSet.contains(key))
            {
                mutationParams.put(key, value);
            }
            else if (survivalSelectionParamsSet.contains(key))
            {
                survivalSelectionParams.put(key, value);
            }
        }
    }


    public void run()
    {
        // Run your algorithm here

        boolean print = false;

        String paramsData = System.getProperty("params");

        /*
        System.out.println("--PARAMSFILE--");
        System.out.println(paramsData);
        System.out.println("--------------");
        */


        // EA Parameters
        int evals = 0;

        int popSize = 12;
        // Cant be higher than pop size
        int nChildren = 8;

        // TODO Put into the Hashmaps when functions are moved to Selection.java
        // Tournament selection candidates
        int k = 4;
        // constant for proportional fitness
        int c = 2;
//        int nParents = popSize / 2;
        // Roulette wheel parameter S. Range: 1.0 < s  2.0
        double sRW = 2.0;

/*
        popSize = 4;
        nChildren = popSize;
*/

//        evaluations_limit_ = 10000;


        // XXX For making sure that result stays consistent throughout edits.
        // recombination = uniform
        // parent selection = greedy
        // mutation = uncorrelated
        // survival selection = (mu,lambda)
        // make testc ==> 9.999938929480702

        // Setting parent selection type and parameters.
        Selection.SELECTION_TYPE parentSelectionType = Selection.SELECTION_TYPE.ROUNDROBIN;
        HashMap<String, Double> parentSelectionParams =
                new HashMap<String, Double>();
        parentSelectionParams.put(Selection.PARAM.PARENT_K.toString(),
                (double)nChildren);
        parentSelectionParams.put(Selection.PARAM.ROUNDROBIN_Q.toString(),
                (double)nChildren);

        // Setting recombination type and parameters.
        Recombination.TYPE recombinationType = Recombination.TYPE.UNIFORM;
        HashMap<String, Double> recombinationParams =
                new HashMap<String, Double>();
//        recombinationParams.put(Recombination.PARAM.ALPHA.toString(),
//                0.5);

        // Setting mutation type and parameters.
        Mutation.TYPE mutationType = Mutation.TYPE.UNCORRELATED;
        HashMap<String, Double> mutationParams = new HashMap<String, Double>();
        mutationParams.put(Mutation.PARAM.MUTATIONRATE.toString(),
                1.0 / Population.dim);

        // Setting survival selection type and parameters.
        Selection.SURVIVAL_TYPE survivalSelectionType = Selection.SURVIVAL_TYPE.MUCOMMALAMBDA;
        HashMap<String, Double> survivalSelectionParams =
                new HashMap<String, Double>();


        if (paramsData != null)
        {
            try
            {
                String param;
                HashMap<String, Double> params = InOut.load(paramsData);
                param = Population.PARAM.SIZE.toString();
                popSize = params.containsKey(param) ?
                        params.get(param).intValue() :
                        popSize;
                param = Population.PARAM.NCHILDREN.toString();
                nChildren = params.containsKey(param) ?
                        params.get(param).intValue() :
                        nChildren;
                handleParams(params, parentSelectionParams, recombinationParams,
                        mutationParams, survivalSelectionParams);
            }
            catch(IOException e)
            {
                printException(e);
                return;
            }
        }

        
        // we ONLY evaluate islands, afterwards terminate. (i.e. not doing normal evolutionary loop)
        Boolean evaluate_islands = true;
        
        if (evaluate_islands)
        {
        // XXX Parameters used in Island model (for now split from rest).
        Recombination.TYPE recomb_method = Recombination.TYPE.SIMPLEARITHMETIC;
        Mutation.TYPE mutation_method = Mutation.TYPE.UNCORRELATED;
        Selection.SELECTION_TYPE selection_method = Selection.SELECTION_TYPE.GREEDY;
        Selection.SURVIVAL_TYPE survival_method = Selection.SURVIVAL_TYPE.MUPLUSLAMBDA;

        // Test island
        // (XXX Uses different random so values in myPop stay the same
        //      while Island.java develops.)

        Recombination.TYPE[] recomb_types = Recombination.TYPE.values();
        Mutation.TYPE[] mutation_types = Mutation.TYPE.values();
        Selection.SELECTION_TYPE[] selection_types = Selection.SELECTION_TYPE.values();
        Selection.SURVIVAL_TYPE[] survival_types = Selection.SURVIVAL_TYPE.values();

        int nIslands = 4;
        IslandList island_list = new IslandList(nChildren);
        for(int i = 0; i < nIslands; i++)
        {
            island_list.addIsland(new Island(recomb_types[rnd_.nextInt(recomb_types.length)], mutation_types[rnd_.nextInt(mutation_types.length)],
                selection_types[rnd_.nextInt(selection_types.length)], survival_types[rnd_.nextInt(survival_types.length)], popSize, evaluation_::evaluate, islandRnd_));
        }

        int evolveAmount = 1;
        evals = island_list.evaluateIslands(evals, evaluations_limit_);
        System.out.println(evaluations_limit_);
        while(evals < evaluations_limit_)
        {
            // System.out.println(evals);
            island_list.evolveIslands(evolveAmount);
            island_list.migration();
            evals = island_list.evaluateIslands(evals, evaluations_limit_);
        }
                
        // Get individuals from the islands to print for the visualization
        for(int i=0; i< nIslands; i++)
        {
            Island new_island = island_list.getIsland(i);
            Population island_pop = new_island.getPop();
            Recombination.TYPE recomb_method_island = new_island.getRecombinationType();
            
            Mutation.TYPE mutation_method_island = new_island.getMutationType();
            Selection.SELECTION_TYPE selection_method_island = new_island.getSelectionType();
            Selection.SURVIVAL_TYPE survival_method_island = new_island.getSurvivalType();
            Population.Individual[] individuals_island = island_pop.getIndividuals();
        
            for(Population.Individual individual_island : individuals_island)
            {
                // Print island indicator
                System.out.print(i+1);
                System.out.print(",");
                // print fitness
                System.out.print(individual_island.fitness);
                System.out.print(",");
                // Print all genomes
                double[] gen = individual_island.getGenome();
                for (int j=0; j<gen.length; j++)
                {
                  if (j != gen.length-1)
                  {
                    System.out.print(gen[j]);
                    System.out.print(",");
                  }
                  else
                  {
                    System.out.println(gen[j]);
                  }
                }
            }
        }

        // Exit since we are only interested in island evaluation
        System.exit(0);
        }
        
        
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
                        myPop.parentSelection(rnd_,
                        parentSelectionType, parentSelectionParams);

                /*
                System.err.print(myPop.size());
                System.err.print(" ");
                System.err.println(popSize);
                System.err.print(parents.length);
                System.err.print(" ");
                System.err.println(nChildren);
                */



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


                try
                {
                    // ---------- Survivor selection
                    myPop.survival(childPop, rnd_,
                            survivalSelectionType, survivalSelectionParams);
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
