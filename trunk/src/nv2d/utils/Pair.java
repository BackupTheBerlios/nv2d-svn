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

package nv2d.utils;

public class Pair {
	protected Object _car;
	protected Object _cdr;

	public Pair(Object a, Object b) {
		_car = a;
		_cdr = b;
	}

	public Object car() {
		return _car;
	}

	public Object cdr() {
		return _cdr;
	}

	public void setCar(Object o) {
		_car = o;
	}

	public void setCdr(Object o) {
		_cdr = o;
	}

	public boolean contains(Object o) {
		if(o.equals(_car) || o.equals(_cdr)) {
			return true;
		}
		return false;
	}

	public boolean equals(Object o) {
		if(o.getClass() != Pair.class) {
			return false;
		}

		return (_car.equals(((Pair) o)._car) && _cdr.equals(((Pair) o)._cdr));
	}
}
