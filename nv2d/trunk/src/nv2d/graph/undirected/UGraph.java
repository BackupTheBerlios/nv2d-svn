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

package nv2d.graph.undirected;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.graph.Vertex;
import nv2d.graph.Path;
import nv2d.utils.Pair;

public class UGraph extends nv2d.graph.Graph {
	private Set _e;
	private Set _v;

	private boolean _indecized;
	private Object [] _vertexIndex;
	
	public UGraph() {
		_e = new HashSet();
		_v = new HashSet();
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
		return false;
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

	public Graph newInstance() {
		return new UGraph();
	}

	public Vertex findVertex(String id) {
		if(!_indecized) {
			indecize();
		}
		int idx = Arrays.binarySearch(_vertexIndex, new UVertex(id));
		if(idx < 0) {
			return null;
		}
		return (Vertex) _vertexIndex[idx];
	}

	/* TODO */
	public Path shortestPath(Vertex source, Vertex dest) {
		return null;
	}

	public boolean add(GraphElement ge) {
		_indecized = false;
		// if(ge.getClass() == DVertex.class) {
		if(ge instanceof UVertex) {
			ge.setParent(this);
			return _v.add(ge);
		}
		// if(ge.getClass() == DEdge.class) {
		if(ge instanceof UEdge) {
			Pair ends = ((UEdge) ge).getEnds();
			((UVertex) ends.car()).addEdge((UEdge) ge);
			((UVertex) ends.cdr()).addEdge((UEdge) ge);
			ge.setParent(this);
			return _e.add(ge);
		}

		System.err.println("You must add an undirected graph element to an undirected Graph.");
		return false;
	}

	/* TODO: unfinished */
	public boolean remove(GraphElement ge) {
		if(!_v.contains(ge) || !_e.contains(ge)) {
			return false;
		}
		
		if(ge instanceof UVertex) {
			// outEdges() is identical to inEdges()
			Iterator i = ((UVertex) ge).outEdges().iterator();
			while(i.hasNext()) {
				removeEdge((UEdge) i.next());
			}
			_v.remove(ge);
		} else if (ge instanceof UEdge) {
			removeEdge((UEdge) ge);
		}
		return false;
	}
	
	/** Remove an edge from this graph.  This method assumes that the edge exists.
	 * 
	 * @param e the <code>UEdge</code> to remove
	 */
	private void removeEdge(UEdge e) {
		((UVertex) ((UEdge) e).getCar()).removeEdge(e);
		((UVertex) ((UEdge) e).getCdr()).removeEdge(e);
		_e.remove(e);
	}

	public void clear() {
		_e = new HashSet();
		_v = new HashSet();

		_indecized = false;
		_vertexIndex = null;
	}
	
	private void indecize() {
		_vertexIndex = getVertices().toArray();
		Arrays.sort(_vertexIndex);
		_indecized = true;
	}
}
