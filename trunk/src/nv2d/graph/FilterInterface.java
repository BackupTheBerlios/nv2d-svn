package nv2d.graph;

/* TODO: This thing is majorly fucked up
 * This interface is used by NController to filter graphs. */
public interface FilterInterface {
	public void initialize(Graph g, Object [] args);
	public Graph filter();
}
