package nv2d.graph;

import cern.colt.list.ObjectArrayList;

import nv2d.exceptions.QueryNotFound;

public class DataStore {
	protected ObjectArrayList _store;

	public DataStore() {
		_store = new ObjectArrayList();
	}

	/** Get a Datum from storage. */
	public Datum getDatum(String name) {
		int i = _store.binarySearch(new Datum(name, null));

		if(i >= 0) {
			// found
			return (Datum) _store.get(i);
		}

		// run-time exception
		throw (new QueryNotFound(name, "DataStore"));
	}

	/** Use to set a new Datum or to replace an existing Datum with a new
	 * value.*/
	public void setDatum(Datum d) {
		int i = _store.binarySearch(new Datum(d.name(), null));

		if(i >= 0) {
			// found -- now replace
			_store.set(i, d);
			return;
		} else {
			// not found
			// i = (-[insertion point] - 1)
			_store.beforeInsert(-(i + 1), d);
		}
	}

	/** Equality test; Two Datum are considered 'equal' if their names are the
	 * same. */
	public boolean equals(Object o) {
		if(o.getClass() != DataStore.class) {
			return false;
		}
		return _store.equals(((DataStore) o)._store);
	}
}
