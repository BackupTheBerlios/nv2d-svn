package nv2d.graph;

import java.util.Set;

import cern.colt.Sorting;

public abstract class Graph extends DataStore {
	public abstract Set getEdges();
	public abstract Set getVertices();
	public abstract int numVertices();
	public abstract int numEdges();

	public abstract Graph subset(Set vertices);

	public abstract boolean add(GraphElement ge);
	public abstract boolean remove(GraphElement ge);
	public abstract void clear();
}
