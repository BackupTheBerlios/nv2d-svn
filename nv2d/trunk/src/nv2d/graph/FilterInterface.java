package nv2d.graph;

public interface FilterInterface {
	public void initialize(Graph g, Object [] args);
	public Graph filter(Graph g);
}
