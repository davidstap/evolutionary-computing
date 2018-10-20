import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class IslandList {
    private ArrayList<Island> island_list;
    private int nChildren;

    public IslandList(int nChildren)
    {
        this.island_list = new ArrayList<Island>();
        this.nChildren = nChildren;
    }

    // Migrate after fixed number of generations
    // Most authors have used epoch lengths of the range 25 to 150 generations.
    // How many migrate: usually 2 to 5
    // How: random(!), best, or pick from fittest half
    // Move(!) or copy
    void migration()
    {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        int n = island_list.size();
        for(int i = 0; i < n; i++)
        {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        for(int i = 0; i < n; i+=2)
        {
            Random rnd = new Random();
            Population pop1 = island_list.get(i).getPop();
            Population pop2 = island_list.get(i+1).getPop();

            int i1 = rnd.nextInt(pop1.size());
            int i2 = rnd.nextInt(pop2.size());

            Population.Individual candidate_1 = pop1.getIndividuals()[i1];
            Population.Individual candidate_2 = pop2.getIndividuals()[i2];

            pop1.changeIndividual(i1, candidate_2);
            pop2.changeIndividual(i2, candidate_1);
        }
    }

    void addIsland(Island island)
    {
        this.island_list.add(island);
    }

    void removeIsland(int i)
    {
        this.island_list.remove(i);
    }

    Island getIsland(int i)
    {
        return this.island_list.get(i);
    }

    ArrayList<Island> getAllIslands()
    {
        return this.island_list;
    }

    // Amount of evolution cycles : n
    void evolveIslands(int n)
    {
        for(int i = 0; i < n; i++)
        {
            for(Island island : this.island_list)
            {
                island.evolutionCycle(this.nChildren);
            }
        }
    }

    void evaluateIslands(int evals, int evaluations_limit)
    {
        for(Island island: this.island_list)
        {
            island.evaluation(evals, evaluations_limit);
        }
    }
}
