package nv2d.graph;

import java.util.Set;

public abstract class Graph extends DataStore {
	/* Accessors */
	/** Get a set of the edges in the graph. */
	public abstract Set getEdges();

	/** Get a set of the vertices in the graph. */
	public abstract Set getVertices();
	
	/** Returns the number of unique vertices in the graph. */
	public abstract int numVertices();

	/** Returns the number of unique edges in the graph. */
	public abstract int numEdges();

	/** Returns a subset of the graph containing only the vertices in the
	 * parameter Set. */
	public abstract Graph subset(Set vertices);

	/** Find the edge length between two vertices.  If they are not
	 * adjacent, return 0. */
	public abstract double edgeLen(Vertex source, Vertex dest);

	/* Modifiers */

	/** Find the shortest path length between 2 vertices.  Calculate the
	 * shortest paths using one of the algorithms provided in
	 * nv2d.algorithms.shortestpaths package. */
	public abstract double shortestPathLen(Vertex source, Vertex dest);

	/** Add a <code>GraphElement</code> to the graph.  Note that unless there
	 * is a class which knows how to render the graph element, it will not be
	 * shown. */
	public abstract boolean add(GraphElement ge);

	/** Remove a <code>GraphElement</code> from the graph. */
	public abstract boolean remove(GraphElement ge);

	/** Clear all the data contained in this graph.  This includes objects of
	 * the type <code>Datum</code> and <code>GraphElement</code>. */
	public abstract void clear();
}
