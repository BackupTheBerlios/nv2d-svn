package nv2d.graph;

/* TODO: This thing is majorly fucked up
 * This interface is used by NController to filter graphs. */
public interface FilterInterface {
	/**
	 * Provide the filter with a graph object and any arguments required.
	 */
	public void initialize(Graph g, Object [] args);
	
	/**
	 * Returns the last arguments given to this filter object.  This functionality
	 * should be provided to maintain consistency for the user interface; for
	 * example, if a {@link nv2d.graph.filter.DegreeFilter} was last set to
	 * 2 degrees, then changing the center vertex without touching the degree
	 * setting should then use 2 degrees again.
	 */
	public Object [] lastArgs();
	
	/**
	 * Run the filter and return the subgraph.
	 */
	public Graph filter();
}
