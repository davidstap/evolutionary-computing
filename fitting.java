import java.util.HashMap;

public class fitting
{
    public static void run()
    {
        int evals = 0;
        int evaluations_limit_ = 5;
        
        int popSize = 2;
        
        /*
        HashMap<String, String> types = new HashMap<String, String>();
        types.put(FitPopulation.SETTABLE.PARENTSELECTION.toString(),
                Selection.TYPE.UNIFORM.toString());
        types.put(FitPopulation.SETTABLE.RECOMBINATION.toString(),
                Recombination.TYPE.ONEPOINT.toString());
        types.put(FitPopulation.SETTABLE.MUTATION.toString(),
                Mutation.TYPE.UNCORRELATED.toString());
        types.put(FitPopulation.SETTABLE.SURVIVALSELECTION.toString(),
                Selection.TYPE.MUCLAMBDA.toString());
        */
        
        /*
        FitPopulation mypop = new FitPopulation(popSize);
        evals = mypop.evaluate(evals, evaluations_limit_);
        
        System.out.println(">> START OF FITTING LOOP");
        while (evals < evaluations_limit_)
        {
            evals = mypop.evaluate(evals, evaluations_limit_);
        }
        */
        
        
        
    }
    
    public static void main(String[] args)
    {
        System.out.println(">> START OF FITTING");
        run();
        System.out.println(">> END OF FITTING");
    }
}
