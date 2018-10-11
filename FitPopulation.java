import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.Map;

public class FitPopulation
{
    public enum SETTABLE {
        PARENTSELECTION, RECOMBINATION, MUTATION, SURVIVALSELECTION
    }
    
    private Individual[] individuals;
    
    public FitPopulation(
        int size,
        Map<String, String> types,
        Map<String, Map<String, Double>> args
    )
    {
        individuals = new Individual[size];
        for (int i = 0; i < size; i++)
        {
            individuals[i] = new Individual();
        }
    }
    
    // Evaluates members of population keeping track of number of total
    // evaluations, returns new total number of evaluations. If evaluations
    // limit is exceeded, deletes not-evaluated individuals from population.
    public int evaluate(int evals, int evaluations_limit)
    {
        for (int i = 0; i < individuals.length; i++)
        {
            // Check for exceeding of evaluations limit.
            if (evals >= evaluations_limit)
            {
                // Discard not-evaluated part of population.
                if (i == 0)
                {
                    individuals = new Individual[0];
                }
                else
                {
                    individuals = Arrays.copyOfRange(individuals, 0, i);
                }
                break;
            }
            // Evaluate individual.
            individuals[i].evaluate();
            evals++;
        }
        return evals;
    }
    
    public class Individual implements Comparable<Individual>
    {
        
        public double fitness;
        
        public Individual()
        {
            fitness = 0;
        }
        
        // https://stackoverflow.com/questions/26830617/
        //      java-running-bash-commands
        private String system(String cmd)
                throws IOException, InterruptedException
        {
            Process p;
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(p.getInputStream()));
            StringBuffer output = new StringBuffer();
            
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            return output.toString();
        }
        
        public void evaluate()
        {
            fitness++;
            
            try
            {
                System.out.print(system("echo " + Double.toString(fitness)));
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
        
        @Override
        public int compareTo(Individual individual)
        {
            return Double.compare(fitness, individual.fitness);
        }
        
    }
    
}
