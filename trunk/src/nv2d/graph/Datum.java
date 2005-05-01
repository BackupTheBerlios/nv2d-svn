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

import java.lang.ClassCastException;
import java.lang.Comparable;

/** Stores one piece of information with an identifier.
 *
 * <p><b>A note about Datum naming convention:</b><br>
 * Datum's with names beginning with two underscores (i.e. '__sna_cache') will
 * not be shown.  These will be considered internal variables.  All others will
 * be displayed (if possible) in the visualization.
 * */
public class Datum implements Comparable {
	protected String _name;
	protected Object _value;

	/** Constructor.  The value will be initialized to null. */
	public Datum(String name) {
		if(name == null) {
			throw (new IllegalArgumentException("Illegal argument in Datum constructor: null name"));
		}
		_name = name;
		_value = null;
	}

	/** Initialize with a specific value.  */
	public Datum(String name, Object value) {
		if(name == null) {
			throw (new IllegalArgumentException("Illegal argument in Datum constructor: null name"));
		}
		_name = name;
		_value = value;
	}

	/** Get the value for this datum.
	 * @return null if a value has not been set.
	 */
	public Object get() {
		return _value;
	}

	/** Get the identifier for this datum.
	 */
	public String name() {
		return _name;
	}

	/** Set the value for this datum.
	 * @param value null values are accepted.
	 */
	public void set(Object value) {
		_value = value;
	}

	/** Equality is determined only by name. */
	public boolean equals(Object o) {
		if(o instanceof Datum) {
			Datum d = (Datum) o;
			if(d.name().equals(this.name())) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return _name.hashCode();
	}

	/** Compares this Datum with another <b>based on name</b>.  That is to say
	 * that the value of two Datum objects are not considered in the equality
	 * test.  See <code>Comparable</code> interface for details. */
	public int compareTo(Object o) {
		if(o.getClass() != Datum.class) {
			throw (new ClassCastException("Tried to compare Datum with " + o.getClass()));
		}

		return name().compareTo(((Datum) o).name());
	}
}
