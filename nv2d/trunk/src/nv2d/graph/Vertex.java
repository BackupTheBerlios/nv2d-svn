package nv2d.graph;

import java.lang.ClassCastException;
import java.lang.IllegalArgumentException;

import java.util.Set;

public abstract class Vertex extends GraphElement {
	public Vertex(String id) {
		super(id);
	}

	/** This should return all edges for undirected graphs */
	public abstract Set inEdges();

	/** This should return all edges for undirected graphs.  For undirected
	 * graphs, this method should return the same object as
	 * <code>inEdges()</code>. */
	public abstract Set outEdges();

	/** Return all nodes which share an edge with this node. */
	public abstract Set neighbors();

	/** A valid edge parameter must at least contain this vertex.  This is
	 * protected, only <code>Graph</code> should be able to access this. */
	//abstract void addInEdge(Edge e);

	/** A valid edge parameter must at least contain this vertex.  This is
	 * protected, only <code>Graph</code> should be able to access this. */
	//abstract void addOutEdge(Edge e);
}
