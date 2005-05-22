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

import edu.berkeley.guir.prefuse.graph.DefaultNode;

import nv2d.graph.Datum;
import nv2d.graph.Vertex;
import nv2d.graph.GraphElement;

public class PNode extends DefaultNode implements PElement {
	private Vertex _v;
	private boolean _isStartPoint, _isEndPoint, _isPathElement, _isSelected;

	public PNode(Vertex v) {
		_v = v;
		_isStartPoint = false;
		_isEndPoint = false;
		_isPathElement = false;
		_isSelected = false;
		v.setDatum(new Datum(PGraph.DATUM_POBJ, this));
	}

	public Vertex v() {
		return _v;
	}

	public GraphElement getNV2DGraphElement() {
		return _v;
	}

	// helper method to extract the prefuse PNode object link from a Vertex
	public static PNode v2p(Vertex v) {
		return (PNode) v.getDatum(PGraph.DATUM_POBJ).get();
	}

	public boolean isStartPoint() {
		return _isStartPoint;
	}

	public boolean isEndPoint() {
		return _isEndPoint;
	}

	public boolean isPathElement() {
		return _isPathElement;
	}

	public void setStartPoint(boolean b) {
		_isStartPoint = b;
	}

	public void setEndPoint(boolean b) {
		_isEndPoint = b;
	}

	public void setPathElement(boolean b) {
		_isPathElement = b;
	}

	public void setSelected(boolean b) {
		_isSelected = b;
	}

	public boolean isSelected() {
		return _isSelected;
	}
}
