package nv2d.render;

import edu.berkeley.guir.prefuse.graph.DefaultNode;

import nv2d.graph.Datum;
import nv2d.graph.Vertex;

public class PNode extends DefaultNode {
	private Vertex _v;

	public PNode(Vertex v) {
		_v = v;
		v.setDatum(new Datum(PGraph.DATUM_POBJ, this));
	}

	public Vertex v() {
		return _v;
	}

	// helper method to extract the prefuse PNode object link from a Vertex
	public static PNode v2p(Vertex v) {
		return (PNode) v.getDatum(PGraph.DATUM_POBJ).get();
	}
}
