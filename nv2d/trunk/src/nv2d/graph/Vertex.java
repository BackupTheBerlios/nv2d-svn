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

import java.util.Set;

public abstract class Vertex extends GraphElement {
	public Vertex(String id) {
		super(id);
	}

	/** This should return all edges for undirected graphs */
	public abstract Set inEdges();

	/** This should return all edges for undirected graphs.  For undirected
	 * graphs, this method should return the same object as
	 * <code>inEdges()</code>. */
	public abstract Set outEdges();

	/** Return all nodes which share an edge with this node. */
	public abstract Set neighbors();

	public String toString() {
		return id();
	}

	/** A valid edge parameter must at least contain this vertex.  This is
	 * protected, only <code>Graph</code> should be able to access this. */
	//abstract void addInEdge(Edge e);

	/** A valid edge parameter must at least contain this vertex.  This is
	 * protected, only <code>Graph</code> should be able to access this. */
	//abstract void addOutEdge(Edge e);
}
