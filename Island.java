import java.util.Random;
import java.util.function.Function;

// FIXME ADDED TO FIX MERGE CONFLICT
import java.util.HashMap;


public class Island {
    private Population subpop;
    private Recombination.TYPE  recomb_method;
    private Mutation.TYPE mutation_method;
    private Selection.SELECTION_TYPE selection_method;
    private Selection.SURVIVAL_TYPE survival_method;
    private Random rnd;
    private Function<double[], Object> evaluationFunction_;

    // FIXME ADDED TO FIX MERGE CONFLICT
    private HashMap<String, Double> recomb_params =
            new HashMap<String, Double>();
    private HashMap<String, Double> survival_params =
            new HashMap<String, Double>();
    private HashMap<String, Double> selection_params =
            new HashMap<String, Double>();


    public Island (
            Recombination.TYPE recomb_method, Mutation.TYPE mutation_method, Selection.SELECTION_TYPE selection_method, Selection.SURVIVAL_TYPE survival_method, int n,
            Function<double[], Object> evaluationFunction_, Random rnd)
    {
        this.subpop = new Population(n, evaluationFunction_, rnd);
        this.recomb_method = recomb_method;
        this.mutation_method = mutation_method;
        this.selection_method = selection_method;
        this.survival_method = survival_method;
        this.rnd = rnd;
        this.evaluationFunction_ = evaluationFunction_;
    }

    // Full cycle of parent select, recombination, mutation
    // and survivor selection
    void evolutionCycle(int nChildren)
    {
        Population.Individual[] parents = this.parent_selection(nChildren);
        Population.Individual[] recombChildren = this.recombination(parents);
        Population childPop = new Population(
                recombChildren, this.evaluationFunction_);
        childPop = this.mutate(childPop);
        survival_selection(childPop);
    }

    void evaluation(int evals, int evaluation_limit)
    {
    	this.subpop.evaluate(evals, evaluation_limit);
    }

    // Mutate population of island
    private Population mutate(Population pop)
    {
        pop.mutate(this.rnd, this.mutation_method);
        return pop;
    }
    // Recombine parents of island to obtain children
    private Population.Individual[] recombination(
            Population.Individual[] parents)
    {
        return Recombination.recombination(parents, this.rnd, this.recomb_method, this.recomb_params);
    }

    private Population.Individual[] parent_selection(int nChildren)
    {
        // FIXME changed with addition of Selection.TYPE and Selection.PARAM
        selection_params.put(Selection.PARAM.PARENT_K.toString(),
                (double)nChildren);


        return this.subpop.parentSelection(this.rnd, this.selection_method, this.selection_params);
    }

//    TODO ADD DIFFERENT SURVIVAL SELECTION METHODS
    private void survival_selection(Population childPop)
    {
        // FIXME changed with addition of Selection.TYPE and Selection.PARAM
        this.subpop.survival(childPop, this.rnd, this.survival_method, this.survival_params);
        /*
        switch(this.survival_method)
        {
            case "":
                this.subpop.survival(childPop, this.rnd);
                break;
            default:
                System.out.println("No valid survival selection mechanism found");
        }
        */
    }

    Recombination.TYPE getRecombinationType()
    {
    	return this.recomb_method;
    }

    Mutation.TYPE getMutationType()
    {
    	return this.mutation_method;
    }

    Selection.SELECTION_TYPE getSelectionType()
    {
    	return this.selection_method;
    }

   	Selection.SURVIVAL_TYPE getSurvivalType() 
   	{
   		return this.survival_method;
   	}


    Population getPop()
    {
        return this.subpop;
    }

}
