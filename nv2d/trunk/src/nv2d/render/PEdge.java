package nv2d.render;

import java.lang.ClassCastException;

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultNode;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Vertex;

public class PEdge extends DefaultEdge {
	private Edge _e;

	public PEdge(Edge e, DefaultNode car, DefaultNode cdr) {
		super(car, cdr);
		_e = e;
		e.setDatum(new Datum(PGraph.DATUM_POBJ, this));
	}
	
	public Edge e() {
		return _e;
	}
}
