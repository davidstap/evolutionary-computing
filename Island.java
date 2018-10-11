import java.util.Random;
import java.util.function.Function;

public class Island {
    private Population subpop;
    private Recombination.TYPE  recomb_method;
    private String mutation_method;
    private String selection_method;
    private String survival_method;
    private Random rnd;
    private Function<double[], Object> evaluationFunction_;

    public Island (
            Recombination.TYPE recomb_method, String mutation_method, String selection_method, String survival_method, int n,
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
        return Recombination.recombination(parents, this.rnd, this.recomb_method);
    }

    private Population.Individual[] parent_selection(int nChildren)
    {
        return this.subpop.parentSelection(nChildren, this.rnd, this.selection_method);
    }

//    TODO ADD DIFFERENT SURVIVAL SELECTION METHODS
    private void survival_selection(Population childPop)
    {
        switch(this.survival_method)
        {
            case "":
                this.subpop.survival(childPop, this.rnd);
                break;
            default:
                System.out.println("No valid survival selection mechanism found");
        }
    }
}
