package nv2d.graph;

/* TODO: This thing is majorly fucked up */
public interface FilterInterface {
	public void initialize(Graph g, Object [] args);
	public Graph filter(Graph g);
}
