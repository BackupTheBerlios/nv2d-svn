package nv2d.testsuit.algorithms;

import java.lang.Float;
import java.util.Iterator;

import junit.framework.TestCase;

import nv2d.algorithms.AlgConst;
import nv2d.algorithms.shortestpaths.Dijkstra;
import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.directed.DEdge;
import nv2d.graph.directed.DGraph;
import nv2d.graph.directed.DVertex;

import nv2d.testsuit.graph.DirectedGraphTest;

/** We need to define some tolerances since we will be comparing shortest path
 * distances. */
public class DijkstraTest extends TestCase {
	static public final double TOLERANCE = 5e-5;
	DGraph _g1;

	public DijkstraTest(String name) {
		super(name);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(DijkstraTest.class);
	}

	protected void setUp() {
		_g1 = DirectedGraphTest.testDGraphOne();
	}

	protected void tearDown() {
		_g1 = null;
	}

	public void testDistances() {
		DGraph g = _g1;
		Dijkstra algorithm = new Dijkstra(g);

		DVertex source = null;
		try {
			source = (DVertex) findNode(g, "a");
		} catch (Exception e) {
			System.out.println("Could not find node [a]");
			// inform JUnit there was an error
			assertTrue(false);
		}

		System.out.println("Using Vertex [" + source.id() + "] as the source");

		algorithm.init(g, source);
		algorithm.run();

		// check values
		// a->0
		// b->4
		// c->22
		// d->63
		// e->16
		// f->42
		// g->51
		// h->49
		// i->53
		// j->34

		Float va = (Float) findNode(g, "a").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float vb = (Float) findNode(g, "b").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float vc = (Float) findNode(g, "c").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float vd = (Float) findNode(g, "d").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float ve = (Float) findNode(g, "e").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float vf = (Float) findNode(g, "f").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float vg = (Float) findNode(g, "g").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float vh = (Float) findNode(g, "h").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float vi = (Float) findNode(g, "i").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Float vj = (Float) findNode(g, "j").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();

		assertTrue(withinBounds((double) va.floatValue(), 0.0));
		assertTrue(withinBounds((double) vb.floatValue(), 4.0));
		assertTrue(withinBounds((double) vc.floatValue(), 22.0));
		assertTrue(withinBounds((double) vd.floatValue(), 63.0));
		assertTrue(withinBounds((double) ve.floatValue(), 16.0));
		assertTrue(withinBounds((double) vf.floatValue(), 42.0));
		assertTrue(withinBounds((double) vg.floatValue(), 51.0));
		assertTrue(withinBounds((double) vh.floatValue(), 49.0));
		assertTrue(withinBounds((double) vi.floatValue(), 53.0));
		assertTrue(withinBounds((double) vj.floatValue(), 34.0));


		/* // tests the path stuff -- should be in another test method
		try {
			System.out.println(algorithm.getPath(findNode(g, "a"), findNode(g, "i")));
		} catch (NoPathExists e) {
			System.out.println(e.toString());
		}
		*/
	}

	/** Private. */
	private Vertex findNode(Graph g, String id) {
		Iterator it = g.getVertices().iterator();
		while(it.hasNext()) {
			Vertex v = (Vertex) it.next();

			if(v.id().equals(id)) {
				return v;
			}
		}
		return null;
	}

	/** Tests if val is equal to metric (the 'correct' number) within the
	 * tolerance levels */
	private boolean withinBounds(double val, double metric) {
		if(val < (metric + TOLERANCE) && val > (metric - TOLERANCE)) {
			return true;
		}
		return false;
	}
}
