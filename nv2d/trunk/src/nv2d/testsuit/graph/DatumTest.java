package nv2d.testsuit.graph;

import java.lang.IllegalArgumentException;
import java.awt.Color;

import junit.framework.TestCase;

import nv2d.graph.Datum;

public class DatumTest extends TestCase {
	private Datum a;
	private Datum b;
	private Datum c;
	private Datum d;
	private Datum e;
	private Datum f;

	public DatumTest(String name) {
		super(name);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(DatumTest.class);
	}

	protected void setUp() {
		a = null;
		b = null;
		c = null;
	}

	protected void tearDown() {
		a = null;
		b = null;
		c = null;
	}

	public void testException() {
		boolean passed = false;
		try {
			a = new Datum(null, Color.red);
		} catch(IllegalArgumentException e) {
			passed = true;
		}
		assertTrue(passed);

		passed = false;
		try {
			a = new Datum("a", Color.blue);
			String d = new String("test");
			a.compareTo(d);
		} catch(ClassCastException e) {
			passed = true;
		}
		assertTrue(passed);
	}

	public void testAccessors() {
		a = new Datum("a", Color.red);

		assertTrue((Color) a.get() == Color.red);
		assertTrue(a.name().equals("a"));

		a.set(Color.blue);
		assertTrue((Color) a.get() == Color.blue);
	}

	public void testCompare() {
		int cab, cbc, cca;

		a = new Datum("alpha", "beta");
		b = new Datum("beta", "gamma");
		c = new Datum("alpha", (new Color(123, 234, 65)));

		cab = a.compareTo(b);
		cbc = b.compareTo(c);
		cca = c.compareTo(a);

		assertTrue(0 > cab && 0 < cbc && 0 == cca);
	}
}
