package nv2d.graph;

import java.util.Set;

import cern.colt.Sorting;

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

	/* Modifiers */
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
