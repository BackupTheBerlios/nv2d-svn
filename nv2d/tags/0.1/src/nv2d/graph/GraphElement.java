package nv2d.graph;

public class GraphElement extends DataStore implements Comparable {
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

	final public DataStore getParent() {
		return _parent;
	}

	final public void setParent(DataStore p) {
		_parent = p;
	}

	/** The default behavior for comparison is the <code>id()</code> method. */
	public boolean equals(Object o) {
		try {
			if(((GraphElement) o).id().equals(id())) {
				return true;
			}
		} finally {
			// do nothing -- wait to return false
		}

		return false;
	}

	public int compareTo(Object o) {
		GraphElement ge;
		try {
			ge = (GraphElement) o;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Tried to compare a Vertex with anon-vertex object.");
		}
		return id().compareTo(ge.id());
	}
}
