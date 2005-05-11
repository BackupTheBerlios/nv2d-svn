/*
 * UGraphTest.java
 * JUnit based test
 *
 * Created on April 24, 2005, 5:35 PM
 */

package nv2d.graph.undirected;

import junit.framework.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.graph.Vertex;
import nv2d.graph.Path;
import nv2d.graph.directed.DVertex;
import nv2d.utils.Pair;

/**
 *
 * @author bshi
 */
public class UGraphTest extends TestCase {
	UGraph _g;
	UVertex _n01, _n02, _n03, _n04, _n05, _n06, _n07, _n08, _n09, _n10;
	UEdge _e01, _e02, _e03, _e04, _e05, _e06, _e07, _e08, _e09;
	
	public UGraphTest(String testName) {
		super(testName);
	}

	protected void setUp() throws java.lang.Exception {
		_g = new UGraph();

		_n01 = new UVertex("n01");
		_n02 = new UVertex("n02");
		_n03 = new UVertex("n03");
		_n04 = new UVertex("n04");
		_n05 = new UVertex("n05");
		_n06 = new UVertex("n06");
		_n07 = new UVertex("n07");
		_n08 = new UVertex("n08");
		_n09 = new UVertex("n09");
		_n10 = new UVertex("n10");
		
		_e01 = new UEdge(_n01, _n02, 1.0);
		_e02 = new UEdge(_n01, _n03, 2.0);
		_e03 = new UEdge(_n01, _n04, 3.0);
		_e04 = new UEdge(_n02, _n07, 4.0);
		_e05 = new UEdge(_n02, _n08, 5.0);
		_e06 = new UEdge(_n03, _n09, 6.0);
		_e07 = new UEdge(_n03, _n10, 7.0);
		_e08 = new UEdge(_n04, _n05, 8.0);
		_e09 = new UEdge(_n04, _n06, 9.0);
		
		_g.add(_n01);
		_g.add(_n02);
		_g.add(_n03);
		_g.add(_n04);
		_g.add(_n05);
		_g.add(_n06);
		_g.add(_n07);
		_g.add(_n08);
		_g.add(_n09);
		_g.add(_n10);
		_g.add(_e01);
		_g.add(_e02);
		_g.add(_e03);
		_g.add(_e04);
		_g.add(_e05);
		_g.add(_e06);
		_g.add(_e07);
		_g.add(_e08);
		_g.add(_e09);
	}

	protected void tearDown() throws java.lang.Exception {
		_n01 = _n02 = _n03 = _n04 = _n05 = _n06 = _n07 = _n08 = _n09 = _n10 = null;
		_e01 = _e02 = _e03 = _e04 = _e05 = _e06 = _e07 = _e08 = _e09 = null;
	}

	public static junit.framework.Test suite() {
		junit.framework.TestSuite suite = new junit.framework.TestSuite(UGraphTest.class);
		
		return suite;
	}

