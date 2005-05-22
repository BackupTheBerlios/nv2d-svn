/*
 * DijkstraTest.java
 * JUnit based test
 *
 * Created on April 21, 2005, 9:31 PM
 */

package nv2d.algorithms.shortestpaths;

import junit.framework.*;
import java.util.Iterator;
import nv2d.algorithms.AlgConst;
import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.directed.DGraph;
import nv2d.graph.directed.DVertex;
import nv2d.graph.directed.DEdge;


/**
 *
 * @author bshi
 */
public class DijkstraTest extends TestCase {
	static public final double TOLERANCE = 5e-5;
	DGraph _g1;
	
	public DijkstraTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws java.lang.Exception {
		_g1 = makeDGraphOne();
	}
	
	protected void tearDown() throws java.lang.Exception {
	}
	
	public static junit.framework.Test suite() {
		junit.framework.TestSuite suite = new junit.framework.TestSuite(DijkstraTest.class);
		
		return suite;
	}
	
	/**
	 * Test of getPath method, of class nv2d.algorithms.shortestpaths.Dijkstra.
	 */
	public void testGetPath() {
		System.out.println("testGetPath");
		
		// TODO add your test code below by replacing the default call to fail.
		System.out.println("   The test case is empty.");
	}
	
	/**
	 * Test of init method, of class nv2d.algorithms.shortestpaths.Dijkstra.
	 */
	public void testInit() {
		System.out.println("testInit");
		
		// TODO add your test code below by replacing the default call to fail.
		System.out.println("   The test case is empty.");
	}
	
	/**
	 * Test of run method, of class nv2d.algorithms.shortestpaths.Dijkstra.
	 */
	public void testRun() {
		System.out.println("testRun");
		
		// TODO add your test code below by replacing the default call to fail.
		System.out.println("   The test case is empty.");
	}
	
	// TODO add test methods here. The name must begin with 'test'. For example:
	public void testDistances() {
		DGraph g = _g1;
		Dijkstra algorithm = new Dijkstra(g);
		
		DVertex source = null;
		try {
			source = (DVertex) findNode(g, "a");
		} catch (Exception e) {
			// System.out.println("Could not find node [a]");
			// inform JUnit there was an error
			assertTrue(false);
		}
		
		// System.out.println("Using Vertex [" + source.id() + "] as the source");
		
		algorithm.init(g, source);
		algorithm.run();
		
		// check values (source is 'a')
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
		
		Double va = (Double) findNode(g, "a").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double vb = (Double) findNode(g, "b").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double vc = (Double) findNode(g, "c").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double vd = (Double) findNode(g, "d").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double ve = (Double) findNode(g, "e").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double vf = (Double) findNode(g, "f").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double vg = (Double) findNode(g, "g").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double vh = (Double) findNode(g, "h").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double vi = (Double) findNode(g, "i").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		Double vj = (Double) findNode(g, "j").getDatum(AlgConst.DATUM_DIJKSTRA_D).get();
		
		assertTrue(withinBounds((double) va.doubleValue(), 0.0));
		assertTrue(withinBounds((double) vb.doubleValue(), 4.0));
		assertTrue(withinBounds((double) vc.doubleValue(), 22.0));
		assertTrue(withinBounds((double) vd.doubleValue(), 63.0));
		assertTrue(withinBounds((double) ve.doubleValue(), 16.0));
		assertTrue(withinBounds((double) vf.doubleValue(), 42.0));
		assertTrue(withinBounds((double) vg.doubleValue(), 51.0));
		assertTrue(withinBounds((double) vh.doubleValue(), 49.0));
		assertTrue(withinBounds((double) vi.doubleValue(), 53.0));
		assertTrue(withinBounds((double) vj.doubleValue(), 34.0));
		
		
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

    public static DGraph makeDGraphOne() {
		DGraph graph = new DGraph();
		DVertex a = new DVertex("a");
		DVertex b = new DVertex("b");
		DVertex c = new DVertex("c");
		DVertex d = new DVertex("d");
		DVertex e = new DVertex("e");
		DVertex f = new DVertex("f");
		DVertex g = new DVertex("g");
		DVertex h = new DVertex("h");
		DVertex i = new DVertex("i");
		DVertex j = new DVertex("j");

		DEdge e0 = new DEdge(a, b, 4.0f);
		DEdge e1 = new DEdge(b, a, 74.0f);
		DEdge e2 = new DEdge(b, c, 18.0f);
		DEdge e3 = new DEdge(c, b, 12.0f);
		DEdge e4 = new DEdge(a, d, 85.0f);
		DEdge e5 = new DEdge(b, e, 12.0f);
		DEdge e6 = new DEdge(c, f, 74.0f);
		DEdge e7 = new DEdge(c, j, 12.0f);
		DEdge e8 = new DEdge(d, e, 32.0f);
		DEdge e9 = new DEdge(e, d, 66.0f);
		DEdge e10 = new DEdge(e, f, 76.0f);
		DEdge e11 = new DEdge(f, j, 21.0f);
		DEdge e12 = new DEdge(j, f, 8.0f);
		DEdge e13 = new DEdge(g, d, 12.0f);
		DEdge e14 = new DEdge(d, g, 38.0f);
		DEdge e15 = new DEdge(e, h, 33.0f);
		DEdge e16 = new DEdge(i, f, 31.0f);
		DEdge e17 = new DEdge(f, i, 11.0f);
		DEdge e18 = new DEdge(i, j, 78.0f);
		DEdge e19 = new DEdge(g, h, 10.0f);
		DEdge e20 = new DEdge(h, g, 2.0f);
		DEdge e21 = new DEdge(h, i, 72.0f);
		DEdge e22 = new DEdge(i, h, 18.0f);

		graph.add(a);
		graph.add(b);
		graph.add(c);
		graph.add(d);
		graph.add(e);
		graph.add(f);
		graph.add(g);
		graph.add(h);
		graph.add(i);
		graph.add(j);

		graph.add(e0);
		graph.add(e1);
		graph.add(e2);
		graph.add(e3);
		graph.add(e4);
		graph.add(e5);
		graph.add(e6);
		graph.add(e7);
		graph.add(e8);
		graph.add(e9);
		graph.add(e10);
		graph.add(e11);
		graph.add(e12);
		graph.add(e13);
		graph.add(e14);
		graph.add(e15);
		graph.add(e16);
		graph.add(e17);
		graph.add(e18);
		graph.add(e19);
		graph.add(e20);
		graph.add(e21);
		graph.add(e22);

		return graph;
	}

}
