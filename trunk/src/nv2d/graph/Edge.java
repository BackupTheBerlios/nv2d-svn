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

package nv2d.graph;

import nv2d.utils.Pair;

public abstract class Edge extends GraphElement {
	public Edge(String id) {
		super(id);
	}

	// Accessors

	/** Get the length of this edge. */
	public abstract double length();

	/** Get the two ends of this edge.  This method returns a Pair containing
	 * two unique Edge objects. */
	public abstract Pair getEnds();

	/** Returns the Vertex opposite of the parameter Vertex on this edge. This
	 * method throws an exception if the parameter Vertex is not incident to
	 * this Edge. */
	public abstract Vertex getOpposite(Vertex v);

	// Modifiers

	/** Change the length of this edge.  The interface does not define a
	 * starting edge length. */
	public abstract void setLength(double l);
}
