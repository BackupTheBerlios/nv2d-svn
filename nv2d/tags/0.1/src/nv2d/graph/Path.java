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

	/* TODO: Needs testing */
	public double totalLength() {
		double length = 0.0;
		ListIterator i = _p.listIterator();
		if(i.hasNext()) {
			Vertex prev = (Vertex) i.next();
			Vertex curr = null;
			while(i.hasNext()) {
				curr = (Vertex) i.next();
				length += _gref.edgeLen(prev, curr);
				prev = curr;
			}
		}
		// System.out.println("   (path length = " + length + ")");
		return length;
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
