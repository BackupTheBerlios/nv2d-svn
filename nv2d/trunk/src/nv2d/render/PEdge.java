package nv2d.render;

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultNode;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Vertex;
import nv2d.graph.directed.DEdge;

public class PEdge extends DefaultEdge {
	private Edge _e;
	private boolean _isPathElement; // is this edge part of a path?

	public PEdge(Edge e, DefaultNode car, DefaultNode cdr) {
		// Directed edges must be created through constructor.
		super(car, cdr, (e instanceof DEdge));
		_e = e;
		e.setDatum(new Datum(PGraph.DATUM_POBJ, this));
	}
	
	public Edge e() {
		return _e;
	}

	public void setPathElement(boolean b) {
		_isPathElement = b;
	}

	public boolean isPathElement() {
		return _isPathElement;
	}
}
