package nv2d.graph;

import nv2d.utils.Pair;

public abstract class Edge extends GraphElement {
	public Edge(String id) {
		super(id);
	}

	// Accessors

	/** Get the length of this edge. */
	public abstract double length();

	/** Get the two ends of this edge.  This method returns a Pair containing
	 * two unique Edge objects. */
	public abstract Pair getEnds();

	/** Returns the Vertex opposite of the parameter Vertex on this edge. This
	 * method throws an exception if the parameter Vertex is not incident to
	 * this Edge. */
	public abstract Vertex getOpposite(Vertex v);

	// Modifiers

	/** Change the length of this edge.  The interface does not define a
	 * starting edge length. */
	public abstract void setLength(double l);
}
