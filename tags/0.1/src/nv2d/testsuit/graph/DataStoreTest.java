package nv2d.testsuit.graph;

import java.awt.Color;

import junit.framework.TestCase;

import nv2d.graph.Datum;
import nv2d.graph.DataStore;

public class DataStoreTest extends TestCase {
	private DataStore A;
	private DataStore B;
	private Datum a;
	private Datum b;
	private Datum c;
	private Datum d;
	private Datum e;
	private Datum f;

	public DataStoreTest(String name) {
		super(name);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(DataStoreTest.class);
	}

	protected void setUp() {
		A = new DataStore();
		B = new DataStore();
		a = new Datum("red", Color.red);
		b = new Datum("green", Color.green);
		c = new Datum("blue", Color.blue);

		d = new Datum("red", new String("red"));
		e = new Datum("green", new String("green"));
		f = new Datum("blue", new String("blue"));
	}

	protected void tearDown() {
		A = null;
		B = null;
		a = null;
		b = null;
		c = null;
		d = null;
		e = null;
		f = null;
	}

	public void testGetAndSet() {
		A.setDatum(a);
		A.setDatum(b);
		A.setDatum(c);

		assertTrue(A.getDatum("red") == a);
		assertTrue(A.getDatum("green") == b);
		assertTrue(A.getDatum("blue") == c);

		A.setDatum(d);
		A.setDatum(e);
		A.setDatum(f);

		assertTrue(A.getDatum("red") == d);
		assertTrue(A.getDatum("green") == e);
		assertTrue(A.getDatum("blue") == f);
	}

	public void testEquals() {
		assertTrue(A.equals(B));
		A.setDatum(a);
		A.setDatum(c);
		A.setDatum(b);
		assertTrue(!A.equals(B));
		B.setDatum(e);
		B.setDatum(f);
		B.setDatum(d);
		assertTrue(A.equals(B));
	}

	public void testNull() {
		a.set(null);
		assertTrue(null == a.get());
	}
}
