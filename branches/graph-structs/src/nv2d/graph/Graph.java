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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public interface Graph extends DataStore {
	/* Accessors */
	/** Get a set of the edges in the graph. */
	public Set getEdges();

	/** Get a set of the vertices in the graph. */
	public Set getVertices();
	
	/** Returns the number of unique vertices in the graph. */
	public int numVertices();

	/** Returns the number of unique edges in the graph. */
	public int numEdges();
	
	public boolean isDirected();

	/** Returns a subset of the graph containing only the vertices and edges
	 * in the parameter Set.  Edges whose vertices are not also in the
	 * parameter Set will have them added.  */
	public Graph subset(Set graphelements);

	/** Find the edge length between two vertices.  If they are not
	 * adjacent, return 0. */
	public double edgeLen(Vertex source, Vertex dest)
			throws IllegalArgumentException;

	/** Create a new graph of this type (mainly for filters). */
	public Graph newInstance();

	/** Find a vertex by it's <code>id()</code>.  Returns a null pointer if
	 * the object does not exist.
	 * @param id
	 * @return {@link nv2d.graph.Vertex} with the same id.  <code>null</code> if
	 * 	the vertex does not exist.
	 */
	public Vertex findVertex(String id);

	/* Modifiers */

	/** Find the shortest path length between 2 vertices.  Calculate the
	 * shortest paths using one of the algorithms provided in
	 * {@link nv2d.algorithms.shortestpaths} package.
	 * 
	 * @param source the starting vertex in the path
	 * @param dest the end vertex in the path
	 * @return a {@link nv2d.graph.Path} object containing the shortest path.
	 *  <code>null</code> if no path exists between the two vertices.
	 */
	public Path shortestPath(Vertex source, Vertex dest);

	/** Add a <code>GraphElement</code> to the graph.  Note that unless there
	 * is a class which knows how to render the graph element, it will not be
	 * shown. */
	public boolean add(GraphElement ge);

	/** Remove a <code>GraphElement</code> from the graph. */
	public boolean remove(GraphElement ge);

	/** Clear all the data contained in this graph.  This includes objects of
	 * the type <code>Datum</code> and <code>GraphElement</code>. */
	public void clear();
}
