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

public abstract class GraphElement extends DataStore implements Comparable {
	private String _id = null;
	private String _displayId = null;
	private DataStore _parent = null;

	public GraphElement(String id) {
		_id = id;
		_parent = null;
	}

	final public void setId(String id) {
		_id = new String(id);
	}

	final public String id() {
		return _id;
	}
	
	public void setDisplayId(String displayId) {
		_displayId = displayId;
	}
	
	public String displayId() {
		if(_displayId == null) {
			return id();
		}
		return _displayId;
	}

	/** Creates a duplicate GraphElement with the same <code>id</code> and
	 * the same non-system datums (those whose datum names do not follow the
	 * format of the string '__[owner]:[name]'.
	 * @param destGraph Each graph element must have a parent graph to go
	 *    into. */
	public abstract GraphElement clone(Graph destGraph);

	final public DataStore getParent() {
		return _parent;
	}

	final public void setParent(DataStore p) {
		_parent = p;
	}

	/** The default behavior for comparison is the <code>id()</code> method. */
	public boolean equals(Object o) {
		if(null == o) {
			return false;
		}
		
		try {
			if(((GraphElement) o).id().equals(id())) {
				return true;
			}
		} finally {
			// do nothing; false
		}

		return false;
	}
	
	public int hashCode() {
		return id().hashCode();
	}

	final public int compareTo(Object o) {
		GraphElement ge = (GraphElement) o;
		return id().compareTo(ge.id());
	}
}
