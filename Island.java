import java.util.Random;
import java.util.function.Function;

public class Island {
    private Population subpop;
    private String recomb_method;
    private String mutation_method;
    private Random rnd;
    private Function<double[], Object> evaluationFunction_;

    public Island (
            String recomb_method, String mutation_method, int n,
            Function<double[], Object> evaluationFunction_, Random rnd)
    {
        this.subpop = new Population(n, evaluationFunction_, rnd);
        this.recomb_method = recomb_method;
        this.mutation_method = mutation_method;
        this.rnd = rnd;
        this.evaluationFunction_ = evaluationFunction_;
    }

    // Full cycle of parent select, recombination, mutation
    // and survivor selection
    void evolutionCycle(int nChildren)
    {
        Population.Individual[] parents = this.subpop.parentSelection(nChildren);
        Population.Individual[] recombChildren = this.recombination(parents);
        int[] N = {nChildren};
        Population childPop = new Population(
                recombChildren, this.evaluationFunction_);
        childPop = this.mutate(childPop);
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
        switch(this.recomb_method)
        {
            case "simpleArithmetic":
                return Recombination.recombination(
                        parents, this.rnd, Recombination.TYPE.SIMPLEARITHMETIC);
            case "uniform":
                return Recombination.recombination(
                        parents, this.rnd, Recombination.TYPE.UNIFORM);
            case "wholeArithmetic":
                return Recombination.recombination(
                        parents, this.rnd, Recombination.TYPE.WHOLEARITHMETIC);
            default:
                System.out.println("No valid recombination method selected");
        }
        return null;
    }
}
