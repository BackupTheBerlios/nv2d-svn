package nv2d.graph.directed;

import java.util.Set;
import java.util.HashSet;
import java.lang.IllegalArgumentException;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import nv2d.graph.Graph;
import nv2d.graph.GraphElement;

public class DGraph extends Graph {
	protected Set _e;	// edges
	protected Set _v;	// vertices
	protected Set _o;	// other graph elements

	public DGraph() {
		_e = (Set) (new HashSet());
		_v = (Set) (new HashSet());
		_o = (Set) (new HashSet());
	}

	public Set getEdges() {
		return _e;
	}

	public Set getVertices() {
		return _v;
	}

	public Set getOtherGraphElements() {
		return _o;
	}

	public int numVertices() {
		return _v.size();
	}

	public int numEdges() {
		return _e.size();
	}

	public boolean add(GraphElement ge) {
		if(ge.getClass() == DVertex.class) {
			ge.setParent(this);
			return _v.add(ge);
		}
		if(ge.getClass() == DEdge.class) {
			// ouch. looks like lisp
			((DVertex) ((DEdge) ge).getDest()).addInEdge( (DEdge) ge );
			((DVertex) ((DEdge) ge).getSource()).addOutEdge( (DEdge) ge );
			ge.setParent(this);
			return _e.add(ge);
		}

		/* TODO: need to add cases for other types here */
		throw new IllegalArgumentException("You must add a Directed graph element to a Directed Graph.");
	}

	public Graph subset(Set vertices) {
		/* NOT IMPLEMENTED */
		return null;
	}

	public boolean remove(GraphElement ge) {
		if(ge.getClass() == DVertex.class) {
			return _v.remove(ge);
		}
		if(ge.getClass() == DEdge.class) {
			return _e.remove(ge);
		}

		/* TODO: need to add cases for other types here */
		throw new IllegalArgumentException("You must add a Directed graph element to a Directed Graph.");
	}

	public void clear() {
		_v.clear();
		_e.clear();
		_o.clear();
	}
}
