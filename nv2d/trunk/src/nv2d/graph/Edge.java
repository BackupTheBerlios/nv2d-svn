package nv2d.graph;

import nv2d.utils.Pair;

public abstract class Edge extends GraphElement {
	public Edge(String id) {
		super(id);
	}

	public abstract float length();
	public abstract void setLength(float l);
	public abstract Pair getEnds();
	public abstract Vertex getOpposite(Vertex v);
}
