public class Children
{
    private double[] child1;
    private double[] child2;
    private Population pop;

    public Children(double[] child1, double[] child2)
    {
        this.child1 = child1;
        this.child2 = child2;
        this.pop = new Population();
    }

    public Population.Individual getChild1()
    {
    	return pop.new Individual(child1);
    }

	public Population.Individual getChild2()
    {
    	return pop.new Individual(child2);
    }
}