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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public abstract class Graph extends DataStore {
	/* Accessors */
	/** Get a set of the edges in the graph. */
	public abstract Set getEdges();

	/** Get a set of the vertices in the graph. */
	public abstract Set getVertices();
	
	/** Returns the number of unique vertices in the graph. */
	public abstract int numVertices();

	/** Returns the number of unique edges in the graph. */
	public abstract int numEdges();
	
	public abstract boolean isDirected();

	/** Returns a subset of the graph containing only the vertices and edges
	 * in the parameter Set.  Edges whose vertices are not also in the
	 * parameter Set will have them added.  */
	public Graph subset(Set graphelements) {
		// filter for those edges which contain only the vertices we want
		Graph g = newInstance();
		Hashtable edges = new Hashtable(); // originals
		Iterator i = graphelements.iterator();

		// collect all the graph elements and catalog them
		while(i.hasNext()) {
			Object o = i.next();
			String id = ((GraphElement) o).id();
			if(getVertices().contains(o) || getEdges().contains(o)) {
				if(o instanceof Vertex && null == g.findVertex(id)) {
					g.add(((Vertex) o).clone(g));
				} else if(o instanceof Edge) {
					// add the endpoints of the edges if necessary
					Vertex tsource = (Vertex) ((Edge) o).getEnds().car();
					Vertex tdest = (Vertex) ((Edge) o).getEnds().cdr();
					if(null == g.findVertex(tsource.id())) {
						g.add(tsource.clone(g));
					}
					if(null == g.findVertex(tdest.id())) {
						g.add(tdest.clone(g));
					}
					// clone the edge
					edges.put(id, ((Edge) o).clone(g));
				}
				// should never reach this point
				assert(false);
			} else {
				// was not part of the edge
				assert(false);
			}
		}

		// collect any additional edges for the vertices which were not included
		i = getEdges().iterator();
		while(i.hasNext()) {
			Edge e = (Edge) i.next();
			if(
				!edges.containsKey(e.id()) &&
				null != g.findVertex(((Vertex) e.getEnds().car()).id()) &&
				null != g.findVertex(((Vertex) e.getEnds().cdr()).id())
			) {
				edges.put(e.id(), e.clone(g));
			}
		}

		// insert cloned edges into graph
		i = edges.values().iterator();
		while(i.hasNext()) {
			g.add((Edge) i.next());
		}

		return g;
	}

	/** Find the edge length between two vertices.  If they are not
	 * adjacent, return 0. */
	public abstract double edgeLen(Vertex source, Vertex dest);

	/** Create a new graph of this type (mainly for filters). */
	public abstract Graph newInstance();

	/** Find a vertex by it's <code>id()</code>.  Returns a null pointer if
	 * the object does not exist.
	 * @param id
	 * @return {@link nv2d.graph.Vertex} with the same id.  <code>null</code> if
	 * 	the vertex does not exist.
	 */
	public abstract Vertex findVertex(String id);

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
	public abstract Path shortestPath(Vertex source, Vertex dest);

	/** Add a <code>GraphElement</code> to the graph.  Note that unless there
	 * is a class which knows how to render the graph element, it will not be
	 * shown. */
	public abstract boolean add(GraphElement ge);

	/** Remove a <code>GraphElement</code> from the graph. */
	public abstract boolean remove(GraphElement ge);

	/** Clear all the data contained in this graph.  This includes objects of
	 * the type <code>Datum</code> and <code>GraphElement</code>. */
	public abstract void clear();
}
