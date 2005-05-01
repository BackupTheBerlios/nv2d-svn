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

package nv2d.render;

import java.awt.*;

import nv2d.ui.NController;
import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.Datum;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;

/**
 *
 * @author bshi
 */
public class LegendColorizer extends ColorFunction {
	public static final String DATUM_LEGENDCOLOR = "__colorlegend:vertexcolor";

	private Graph _model;
	
	public LegendColorizer(NController ctl) {
		_model = ctl.getModel();
	}
	
	public Paint getFillColor(VisualItem i) {
		if(i.getEntity() instanceof PNode) {
			PNode p = (PNode) i.getEntity();
			
			// TODO: this is a performance bottleneck (runs binary search)
			// A possible fix is to use a normal datum.
			Vertex v = _model.findVertex(p.v().id());
			Datum d = null;
			
			if(v != null) {
				d = v.getDatum(DATUM_LEGENDCOLOR);
			}
			// Datum d = p.v().getDatum(DATUM_LEGENDCOLOR);
			if(d != null) {
				return (Color) d.get();
			}
		}
		return Colorizer.fill_normal;
	}
}
