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

package nv2d.graph.filter;

import java.lang.Integer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nv2d.graph.FilterInterface;
import nv2d.graph.Graph;
import nv2d.graph.Edge;
import nv2d.graph.Vertex;

public class DegreeFilter implements FilterInterface {
	private int _deg;
	private boolean _inited;
	private Vertex _center;
	private Graph _g;
	
	private Object [] _lastArgs;
	
	public DegreeFilter() {
		_inited = false;
	}
	
	public Object [] lastArgs() {
		return _lastArgs;
	}

	public void initialize(Graph g, Object [] args) {
		_lastArgs = args;
		_g = g;
		if(args == null || args.length != 2) {
			System.err.println("Error: wrong number of arguments for the degree filter.");
			return;
		}
		if(args[0] instanceof Vertex && args[1] instanceof Integer) {
			initialize(g, (Vertex) args[0], ((Integer) args[1]).intValue());
			return;
		}

		System.err.println("Error: Arguments do no match required class type (Vertex, Integer)");
	}

	public Graph filter() {
		if(!_inited) {
			System.err.println("Error: Degree filter has not been initialized.");
			return null;
		}

		if(_deg == -1) {
			// don't do any filtering
			return _g;
		}

		int i = _deg;
		Set vertices = new HashSet();
		Set tmpSet = null;
		vertices.add(_center);

		tmpSet = getOutNeighbors(_center);
		vertices.addAll(tmpSet);
		i--;

		// grab vertices
		for(int j = i; j > 0; j--) {
			tmpSet = getOutNeighbors(tmpSet);
			vertices.addAll(tmpSet);
		}
		return _g.subset(vertices);
	}

	private Set getOutNeighbors(Set s) {
		// s must be a set of vertices
		Set outSet = new HashSet();
		Iterator i = s.iterator();
		while(i.hasNext()) {
			outSet.addAll(getOutNeighbors((Vertex) i.next()));
		}
		return outSet;
	}

	private Set getOutNeighbors(Vertex v) {
		Set outSet = new HashSet();
		Iterator i = v.outEdges().iterator();
		while(i.hasNext()) {
			outSet.add(((Edge) i.next()).getOpposite(v));
		}
		return outSet;
	}


	private void initialize(Graph g, Vertex center, int i) {
		if(g == null) {
			System.err.println("Error: Graph is null.");
			_inited = false;
			return;
		}
		if(i >= -1 && i <= 6) {
			_deg = i;
		} else {
			System.err.println("Error: attempted to set degree to an invalid value (setting to 1)");
			_deg = 1;
		}
		if(g.getVertices().contains(center)) {
			_center = center;
		} else {
			System.err.println("Error: attempted to set a center Vertex which does not exist in this graph.  Using random center");
			Iterator iter = g.getVertices().iterator();
			if(iter.hasNext()) {
				_center = (Vertex) iter.next();
			} else {
				_inited = false;
				return;
			}
		}
		_inited = true;
	}
}
