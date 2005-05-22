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

public interface GraphElement extends Comparable, DataStore {
	public void setId(String id);

	public String id();

	public void setDisplayId(String displayId);
	
	public String displayId();

	/** Creates a duplicate GraphElement with the same <code>id</code> and the
	 * same non-system datums (those whose datum names do not follow the format
	 * of the string '__[owner]:[name]'.
	 * @param destGraph Each graph element must have a parent graph to go into.
	 */
	public GraphElement clone(Graph destGraph);

	public DataStore getParent();

	public void setParent(DataStore p);

	/** The default behavior for comparison is the <code>id()</code> method. */
	public boolean equals(Object o);
	
	public int hashCode();

	public int compareTo(Object o);
}
