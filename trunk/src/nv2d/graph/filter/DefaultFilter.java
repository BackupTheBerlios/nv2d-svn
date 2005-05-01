/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * $Id$
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

package nv2d.graph.filter;

import nv2d.graph.Graph;
import nv2d.graph.FilterInterface;

/* Identity filter */
public class DefaultFilter implements FilterInterface {
	Graph _g;

	public void initialize(Graph g, Object [] args) {
		_g = g;
	}
	
	public Object [] lastArgs() {
		return new Object[0];
	}

	public Graph filter() {
		return _g;
	}
}
