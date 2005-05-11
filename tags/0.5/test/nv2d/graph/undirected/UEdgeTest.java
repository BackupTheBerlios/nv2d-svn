/*
 * UEdgeTest.java
 * JUnit based test
 *
 * Created on April 22, 2005, 8:21 PM
 */

package nv2d.graph.undirected;

import junit.framework.*;
import java.util.Iterator;
import java.util.Set;
import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.graph.Vertex;
import nv2d.utils.Pair;

/**
 *
 * @author bshi
 */
public class UEdgeTest extends TestCase {
	private UVertex _a, _aa, _b, _bb, _c, _d, _e;
	private UEdge _eab, _eaabb, _eba;
	
	public UEdgeTest(String testName) {
		super(testName);
	}

	protected void setUp() throws java.lang.Exception {
		_a = new UVertex("a");
		_aa = new UVertex("a");
		_b = new UVertex("b");
		_bb = new UVertex("b");
		_c = new UVertex("c");
		_eab = new UEdge(_a, _b, 1.0);
		_eaabb = new UEdge(_aa, _bb, 1.0);
		_eba = new UEdge(_b, _a, 2.0);
	}

	protected void tearDown() throws java.lang.Exception {
		_a = null;
		_aa = null;
		_b = null;
		_bb = null;
		_c = null;
		_d = null;
		_e = null;
		_eab = null;
		_eaabb = null;
		_eba = null;
	}

	public static junit.framework.Test suite() {
		junit.framework.TestSuite suite = new junit.framework.TestSuite(UEdgeTest.class);
		
		return suite;
	}
	
	public void testConstructor() {
		try {
			UEdge edge = new UEdge(_a, null, 1.0);
			fail("illegal argument should be thrown for invalid edge constructor arguments");
		} catch (NullPointerException e) {
			// correct behavior
		}
	}

	/**
	 * Test of equals method, of class nv2d.graph.undirected.UEdge.
	 */
	public void testEquals() {
		System.out.println("testEquals");
		
		assertTrue(_eab.equals(_eba));
		assertTrue(_eab.equals(_eaabb));
		assertTrue(!_eab.equals(new UEdge(_c, _b, 1.0)));
		assertTrue(!_eab.equals(_a));
	}

	/**
	 * Test of hashCode method, of class nv2d.graph.undirected.UEdge.
	 */
	public void testHashCode() {
		System.out.println("testHashCode");
		
		assertTrue(_eab.hashCode() == _eba.hashCode());
		assertTrue(_eaabb.hashCode() == _eab.hashCode());
	}

	/**
	 * Test of length method, of class nv2d.graph.undirected.UEdge.
	 */
	public void testLength() {
		System.out.println("testLength");
		
		assertTrue(_eab.length() == 1.0);
		assertTrue(_eba.length() == 2.0);
		assertTrue(_eaabb.length() == 1.0);
	}

	/**
	 * Test of getEnds, getCar, and getCdr method, of class nv2d.graph.undirected.UEdge.
	 */
	public void testGetEnds() {
		System.out.println("testGetEnds");
		
		assertTrue(_eab.getEnds().car() == _eab.getCar());
		assertTrue(_eab.getEnds().cdr() == _eab.getCdr());		
		assertTrue(_eba.getEnds().car() == _eba.getCar());
		assertTrue(_eba.getEnds().cdr() == _eba.getCdr());
		assertTrue(_eab.getEnds().car() == _a);
		assertTrue(_eba.getEnds().car() == _b);
		assertTrue(_eab.getEnds().cdr() == _b);
		assertTrue(_eba.getEnds().cdr() == _a);
	}

	/**
	 * Test of getOpposite method, of class nv2d.graph.undirected.UEdge.
	 */
	public void testGetOpposite() {
		System.out.println("testGetOpposite");
		
		_eab = new UEdge(_a, _b, 1.0);
		_eaabb = new UEdge(_aa, _bb, 1.0);
		_eba = new UEdge(_b, _a, 2.0);
		
		assertTrue(_eab.getOpposite(_a) == _b);
		assertTrue(_eab.getOpposite(_b) == _a);
		// TODO: not sure that this is the behavior that we want
		// this is true since we use "equals" to test for equality
		assertTrue(_eba.getOpposite(_aa) == _b);
		assertTrue(_eba.getOpposite(_b) == _a);
		
		try {
			_eab.getOpposite(_c);
			fail("An exception was not thrown for an illegal argument");
		} catch(IllegalArgumentException ex) {
			// correct behavior
		}
	}

	/**
	 * Test of setLength method, of class nv2d.graph.undirected.UEdge.
	 */
	public void testSetLength() {
		System.out.println("testSetLength");
		
		_eab.setLength(230.0);
		assertTrue(_eab.length() == 230.0);
		_eab.setLength(-230.0);
		assertTrue(_eab.length() == -230.0);
	}

	/**
	 * Test of clone method, of class nv2d.graph.undirected.UEdge.
	 */
	public void testClone() {
		System.out.println("testClone");
		
		UGraph originalOwner = new UGraph();
		UGraph g = new UGraph();
		
		originalOwner.add(_eab);
		assertTrue(null == _eab.clone(originalOwner));
		
		Integer data1 = new Integer(2345);
		String data2 = "should not be cloned";
		String data3 = "a";
		
		_eab.setDatum(new Datum("number", data1));
		_eab.setDatum(new Datum("__private:datum", data2));
		_eab.setDatum(new Datum("character", data3));
		
		// we haven't cloned the vertices yet
		// this will produce error output that can be safely ignored
		assertTrue(_eab.clone(g) == null);
		assertTrue(_eab.clone(null) == null);


		g.add(_a.clone(g));
		g.add(_b.clone(g));
		UEdge _aClone = (UEdge) _eab.clone(g);
		
		assertTrue(_aClone.getParent() == g);
		assertTrue(_aClone != _eab);
		assertTrue(_aClone.length() == _eab.length());
		assertTrue(_aClone.id().equals(_eab.id()));
		assertTrue(_aClone.getOpposite(_a) == g.findVertex(_b.id()));
		assertTrue(_aClone.getOpposite(_b) == g.findVertex(_a.id()));
		
		assertTrue(_aClone.getDatum("number").get() == data1);
		assertTrue(_aClone.getDatum("__private:datum") == null);
		assertTrue(_aClone.getDatum("character").get() == data3);
	}
}
