/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * $Id: DataStore.java 212 2005-05-01 01:21:40Z bshi $
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

// import nv2d.exceptions.QueryNotFound;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


/** This is the fundamental data structure contained in the <code>graph</code>
 * package. This class behaves like the Set class in the JFC -- which is to say
 * that all the elements are unique according to their <code>equals()</code>
 * method. */

public class DefaultDataStore implements DataStore {
	protected Hashtable _store;

	public DefaultDataStore() {
		_store = new Hashtable();
	}

	/** Get a Datum from storage.
	 * @param name the datum name
	 * @return null if the datum is not defined.
	 */
	public Datum getDatum(String name) {
		return (Datum) _store.get(name);
	}

	/** Use to set a new Datum or to replace an existing Datum with a new
	 * value.  If the name of the datum is identical to the name of a datum
	 * already set in this store, the new one will replace the old one. See the
	 * equals() method for details. */
	public void setDatum(Datum d) {
		_store.put(d.name(), d);
	}
        
        public void remDatum(String a) {
            _store.remove(a);
        }

	/** Return a list of keys available for display in a user interface.
	 * Remember that internal variables are also stored but their keys
	 * begin with two underscores (__).  These should not be displayed.
	 */
	public Set getVisibleDatumSet() {
		Set ds = new HashSet();
		Iterator i = _store.values().iterator();

		// add, filtering out datums which aren't supposed to be seen
		while(i.hasNext()) {
			Datum d = (Datum) i.next();
			if(d.name().length() > 1 && !d.name().substring(0,2).equals("__")) {
				ds.add(d);
			}
		}
		return ds;
	}
}
