/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package nv2d.graph.directed;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nv2d.algorithms.APSPInterface;
import nv2d.algorithms.shortestpaths.Dijkstra;
import nv2d.exceptions.NoPathExists;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.graph.Path;
import nv2d.graph.Vertex;

public class DGraph extends Graph implements Serializable {
	protected Set _e;	// edges
	protected Set _v;	// vertices

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

	/** This variable keeps track of whether things have been indecized */
	private boolean _indecized;
	private Object [] _vertexIndex;

	/** This variable keeps track of the last source used in a
	 * shortestPathLen() call.  */
	private boolean _shortestPathsCache;
	private Vertex _apspSource;


	public DGraph() {
		_e = (Set) (new HashSet());
		_v = (Set) (new HashSet());

		_shortestPaths = new Dijkstra(this);
		_minEdgeLengthCache = 0;
		_shortestPathsCache = false;
		_apspSource = null;
		_indecized = false;
	}

	public Set getEdges() {
		return _e;
	}

	public Set getVertices() {
		return _v;
	}

	public int numVertices() {
		return _v.size();
	}

	public int numEdges() {
		return _e.size();
	}

	public boolean isDirected() {
		return true;
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

	public Path shortestPath(Vertex source, Vertex dest) {
		Path p = null;
		if(!_shortestPathsCache || !source.equals(_apspSource)) {
			_shortestPaths.init(this, source);
			_shortestPaths.run();
		}
		try {
			p = _shortestPaths.getPath(source, dest);
		}
		catch (NoPathExists e) {
			return null;
		}
		_apspSource = source;
		return p;
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
		_indecized = false;
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

		System.err.println("You must add a Directed graph element to a Directed Graph.");
		return false;
	}

	/*// this method was generalized (and moved to Graph) testing in progress
	public Graph subset(Set graphelements) {
		// filter for those edges which contain only the vertices we want
		DGraph g = new DGraph();
		Hashtable edges = new Hashtable(); // originals
		Iterator i = graphelements.iterator();

		// collect all the graph elements and catalog them
		while(i.hasNext()) {
			Object o = i.next();
			String id = ((GraphElement) o).id();
			if(_v.contains(o) || _e.contains(o)) {
				if(o instanceof Vertex && null == g.findVertex(id)) {
					g.add(((Vertex) o).clone(g));
				} else if(o instanceof Edge) {
					// add the endpoints of the edges if necessary
					Vertex tsource = (Vertex) ((Edge) o).getEnds().car();
					Vertex tdest = (Vertex) ((Edge) o).getEnds().cdr();
					if(null == g.findVertex(tsource.id())) {
						g.add(tsource.clone(g));
					}
					if(null == g.findVertex(tdest.id())) {
						g.add(tdest.clone(g));
					}
					// clone the edge
					edges.put(id, ((Edge) o).clone(g));
				}
				// should never reach this point
				assert(false);
			} else {
				// was not part of the edge
				assert(false);
			}
		}

		// collect any additional edges for the vertices which were not included
		i = getEdges().iterator();
		while(i.hasNext()) {
			Edge e = (Edge) i.next();
			if(
				!edges.containsKey(e.id()) &&
				null != g.findVertex(((Vertex) e.getEnds().car()).id()) &&
				null != g.findVertex(((Vertex) e.getEnds().cdr()).id())
			) {
				edges.put(e.id(), e.clone(g));
			}
		}

		// insert cloned edges into graph
		i = edges.values().iterator();
		while(i.hasNext()) {
			g.add((Edge) i.next());
		}

		return g;
	}
	*/

	/** Remove a graph element from the graph.  TODO: has not been properly tested */
	public boolean remove(GraphElement ge) {
		_indecized = false;
		if(_e.contains(ge) || _v.contains(ge)) {
			ge.setParent(null);
		} else {
			return false;
		}

		if(ge instanceof DVertex) {
			_e.removeAll(((DVertex) ge).inEdges());
			_e.removeAll(((DVertex) ge).outEdges());
			_v.remove(ge);
			cleanupVertex(((DVertex) ge).neighbors());
			// invalidate _minEdgeLengthCache
			_minEdgeLengthCache = 0;
			_shortestPathsCache = false;

			return true;
		}
		if(ge instanceof DEdge) {
			_e.remove(ge);
			cleanupVertex((DVertex) ((DEdge) ge).getSource());
			cleanupVertex((DVertex) ((DEdge) ge).getDest());
			// invalidate _minEdgeLengthCache
			_minEdgeLengthCache = 0;
			_shortestPathsCache = false;

			return true;
		}

		return false;
	}

	public Vertex findVertex(String id) {
		if(!_indecized) {
			indecize();
		}
		int idx = Arrays.binarySearch(_vertexIndex, new DVertex(id));
		if(idx < 0) {
			return null;
		}
		return (Vertex) _vertexIndex[idx];
	}

	private void cleanupVertex(Set vertices) {
		_indecized = false;
		Iterator i = vertices.iterator();
		while(i.hasNext()) {
			cleanupVertex((DVertex) i.next());
		}
	}

	/* The remove operation sometimes leave vertices with pointers to edges
	 * that no longer exist.  This method should be called to clean them up. */
	private void cleanupVertex(DVertex v) {
		_indecized = false;
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

	/* Organize all the vertices into an array which can be searched. */
	private void indecize() {
		_vertexIndex = getVertices().toArray();
		Arrays.sort(_vertexIndex);
		_indecized = true;
	}

	public void clear() {
		/* removing all the nodes using the remove() method should handle
		 * clearing all the object references. */
		Iterator i = _v.iterator();

		_indecized = false;

		_minEdgeLengthCache = 0;
		_shortestPathsCache = false;

		while(i.hasNext()) {
			remove((GraphElement) i.next());
		}
	}
}