	/**
	 * Test of getEdges method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testGetEdges() {
		System.out.println("testGetEdges");
		

		assertTrue(_g.getEdges().contains(_e01));
		assertTrue(_g.getEdges().contains(_e02));
		assertTrue(_g.getEdges().contains(_e03));
		assertTrue(_g.getEdges().contains(_e04));
		assertTrue(_g.getEdges().contains(_e05));
		assertTrue(_g.getEdges().contains(_e06));
		assertTrue(_g.getEdges().contains(_e07));
		assertTrue(_g.getEdges().contains(_e08));
		assertTrue(_g.getEdges().contains(_e09));
	}

	/**
	 * Test of getVertices method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testGetVertices() {
		System.out.println("testGetVertices");

		assertTrue(_g.getVertices().contains(_n01));
		assertTrue(_g.getVertices().contains(_n02));
		assertTrue(_g.getVertices().contains(_n03));
		assertTrue(_g.getVertices().contains(_n04));
		assertTrue(_g.getVertices().contains(_n05));
		assertTrue(_g.getVertices().contains(_n06));
		assertTrue(_g.getVertices().contains(_n07));
		assertTrue(_g.getVertices().contains(_n08));
		assertTrue(_g.getVertices().contains(_n09));
		assertTrue(_g.getVertices().contains(_n10));
	}

	/**
	 * Test of numVertices method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testNumVertices() {
		System.out.println("testNumVertices");

		assertTrue(_g.getVertices().size() == 10);
		assertTrue(_g.getVertices().size() == _g.numVertices());
	}

	/**
	 * Test of numEdges method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testNumEdges() {
		System.out.println("testNumEdges");

		assertTrue(_g.getEdges().size() == 9);
		assertTrue(_g.getEdges().size() == _g.numEdges());
	}

	/**
	 * Test of isDirected method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testIsDirected() {
		System.out.println("testIsDirected");

		assertTrue(_g.isDirected() == false);
	}

	/**
	 * Test of edgeLen method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testEdgeLen() {
		System.out.println("testEdgeLen");
		
		assertTrue(_g.edgeLen(_n01, _n02) == 1.0);
		assertTrue(_g.edgeLen(_n01, _n03) == 2.0);
		assertTrue(_g.edgeLen(_n01, _n04) == 3.0);
		assertTrue(_g.edgeLen(_n02, _n07) == 4.0);
		assertTrue(_g.edgeLen(_n02, _n08) == 5.0);
		assertTrue(_g.edgeLen(_n03, _n09) == 6.0);
		assertTrue(_g.edgeLen(_n03, _n10) == 7.0);
		assertTrue(_g.edgeLen(_n04, _n05) == 8.0);
		assertTrue(_g.edgeLen(_n04, _n06) == 9.0);
		
		try {
			_g.edgeLen(_n01, new UVertex("foobar"));
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		
		try {
			_g.edgeLen(new UVertex("foobar"), _n04);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		
		assertTrue(_g.edgeLen(_n08, _n07) == 0);
	}

	/**
	 * Test of newInstance method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testNewInstance() {
		System.out.println("testNewInstance");

		assertTrue(_g.newInstance() instanceof UGraph);
	}

	/**
	 * Test of findVertex method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testFindVertex() {
		System.out.println("testFindVertex");

		assertTrue(_n01 == _g.findVertex("n01"));
		assertTrue(_n02 == _g.findVertex("n02"));
		assertTrue(_n03 == _g.findVertex("n03"));
		assertTrue(_n04 == _g.findVertex("n04"));
		assertTrue(_n05 == _g.findVertex("n05"));
		assertTrue(_n06 == _g.findVertex("n06"));
		assertTrue(_n07 == _g.findVertex("n07"));
		assertTrue(_n08 == _g.findVertex("n08"));
		assertTrue(_n09 == _g.findVertex("n09"));
		assertTrue(_n10 == _g.findVertex("n10"));
		assertTrue(null == _g.findVertex("foobar"));
	}

	/**
	 * Test of shortestPath method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testShortestPath() {
		System.out.println("testShortestPath");
		System.out.println("   This test is deferred to the nv2d.shortestpaths algorithms");		
	}

	/**
	 * Test of add, remove method, of class nv2d.graph.undirected.UGraph.
	 *
	 * TODO: ensure that UEdge behaves according to the specs when given invalid
	 * (null, non-existing graph elements, etc) input.
	 */
	public void testAddRemove() {
		System.out.println("testAddRemove");
		
		assertTrue(false == _g.remove(new UVertex("foobar")));
		assertTrue(false == _g.add(_n01));
		assertTrue(false == _g.add(_e01));
		assertTrue(false == _g.add(new DVertex("directed vertex")));
		assertTrue(_g.findVertex("directed vertex") == null);

		/* remove nodes (also tests edge removal) */
		_g.remove(_n01);
		// three edges should also be gone
		assertTrue(null == _g.findVertex("n01"));
		try {
			_g.edgeLen(_n01, _n02);
			fail("edge not removed correctly in response to vertex removal");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n01, _n03);
			fail("edge not removed correctly in response to vertex removal");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n01, _n04);
			fail("edge not removed correctly in response to vertex removal");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}

		// edges should not be added back in (make sure they were removed correctly)
		_g.add(_n01);
		
		assertTrue(_g.edgeLen(_n01, _n02) == 0);
		assertTrue(_g.edgeLen(_n01, _n03) == 0);
		assertTrue(_g.edgeLen(_n01, _n04) == 0);

		// these were removed from the node removal
		_g.add(_e01);
		_g.add(_e02);
		_g.add(_e03);
		
		assertTrue(_g.edgeLen(_n01, _n02) == 1.0);
		assertTrue(_g.edgeLen(_n01, _n03) == 2.0);
		assertTrue(_g.edgeLen(_n01, _n04) == 3.0);
		
		_g.remove(_e01);

		assertTrue(_g.edgeLen(_n01, _n02) == 0);
	}

	/**
	 * Test of clear method, of class nv2d.graph.undirected.UGraph.
	 */
	public void testClear() {
		System.out.println("testClear");
		
		_g.clear();
		
		assertTrue(null == _g.findVertex("n01"));
		assertTrue(null == _g.findVertex("n02"));
		assertTrue(null == _g.findVertex("n03"));
		assertTrue(null == _g.findVertex("n04"));
		assertTrue(null == _g.findVertex("n05"));
		assertTrue(null == _g.findVertex("n06"));
		assertTrue(null == _g.findVertex("n07"));
		assertTrue(null == _g.findVertex("n08"));
		assertTrue(null == _g.findVertex("n09"));
		assertTrue(null == _g.findVertex("n10"));
		assertTrue(null == _g.findVertex("foobar"));
		
		try {
			_g.edgeLen(_n01, _n02);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n01, _n02);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n01, _n02);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n02, _n07);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n02, _n08);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n03, _n09);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n03, _n10);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n04, _n05);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
		try {
			_g.edgeLen(_n04, _n06);
			fail("Invalid input must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			// correct bahavior
		}
	}
}
