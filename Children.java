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

    public Population.Unit getChild1()
    {
    	return pop.new Unit(child1);
    }

	public Population.Unit getChild2()
    {
    	return pop.new Unit(child2);
    }
}