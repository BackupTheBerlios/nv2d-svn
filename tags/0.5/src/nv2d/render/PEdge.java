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

package nv2d.render;

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultNode;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
// import nv2d.graph.Vertex;
import nv2d.graph.GraphElement;
import nv2d.graph.directed.DEdge;

public class PEdge extends DefaultEdge implements PElement {
	private Edge _e;
	private boolean _isPathElement, _isSelected; // is this edge part of a path?

	public PEdge(Edge e, DefaultNode car, DefaultNode cdr) {
		// Directed edges must be created through constructor.
		super(car, cdr, (e instanceof DEdge));
		_e = e;
		e.setDatum(new Datum(PGraph.DATUM_POBJ, this));

		_isPathElement = false;
		_isSelected = false;
	}
	
	public GraphElement getNV2DGraphElement() {
		return _e;
	}

	public Edge e() {
		return _e;
	}

	public void setPathElement(boolean b) {
		_isPathElement = b;
	}

	public boolean isPathElement() {
		return _isPathElement;
	}

	public void setSelected(boolean b) {
		_isSelected = b;
	}

	public boolean isSelected() {
		return _isSelected;
	}
}
