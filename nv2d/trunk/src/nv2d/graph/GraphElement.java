package nv2d.graph;

public abstract class GraphElement extends DataStore implements Comparable {
	private String _id = null;
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
	final public boolean equals(Object o) {
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

	final public int compareTo(Object o) {
		GraphElement ge = (GraphElement) o;
		return id().compareTo(ge.id());
	}
}
