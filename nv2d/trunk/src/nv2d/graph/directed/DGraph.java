package nv2d.graph.directed;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.lang.IllegalArgumentException;

// import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import nv2d.algorithms.APSPInterface;
import nv2d.algorithms.shortestpaths.Dijkstra;
import nv2d.exceptions.NoPathExists;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.graph.Path;
import nv2d.graph.Vertex;

public class DGraph extends Graph {
	protected Set _e;	// edges
	protected Set _v;	// vertices
	protected Set _o;	// other graph elements

	/* If this variable is zero, the cache is invalid.  If the value is
	 * non-zero, the cache is valid.  Any modifications to the graph should
	 * invalidate the cache.  If the cache is invalidated, the method
	 * <code>minEdgeLength()</code> needs to run through all the edges and
	 * check for the minimum length. */
	private double _minEdgeLengthCache;

	/** Keeps track of shortest paths in the graph using an all pairs shortest
	 * path algorithm.  It is important to make sure that the algorithm is
	 * re-run after a change to the graph. */
	private APSPInterface _shortestPaths;

	/** This variable keeps track of the last source used in a
	 * shortestPathLen() call.  */
	private boolean _shortestPathsCache;


	public DGraph() {
		_e = (Set) (new HashSet());
		_v = (Set) (new HashSet());
		_o = (Set) (new HashSet());

		_shortestPaths = new Dijkstra(this);
		_minEdgeLengthCache = 0;
		_shortestPathsCache = false;
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

	public Graph newInstance() {
		return new DGraph();
	}

	public double edgeLen(Vertex source, Vertex dest) {
		if (!source.neighbors().contains(dest)) {
			return 0.0;
		}
		Set edges = source.outEdges();
		Iterator i = edges.iterator();
		while(i.hasNext()) {
			Edge e = (Edge) i.next();
			if (e.getOpposite(source).equals(dest)) {
				return e.length();
			}
		}
		// we should never reach here.
		assert(false);
		return 0.0;
	}

	public double shortestPathLen(Vertex source, Vertex dest) {
		Path p = null;
		if(!_shortestPathsCache) {
			_shortestPaths.init(this, source);
			_shortestPaths.run();
		}
		try {
			p = _shortestPaths.getPath(source, dest);
		}
		catch (NoPathExists e) {
			return 0.0;
		}

		return p.totalLength();
	}

	public double minEdgeLength() {
		if(_minEdgeLengthCache != 0) {
			return _minEdgeLengthCache;
		}

		Iterator i = _e.iterator();
		while(i.hasNext()) {
			DEdge e = (DEdge) i.next();
			if(e.length() < _minEdgeLengthCache) {
				_minEdgeLengthCache = e.length();
			}
		}
		return _minEdgeLengthCache;
	}

	public boolean add(GraphElement ge) {
		// if(ge.getClass() == DVertex.class) {
		if(ge instanceof DVertex) {
			ge.setParent(this);
			// invalidate _minEdgeLengthCache
			_minEdgeLengthCache = 0;
			_shortestPathsCache = false;
			return _v.add(ge);
		}
		// if(ge.getClass() == DEdge.class) {
		if(ge instanceof DEdge) {
			// update the affected nodes - ouch. looks like lisp
			((DVertex) ((DEdge) ge).getDest()).addInEdge( (DEdge) ge );
			((DVertex) ((DEdge) ge).getSource()).addOutEdge( (DEdge) ge );
			ge.setParent(this);
			// invalidate _minEdgeLengthCache
			_minEdgeLengthCache = 0;
			_shortestPathsCache = false;
			return _e.add(ge);
		}

		/* TODO: need to add cases for other types here */
		throw new IllegalArgumentException("You must add a Directed graph element to a Directed Graph.");
	}

	public Graph subset(Set graphelements) {
		// filter for those edges which contain only the vertices we want
		DGraph g = new DGraph();
		Iterator i = graphelements.iterator();
		while(i.hasNext()) {
			Object o = i.next();
			if(o instanceof Vertex) {
				g.add((Vertex) o);
			} else if(o instanceof Edge) {
				g.add((Vertex) ((Edge) o).getEnds().car());
				g.add((Vertex) ((Edge) o).getEnds().cdr());
				g.add((Edge) o);
			}
		}

		// add any additional edges for the vertices which were not
		// included
		i = getEdges().iterator();
		while(i.hasNext()) {
			Edge e = (Edge) i.next();
			if(graphelements.contains(e.getEnds().car()) && graphelements.contains(e.getEnds().cdr())) {
				g.add(e);
			}
		}
		return g;
	}

	/** Remove a graph element from the graph. */
	public boolean remove(GraphElement ge) {
		if(ge.getClass() == DVertex.class) {
			_e.removeAll(((DVertex) ge).inEdges());
			_e.removeAll(((DVertex) ge).outEdges());
			_v.remove(ge);
			cleanupVertex(((DVertex) ge).neighbors());
			// invalidate _minEdgeLengthCache
			_minEdgeLengthCache = 0;
			_shortestPathsCache = false;

			/* This return value is meaningless for now */
			return true;
		}
		if(ge.getClass() == DEdge.class) {
			_e.remove(ge);
			cleanupVertex((DVertex) ((DEdge) ge).getSource());
			cleanupVertex((DVertex) ((DEdge) ge).getDest());
			// invalidate _minEdgeLengthCache
			_minEdgeLengthCache = 0;
			_shortestPathsCache = false;

			/* This return value is meaningless for now */
			return true;
		}

		/* TODO: need to add cases for other types here */
		throw new IllegalArgumentException("You must add a Directed graph element to a Directed Graph.");
	}

	private void cleanupVertex(Set vertices) {
		Iterator i = vertices.iterator();
		while(i.hasNext()) {
			cleanupVertex((DVertex) i.next());
		}
	}

	/* The remove operation sometimes leave vertices with pointers to edges
	 * that no longer exist.  This method should be called to clean them up. */
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
		/* removing all the nodes using the remove() method should handle
		 * clearing all the object references. */
		Iterator i = _v.iterator();

		_minEdgeLengthCache = 0;
		_shortestPathsCache = false;

		while(i.hasNext()) {
			remove((GraphElement) i.next());
		}
	}
}
