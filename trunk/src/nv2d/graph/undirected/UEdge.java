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

import java.util.Iterator;
import java.util.Set;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.graph.Vertex;
import nv2d.utils.Pair;

public class UEdge extends Edge {
	private double _len;
	private Pair _v;
	
	String _id1, _id2;
	
	public UEdge(UVertex source, UVertex dest, double length) {
		super("UndirectedEdge ["
			+ source.id() + "]<->["
			+ dest.id() + "]");
		
		// _id1 and _id2 are used to determine the hash code of this object
		_id1 = "UndirectedEdge [" + source.id() + "]<->[" + dest.id() + "]";
		_id2 = "UndirectedEdge [" + dest.id() + "]<->[" + source.id() + "]";

		if(source == null || dest == null || source.equals(dest)) {
			throw new java.lang.IllegalArgumentException("Could not create edge");
		}

		_v = new Pair(source, dest);
		_len = length;
	}
	
	public boolean equals(Object o) {
		if(o instanceof UEdge) {
			UEdge e = (UEdge) o;
			if(e.getEnds().contains(_v.car()) && e.getEnds().contains(_v.cdr())) {
				return true;
			}
		}
		return false;
	}
	
	/** This method is overriden to preserve correct behavior when using the HashSet
	 * class. */
	public int hashCode() {
		return java.lang.Math.min(_id1.hashCode(), _id2.hashCode());
	}
	
	public double length() {
		return _len;
	}

	public Pair getEnds() {
		return _v;
	}
	
	public Vertex getCar() {
		return (Vertex) _v.car();
	}
	
	public Vertex getCdr() {
		return (Vertex) _v.cdr();
	}

	public Vertex getOpposite(Vertex v) {
		if(v.equals(_v.car())) {
			return (Vertex) _v.cdr();
		} else if(v.equals(_v.cdr())) {
			return (Vertex) _v.car();
		}
		// v is not part of the edge
		// throw an exception
		throw (new java.lang.IllegalArgumentException("UndirectedEdge.getOpposite() -- argument must be a vertex which is incident to the edge"));
	}

	public void setLength(double l) {
		_len = l;
	}
	
	/** It is important to note that cloning edges is used to create
	 * new subgraphs.  You must clone an edge into a destination graph.
	 */
	public GraphElement clone(Graph destGraph) {
		// TODO: assert that the destGraph contains source and dest
		if(destGraph == null) {
			return null;
		}
		
		if(destGraph.equals(getParent())) {
			System.err.println("You cannot clone an edge into the same graph.");
			return null;
		}

		UEdge e = new UEdge((UVertex) destGraph.findVertex(getCar().id()),
				(UVertex) destGraph.findVertex(getCdr().id()), _len);

		Set attr = getDatumSet();
		Iterator i = attr.iterator();
		while(i.hasNext()) {
			Datum d = (Datum) i.next();
			if(!d.name().matches("__.*:.*")) {
				// not a 'reserved' system datum
				e.setDatum(d);
			}
		}
		return e;
	}
}