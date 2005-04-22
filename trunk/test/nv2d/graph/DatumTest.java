/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package nv2d.graph;

import java.lang.IllegalArgumentException;
import java.awt.Color;

import junit.framework.TestCase;


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
		} catch(IllegalArgumentException ex) {
			passed = true;
		}
		assertTrue(passed);

		passed = false;
		try {
			a = new Datum("a", Color.blue);
			String ds = new String("test");
			a.compareTo(ds);
		} catch(ClassCastException ex) {
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
