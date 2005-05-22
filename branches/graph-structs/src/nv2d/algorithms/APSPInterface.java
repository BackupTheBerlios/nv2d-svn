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

package nv2d.algorithms;

import nv2d.exceptions.NoPathExists;
import nv2d.graph.Graph;
import nv2d.graph.Path;
import nv2d.graph.Vertex;

/** This interface is for all-pairs shortest path implementation. */

public interface APSPInterface {
	/** Get the shortest path between two nodes. 
	 * If the 
	 * */
	public Path getPath(Vertex source, Vertex dest) throws NoPathExists;

	/** Prepare a graph for the algorithm. */
	public void init(Graph g, Vertex s);

	/** Execute the algorithm.  The client should keep track of all changes
	 * made to the graph and re-run this as necessary. */
	public void run();
}
