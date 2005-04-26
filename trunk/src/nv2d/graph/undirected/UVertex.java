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

import java.io.Serializable;
import java.lang.IllegalArgumentException;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import nv2d.graph.Vertex;
import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;

public class UVertex extends Vertex implements Serializable {
	// the intersection of the following two sets should be null
	transient private Set _edges;

	public UVertex(String id) {
		super(id);
		_edges = new HashSet();
	}

	public Set inEdges() {
		return _edges;
	}

	public Set outEdges() {
		return _edges;
	}

	public Set neighbors() {
		HashSet set = new HashSet();
		Iterator it;

		for(it = _edges.iterator(); it.hasNext(); ) {
			set.add(((Edge) it.next()).getOpposite(this));
		}

		return set;
	}

	public GraphElement clone(Graph destGraph) {
		UVertex v = new UVertex(id());
		if(destGraph != getParent()) {
			v.setParent(destGraph);
		} else {
			return null;
		}
		Set attr = getDatumSet();
		v.setDisplayId(displayId());
		Iterator i = attr.iterator();
		while(i.hasNext()) {
			Datum d = (Datum) i.next();
			if(!d.name().matches("__.*:.*")) {
				// not a 'reserved' system datum
				v.setDatum(d);
			}
		}
		return v;
	}

	/**
	 * This method adds an edge to the edge registry.  It should not be used
	 * by client programs.
	 */
	public void addEdge(Edge e) {
		// make sure arguments are okay
		if(e instanceof UEdge && e.getEnds().contains(this)) {
			_edges.add(e);
			return;
		}

		throw new IllegalArgumentException("Could not add edge to this vertex");
	}

	/**
	 * This method removes an edge from the edge registry.  It should not be used
	 * by client programs.
	 */
	public boolean removeEdge(Edge e) {
		return _edges.remove(e);
	}
}
