import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;

public class FitPopulation extends Population
{
    private static final int nRuns = 3;
    private static final String tmpFile = "fitparams/currentEvaluation.params";
    
    public String[] labels;
    
    public FitPopulation(int size, Random rnd, String[] labels_)
    {
        dim = labels_.length;
        labels = labels_;
        individuals = new FitIndividual[size];
        for (int i = 0; i < individuals.length; i++)
        {
            individuals[i] = new FitIndividual(rnd, labels_);
        }
    }
    
    public FitPopulation(Individual[] individuals_, String[] labels_)
    {
        dim = labels_.length;
        labels = labels_;
        individuals = new FitIndividual[individuals_.length];
        for (int i = 0; i < individuals.length; i++)
        {
            individuals[i] = new FitIndividual(individuals_[i], labels_);
        }
    }
    
    // Evaluates members of population keeping track of number of total
    // evaluations, returns new total number of evaluations. If evaluations
    // limit is exceeded, deletes not-evaluated individuals from population.
    public int evaluate(int evals, int evaluations_limit, Random rnd)
            throws IOException
    {
        for (int i = 0; i < individuals.length; i++)
        {
            // Check for exceeding of evaluations limit.
            if (evals >= evaluations_limit)
            {
                // Discard not-evaluated part of population.
                if (i == 0)
                {
                    individuals = new FitIndividual[0];
                }
                else
                {
                    individuals = Arrays.copyOfRange(individuals, 0, i);
                }
                break;
            }
            // Evaluate individual.
            ((FitIndividual)individuals[i]).evaluate(rnd);
            evals++;
            System.err.println(Integer.toString(evals));
        }
        return evals;
    }
    
    public class FitIndividual extends Population.Individual
    {
        // Set minR and maxR based on parameter label.
        public void setLimits(String[] labels)
        {
            for (int i = 0; i < genome.length; i++)
            {
                if (labels[i] == Population.PARAM.SIZE.toString()
                        || labels[i] == Population.PARAM.NCHILDREN.toString()
                        || labels[i] == Selection.PARAM.PARENT_K.toString()
                        || labels[i] == Selection.PARAM.ROUNDROBIN_Q.toString()
                        || labels[i] ==
                        Selection.PARAM.SURVIVALROUNDROBIN_Q.toString())
                {
                    minR[i] = 300.0;
                    maxR[i] = minR[i];
                }
                else if (labels[i] == Selection.PARAM.TOURNAMENT_SIZE.toString())
                {
                    minR[i] = 2.0;
                    maxR[i] = minR[i];
                }
                else if (labels[i] == Mutation.PARAM.MUTATIONRATE.toString()
                        || labels[i] == Recombination.PARAM.ALPHA.toString()
                        // XXX is it useful to keep these small?
                        || labels[i] ==
                        Mutation.PARAM.UNCORRELATED_THETA.toString()
                        || labels[i] ==
                        Mutation.PARAM.UNCORRELATED_THETA_.toString()
                        || labels[i] ==
                        Mutation.PARAM.UNCORRELATED_ETHA.toString())
                {
                    minR[i] = 0.0;
                    maxR[i] = 1.0;
                }
                else
                {
                    minR[i] = 0;
                    maxR[i] = -1;
                }
            }
        }
        
        public FitIndividual(Random rnd, String[] labels)
        {
            for (int i = 0; i < genome.length; i++)
            {
                setLimits(labels);
                // Set random starting genome based on given range.
                if (minR[i] > maxR[i])
                {
                    genome[i] = rnd.nextDouble();
                }
                else if (minR[i] == maxR[i])
                {
                    if (labels[i] == Selection.PARAM.TOURNAMENT_SIZE.toString())
                    {
                        genome[i] = minR[i] + rnd.nextDouble() * 5;
                    }
                    else
                    {
                        genome[i] = minR[i] + rnd.nextDouble() * 200;
                    }
                }
                else
                {
                    genome[i] = (minR[i] + maxR[i]) / 2.0 +
                            (rnd.nextDouble() - 0.5) * (maxR[i] - minR[i]);
                }
                sigmas[i] = minR[i] / 2.0 + 1;
            }
        }
        
        public FitIndividual(Individual individual, String[] labels)
        {
            setLimits(labels);
            genome = individual.genome.clone();
            sigmas = individual.sigmas.clone();
            set_fitness(individual.fitness, individual.shared_fitness);
            prop_fitness = individual.prop_fitness;
            selectionRanking = individual.selectionRanking;
            rank = individual.rank;
        }
        
        public void saveGenome(String fpath)
                throws IOException
        {
            InOut.save(labels, genome, fpath);
        }
        
        public void evaluate(Random rnd)
                throws IOException
        {
            try
            {
                double[] runs = new double[nRuns];
                saveGenome(tmpFile);
                for (int i = 0; i < nRuns; i++)
                {
                    String cmdOut = InOut.command("make testk SEED=" +
                            Integer.toString(rnd.nextInt(Integer.MAX_VALUE)) +
                            " PARAMSFILE=" + tmpFile);
                    BufferedReader reader = new BufferedReader(
                            new StringReader(cmdOut));
                    String line;
                    boolean found = false;
                    while ((line = reader.readLine()) != null)
                    {
                        if (line.startsWith("Score: "))
                        {
                            runs[i] = Double.parseDouble(
                                    line.substring(line.lastIndexOf(" ") + 1,
                                    line.length()));
                            found = true;
                        }
                    }
                }
                double tmp_fitness = 0.0;
                for (double value : runs)
                {
                    tmp_fitness += value;
                }
                tmp_fitness /= nRuns;
                double tmp = 0.0;
                for (double value : runs)
                {
                    tmp += Math.pow((value - tmp_fitness), 2);
                }
                tmp_fitness -= (Math.sqrt(tmp / (nRuns - 1.0)));
                set_fitness(tmp_fitness);
            }
            catch (IOException e)
            {
                System.out.println(e);
                System.exit(1);
            }
            catch (InterruptedException e)
            {
                System.out.println(e);
                System.exit(1);
            }
        }
        
        // Returns clone of individuals genome.
        public double[] getGenome()
        {
            return genome.clone();
        }
    }
    
}
