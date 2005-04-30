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

import java.awt.Color;

import junit.framework.TestCase;


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

	//public static void main(String args[]) {
	//	junit.textui.TestRunner.run(DataStoreTest.class);
	//}

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
	
	public void testRemDatum() {
		A.setDatum(a);
		A.setDatum(b);
		A.setDatum(c);

		A.remDatum("red");
		assertTrue(A.getDatum("red") == null);
		assertTrue(A.getDatum("green") == b && A.getDatum("blue") == c);
		
		A.remDatum("green");
		assertTrue(A.getDatum("red") == null && A.getDatum("green") == null);
		assertTrue(A.getDatum("blue") == c);
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
		assertTrue(A.equals("a") == false);
	}

	public void testNull() {
		a.set(null);
		assertTrue(null == a.get());
	}
}
