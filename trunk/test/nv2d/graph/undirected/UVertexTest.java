/*
 * UVertexTest.java
 * JUnit based test
 *
 * Created on April 22, 2005, 5:59 PM
 */

package nv2d.graph.undirected;

import junit.framework.*;
import java.io.Serializable;
import java.lang.IllegalArgumentException;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import nv2d.graph.Vertex;
import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;

/**
 *
 * @author bshi
 */
public class UVertexTest extends TestCase {
	private UVertex _a, _b, _c, _d, _e;
	private UEdge _e1, _e2, _e3, _e4, _e5;
	
	public UVertexTest(String testName) {
		super(testName);
	}

	protected void setUp() throws java.lang.Exception {
		_a = new UVertex("a");
		_b = new UVertex("b");
		_c = new UVertex("c");
		_d = new UVertex("d");
		_e = new UVertex("e");
		_e1 = new UEdge(_a, _b, 1.0);
		_e2 = new UEdge(_a, _c, 2.0);
		_e3 = new UEdge(_a, _d, 3.0);
		_e4 = new UEdge(_a, _e, 4.0);
		_e5 = new UEdge(_b, _d, 1.0);
	}

	protected void tearDown() throws java.lang.Exception {
		_a = null;
		_b = null;
		_c = null;
		_d = null;
		_e = null;
		_e1 = null;
		_e2 = null;
		_e3 = null;
		_e4 = null;
	}

	public static junit.framework.Test suite() {
		junit.framework.TestSuite suite = new junit.framework.TestSuite(UVertexTest.class);
		
		return suite;
	}

	/**
	 * Test of inEdges method, of class nv2d.graph.undirected.UVertex.
	 */
	public void testInEdges() {
		System.out.println("testInEdges");
		_a.addEdge(_e1);
		try {
			_a.addEdge(new nv2d.graph.directed.DEdge(new nv2d.graph.directed.DVertex("da"),
					new nv2d.graph.directed.DVertex("db"), 1.0));
			assertTrue(false);
		} catch (IllegalArgumentException exp) {
			// this is correct
		}
		
		Set set = _a.inEdges();
		assertTrue(set.size() == 1);
		assertTrue(set.contains(_e1));
		assertTrue(!set.contains(_e2));

		set = _a.inEdges();
		_a.addEdge(_e2);
		assertTrue(set.size() == 2);
		assertTrue(set.contains(_e1));
		assertTrue(set.contains(_e2));
	}

	/**
	 * Test of outEdges method, of class nv2d.graph.undirected.UVertex.
	 */
	public void testOutEdges() {
		System.out.println("testOutEdges");
		_a.addEdge(_e3);
		try {
			_a.addEdge(new nv2d.graph.directed.DEdge(new nv2d.graph.directed.DVertex("da"),
					new nv2d.graph.directed.DVertex("db"), 1.0));
			assertTrue(false);
		} catch (IllegalArgumentException ex) {
			// this is correct
		}
		
		Set set = _a.outEdges();
		assertTrue(set.size() == 1);
		assertTrue(set.contains(_e3));
		assertTrue(!set.contains(_e4));

		set = _a.outEdges();
		_a.addEdge(_e4);
		assertTrue(set.size() == 2);
		assertTrue(set.contains(_e3));
		assertTrue(set.contains(_e4));
	}

	/**
	 * Test of neighbors method, of class nv2d.graph.undirected.UVertex.
	 */
	public void testNeighbors() {
		System.out.println("testNeighbors");
		
		_a.addEdge(_e1);
		_a.addEdge(_e2);
		_a.addEdge(_e3);
		Set set = _a.neighbors();
		assertTrue(set.size() == 3);
		assertTrue(set.contains(_b));
		assertTrue(set.contains(_c));
		assertTrue(set.contains(_d));
		_a.removeEdge(_e1);
		set = _a.neighbors();
		assertTrue(set.size() == 2);
		assertTrue(set.contains(_c));
		assertTrue(set.contains(_d));
		_a.removeEdge(_e1);
		set = _a.neighbors();
		assertTrue(set.size() == 2);
		assertTrue(set.contains(_c));
		assertTrue(set.contains(_d));
	}

	/**
	 * Test of clone method, of class nv2d.graph.undirected.UVertex.
	 */
	public void testClone() {
		System.out.println("testClone");
		
		UGraph g = new UGraph();
		
		Integer data1 = new Integer(2345);
		String data2 = "should not be cloned";
		String data3 = "a";
		
		_a.setDatum(new Datum("number", data1));
		_a.setDatum(new Datum("__private:datum", data2));
		_a.setDatum(new Datum("character", data3));

		UVertex _aClone = (UVertex) _a.clone(g);
		
		assertTrue(_aClone.getParent() == g);
		
		assertTrue(_aClone != _a);
		assertTrue(_aClone.id().equals(_a.id()));
		assertTrue(_aClone.inEdges().size() == 0);
		assertTrue(_aClone.outEdges().size() == 0);
		assertTrue(_aClone.neighbors().size() == 0);
		
		assertTrue(_aClone.getDatum("number").get() == data1);
		assertTrue(_aClone.getDatum("__private:datum") == null);
		assertTrue(_aClone.getDatum("character").get() == data3);
		
	}

	/**
	 * Test of addEdge and removeEdge method, of class nv2d.graph.undirected.UVertex.
	 */
	public void testAddRemoveEdge() {
		System.out.println("testAddRemoveEdge");
		try {
			_a.addEdge(_e5);
			assertTrue(false); // should never reach here
		} catch (IllegalArgumentException ex) {
			// this is correct
		}
		
		// catching the exception when a non-uedge is added is tested
		// by the testInEdges and testOutEdges methods.
		
		_a.addEdge(_e1);
		_a.addEdge(_e2);
		_a.addEdge(_e3);
		Set set = _a.inEdges();
		assertTrue(set.size() == 3);
		_a.removeEdge(_e1);
		set = _a.inEdges();
		assertTrue(set.size() == 2);
		assertTrue(!set.contains(_e1));
		assertTrue(set.contains(_e2));
		assertTrue(set.contains(_e3));
		_a.removeEdge(_e1);
		set = _a.inEdges();
		assertTrue(set.size() == 2);
	}
}
