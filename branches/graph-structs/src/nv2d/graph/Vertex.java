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

package nv2d.graph;

import java.util.Set;

public interface Vertex extends GraphElement {
	/** This should return all edges for undirected graphs */
	public Set inEdges();

	/** This should return all edges for undirected graphs.  For undirected
	 * graphs, this method should return the same object as
	 * <code>inEdges()</code>. */
	public Set outEdges();

	/** Return all nodes which share an edge with this node. */
	public Set neighbors();

	public String toString();
}
