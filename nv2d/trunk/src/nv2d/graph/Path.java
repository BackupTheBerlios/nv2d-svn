package nv2d.graph;

import java.util.List;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.StringBuffer;

public class Path {
	protected Graph _gref;
	protected List _p;

	public Path(Graph g) {
		_gref = g;
		_p = new Vector();
	}

	public Path(Graph g, List l) {
		_gref = g;
		_p = l;
	}

	public void addVertex(Vertex v) {
		_p.add(v);
	}

	public String toString() {
		ListIterator i = _p.listIterator();
		StringBuffer buf = new StringBuffer();

		while(i.hasNext()) {
			Vertex v = (Vertex) i.next();
			buf.append("[" + v.id() + "]");
			if(i.hasNext()) {
				buf.append(" -> ");
			}
		}

		return buf.toString();
	}
}
