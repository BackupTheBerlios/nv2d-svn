package nv2d.graph;

import nv2d.exceptions.QueryNotFound;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


/** This is the fundamental data structure contained in the <code>graph</code>
 * package. This class behaves like the Set class in the JFC -- which is to say
 * that all the elements are unique according to their <code>equals()</code>
 * method. */

public class DataStore {
	protected Hashtable _store;

	public DataStore() {
		_store = new Hashtable();
	}

	/** Get a Datum from storage. */
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
	public Set getDatumSet() {
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

	/** Equality test; two Datum are considered 'equal' if their names are the
	 * same. This has important implications for <code>setDatum()</code> and
	 * <code>getDatum()</code> */
	public boolean equals(Object o) {
		if(o.getClass() != DataStore.class) {
			return false;
		}
		return _store.equals(((DataStore) o)._store);
	}
}

//package nv2d.graph;
//
//import cern.colt.list.ObjectArrayList;
//
//import nv2d.exceptions.QueryNotFound;
//
///** This is the fundamental data structure contained in the <code>graph</code>
// * package. This class behaves like the Set class in the JFC -- which is to say
// * that all the elements are unique according to their <code>equals()</code>
// * method. */
//public class DataStore {
//	protected ObjectArrayList _store;
//
//	public DataStore() {
//		_store = new ObjectArrayList();
//	}
//
//	/** Get a Datum from storage. */
//	public Datum getDatum(String name) {
//		int i = _store.binarySearch(new Datum(name, null));
//
//		if(i >= 0) {
//			// found
//			return (Datum) _store.get(i);
//		}
//
//		// run-time exception
//		throw (new QueryNotFound(name, "DataStore"));
//	}
//
//	/** Use to set a new Datum or to replace an existing Datum with a new
//	 * value.  If the name of the datum is identical to the name of a datum
//	 * already set in this store, the new one will replace the old one. See the
//	 * equals() method for details. */
//	public void setDatum(Datum d) {
//		int i = _store.binarySearch(new Datum(d.name(), null));
//
//		if(i >= 0) {
//			// found -- now replace
//			_store.set(i, d);
//			return;
//		} else {
//			// not found
//			// i = (-[insertion point] - 1)
//			_store.beforeInsert(-(i + 1), d);
//		}
//	}
//
//	/** Equality test; two Datum are considered 'equal' if their names are the
//	 * same. This has important implications for <code>setDatum()</code> and
//	 * <code>getDatum()</code> */
//	public boolean equals(Object o) {
//		if(o.getClass() != DataStore.class) {
//			return false;
//		}
//		return _store.equals(((DataStore) o)._store);
//	}
//}
