package nv2d.render;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.DefaultNode;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.Edge;

public class PGraph extends DefaultGraph {
	public static final String DATUM_POBJ = "__prefuse:linkobj";

	private Graph _g;

	public PGraph(Graph g) {
		_g = g;

		Iterator vi = g.getVertices().iterator();
		while(vi.hasNext()) {
			addNode(new PNode((Vertex) vi.next()));
		}
		Iterator ei = g.getEdges().iterator();
		while(ei.hasNext()) {
			Edge e = (Edge) ei.next();
			DefaultNode car = PNode.v2p((Vertex) e.getEnds().car());
			DefaultNode cdr = PNode.v2p((Vertex) e.getEnds().cdr());
			addEdge(new PEdge(e, car, cdr));
		}
	}
}
