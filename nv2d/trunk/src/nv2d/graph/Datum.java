package nv2d.graph;

import java.lang.ClassCastException;
import java.lang.Comparable;

/* Stores one piece of information with an identifier */
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

	public Object get() {
		return _value;
	}

	public String name() {
		return _name;
	}

	public void set(Object value) {
		_value = value;
	}

	/** Equality is determined only by name. */
	public boolean equals(Object o) {
		if(compareTo(o) == 0) {
			return true;
		}
		return false;
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
