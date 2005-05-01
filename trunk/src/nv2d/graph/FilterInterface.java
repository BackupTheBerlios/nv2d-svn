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

public interface FilterInterface {
	/**
	 * Provide the filter with a graph object and any arguments required.
	 */
	public void initialize(Graph g, Object [] args);
	
	/**
	 * Returns the last arguments given to this filter object.  This functionality
	 * should be provided to maintain consistency for the user interface; for
	 * example, if a {@link nv2d.graph.filter.DegreeFilter} was last set to
	 * 2 degrees, then changing the center vertex without touching the degree
	 * setting should then use 2 degrees again.
	 */
	public Object [] lastArgs();
	
	/**
	 * Run the filter and return the subgraph.
	 */
	public Graph filter();
}
