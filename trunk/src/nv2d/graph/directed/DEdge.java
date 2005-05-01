/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * $Id$
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
import java.lang.IllegalArgumentException;
import java.util.Set;
import java.util.Iterator;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Vertex;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.utils.Pair;

public class DEdge extends Edge implements Serializable {
	transient private Pair _v;
	private double _len;

	public DEdge(DVertex source, DVertex dest, double length) {
		super("DirectedEdge ["
			+ source.id() + "]-->["
			+ dest.id() + "]");

		_v = new Pair(source, dest);
		_len = length;
	}

	public GraphElement clone(Graph destGraph) {
		if(destGraph == null) {
			return null;
		}
		
		if(destGraph.equals(getParent())) {
			System.err.println("You cannot clone an edge into the same graph.");
			return null;
		}

		DEdge e = new DEdge((DVertex) destGraph.findVertex(getSource().id()),
				(DVertex) destGraph.findVertex(getDest().id()), _len);

		Set attr = getVisibleDatumSet();
		Iterator i = attr.iterator();
		while(i.hasNext()) {
			e.setDatum((Datum) i.next());
		}
		return e;
	}

	public Vertex getSource() {
		return (Vertex) _v.car();
	}

	public Vertex getDest() {
		return (Vertex) _v.cdr();
	}

	public double length() {
		return _len;
	}

	public void setLength(double l) {
		_len = l;
	}

	public Pair getEnds() {
		return _v;
	}

	public Vertex getOpposite(Vertex v) {
		if(v.equals(_v.car())) {
			return (Vertex) _v.cdr();
		} else if(v.equals(_v.cdr())) {
			return (Vertex) _v.car();
		}
		// v is not part of the edge
		// throw an exception
		throw (new IllegalArgumentException("DirectedEdge.getOpposite() -- argument must be a vertex which is incident to the edge"));
	}
}
