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

package nv2d.ui;

import java.util.*;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;

/** This class manages the different attributes of a graph and maintaines
 * a legend for each one. */
public class LegendMap {
	private Graph _g;
	private Set _datums;
	
	private Hashtable _colorLegends;
	
	public LegendMap(Graph model) {
		_g = model;
		
		scan();
	}
	
	public void scan() {
		_datums = new HashSet();
		Iterator i = _g.getVertices().iterator();
		while(i.hasNext()) {
			_datums.addAll(((Vertex) i.next()).getDatumSet());
		}
	}
	
	public Set datumSet() {
		return _datums;
	}
	
	public ColorLegend getLegend(String attribute) {
		if(_datums.contains(attribute)) {
			if(_colorLegends.containsValue(attribute)) {
				return (ColorLegend) _colorLegends.get(attribute);
			} else {
				ColorLegend newLegend = new ColorLegend(_g, attribute);
				_colorLegends.put(attribute, newLegend);
				return newLegend;
			}
		}
		return null;
	}
}