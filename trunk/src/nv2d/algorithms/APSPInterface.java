package nv2d.algorithms;

import nv2d.exceptions.NoPathExists;
import nv2d.graph.Graph;
import nv2d.graph.Path;
import nv2d.graph.Vertex;

/** This interface is for all-pairs shortest path implementation. */

public interface APSPInterface {
	/** Get the shortest path between two nodes. 
	 * If the 
	 * */
	public Path getPath(Vertex source, Vertex dest) throws NoPathExists;

	/** Prepare a graph for the algorithm. */
	public void init(Graph g, Vertex s);

	/** Execute the algorithm.  The client should keep track of all changes
	 * made to the graph and re-run this as necessary. */
	public void run();
}
