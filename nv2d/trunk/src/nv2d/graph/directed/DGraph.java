package nv2d.graph.directed;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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

	/** Remove a graph element from the graph. */
	public boolean remove(GraphElement ge) {
		if(ge.getClass() == DVertex.class) {
			_e.removeAll(((DVertex) ge).inEdges());
			_e.removeAll(((DVertex) ge).outEdges());
			_v.remove(ge);
			cleanupVertex(((DVertex) ge).neighbors());
			/* This return value is meaningless for now */
			return true;
		}
		if(ge.getClass() == DEdge.class) {
			_e.remove(ge);
			cleanupVertex((DVertex) ((DEdge) ge).getSource());
			cleanupVertex((DVertex) ((DEdge) ge).getDest());
			/* This return value is meaningless for now */
			return true;
		}

		/* TODO: need to add cases for other types here */
		throw new IllegalArgumentException("You must add a Directed graph element to a Directed Graph.");
	}

	/* The remove operation sometimes leave vertices with pointers to edges
	 * that no longer exist.  This method should be called to clean them up. */
	private void cleanupVertex(Set vertices) {
		Iterator i = vertices.iterator();
		while(i.hasNext()) {
			cleanupVertex((DVertex) i.next());
		}
	}

	private void cleanupVertex(DVertex v) {
		Iterator j = v.inEdges().iterator();
		while(j.hasNext()) {
			DEdge e = (DEdge) j.next();
			if(!_e.contains(e)) {
				v.removeEdge(e);
			}
		}
		j = v.outEdges().iterator();
		while(j.hasNext()) {
			DEdge e = (DEdge) j.next();
			if(!_e.contains(e)) {
				v.removeEdge(e);
			}
		}
	}

	public void clear() {
		_v.clear();
		_e.clear();
		_o.clear();
	}
}
