import java.util.HashMap;
import java.util.Random;

import java.util.Arrays;

import java.io.IOException;

public class fitting
{
    static Random rnd_;

    public static void printException(Exception e)
    {
        System.out.print("\u001B[31m");
        System.out.print(e);
        System.out.print("\u001B[0m");
    }
    
    public static FitPopulation addInitToGenomes(
            FitPopulation pop, String fpath)
            throws IOException, InterruptedException
    {
        HashMap<String, Double> hm = InOut.load(InOut.command(fpath));
        String[] labels = pop.labels;
        Population.Individual[] individuals_ = pop.getIndividuals();
        Population.Individual[] individuals =
                new Population.Individual[individuals_.length];
        for (int i = 0; i < individuals_.length; i++)
        {
            double[] genome = individuals_[i].getGenome();
            for (int j = 0; j < genome.length; j++)
            {
                if (hm.containsKey(labels[j]))
                {
                    genome[j] = hm.get(labels[j]) + genome[j] / 5.0;
                }
            }
            individuals[i] = new Population.Individual(genome);
        }
        return new FitPopulation(individuals, pop.labels);
    }
    
    public static void run()
    {
        rnd_ = new Random();
        rnd_.setSeed(1);

        String bestOfFittingFile = "fitparams/bestOfFitting.params";
        String bestOfGenerationFile = "fitparams/bestOfGeneration.params";

        boolean print = true;

        int evals = 0;
        int evaluations_limit_ = 500;
        
        int popSize = 25;
        
        // Setting parent selection type and parameters.
        Selection.SELECTION_TYPE parentSelectionType = Selection.SELECTION_TYPE.TOURNAMENT;
        HashMap<String, Double> parentSelectionParams =
                new HashMap<String, Double>();
        parentSelectionParams.put(Selection.PARAM.PARENT_K.toString(),
                (double)popSize * 2);
//        parentSelectionParams.put(Selection.PARAM.ROUNDROBIN_Q.toString(),
//                (double)popSize / 2);
        parentSelectionParams.put(Selection.PARAM.TOURNAMENT_SIZE.toString(),
                2.0);

        // Setting recombination type and parameters.
        Recombination.TYPE recombinationType = Recombination.TYPE.ONEPOINT;
        HashMap<String, Double> recombinationParams =
                new HashMap<String, Double>();

        // Setting mutation type and parameters.
        Mutation.TYPE mutationType = Mutation.TYPE.UNCORRELATED;
        HashMap<String, Double> mutationParams = new HashMap<String, Double>();
        mutationParams.put(Mutation.PARAM.MUTATIONRATE.toString(),
                2.0 / Population.dim);

        // Setting survival selection type and parameters.
        Selection.SURVIVAL_TYPE survivalSelectionType = Selection.SURVIVAL_TYPE.MUCOMMALAMBDA;
        HashMap<String, Double> survivalSelectionParams =
                new HashMap<String, Double>();

        /* XXX NOT USED ANYMORE
        // Setting population parameters.
        HashMap<String, Double> fittingParams =
                new HashMap<String, Double>();
        fittingParams.put(Population.PARAM.SIZE.toString(),
                12.0);
        fittingParams.put(Population.PARAM.NCHILDREN.toString(),
                6.0);
        fittingParams.put(Selection.PARAM.PARENT_K.toString(),
                6.0);
        fittingParams.put(Mutation.PARAM.MUTATIONRATE.toString(),
                1.0 / Population.dim);
        */

        String[] paramLabels = {
            Population.PARAM.SIZE.toString(),
//            Population.PARAM.NCHILDREN.toString(),
            Selection.PARAM.PARENT_K.toString(),
//            Selection.PARAM.ROUNDROBIN_Q.toString(),
            Selection.PARAM.TOURNAMENT_SIZE.toString(),
            Mutation.PARAM.MUTATIONRATE.toString(),
            Mutation.PARAM.UNCORRELATED_THETA.toString(),
            Mutation.PARAM.UNCORRELATED_THETA_.toString(),
            Mutation.PARAM.UNCORRELATED_ETHA.toString()
        };

        // Initialize population
        FitPopulation myPop = new FitPopulation(popSize, rnd_, paramLabels);
        try
        {
//            myPop = addInitToGenomes(
//                    myPop, "cat fitparams/good_fittedSchaffers.params");
            evals = myPop.evaluate(evals, evaluations_limit_, rnd_);
        }
        catch (IOException e)
        {
            printException(e);
        }
/*
        catch (InterruptedException e)
        {
            printException(e);
        }
*/

        if (myPop.size() == popSize) {
            // calculate fitness
            while(evals < evaluations_limit_){

                try
                {
                    myPop.sort();

                    if (print)
                    {
                        myPop.print();
                    }

                    System.err.println(Integer.toString(evals) +
                            " || Best fitness: " +
                            Double.toString(myPop.getIndividuals()[0].fitness));

                    // ---------- Save best of generation ----------
                    ((FitPopulation.FitIndividual)myPop.getIndividuals()[0]
                            ).saveGenome(bestOfGenerationFile);

                    // ---------- Parent Selection ----------
                    Population.Individual[] parents =
                            myPop.parentSelection(rnd_,
                            parentSelectionType, parentSelectionParams);

                    // ---------- Recombination ----------
                    Population.Individual[] children;
                    children = myPop.recombination(parents, rnd_,
                            recombinationType, recombinationParams);
                    FitPopulation childPop = new FitPopulation(
                            children, paramLabels);

                    // ---------- Mutation ----------
                    childPop.mutate(rnd_, mutationType, mutationParams);
                    evals = childPop.evaluate(evals, evaluations_limit_, rnd_);

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
                catch (IOException e)
                {
                    printException(e);
                }

            }
        }

        if (print)
        {
            myPop.sort();
            myPop.print();
            System.out.println();
        }


        try
        {
            myPop.sort();
            ((FitPopulation.FitIndividual)myPop.getIndividuals()[0]).saveGenome(
                    bestOfFittingFile);
        }
        catch (IOException e)
        {
            printException(e);
        }


    }

    public static void main(String[] args)
    {
        run();
    }
}
