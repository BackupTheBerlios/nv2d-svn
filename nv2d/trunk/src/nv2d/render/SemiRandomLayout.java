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

package nv2d.render;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.assignment.Layout;

import nv2d.graph.Vertex;
import nv2d.graph.Datum;
import nv2d.render.PNode;
import nv2d.ui.NController;

/**
 * Performs a random layout of <b>new</b> graph nodes within the layout's
 * bounds.  Nodes which have been displayed before get restored to their
 * previous location.  This is a modified version of Prefuse's RandomLayout
 * class.
 *
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 * @author Bo Shi
 */
public class SemiRandomLayout extends Layout {
	NController _ctl;
	
	public SemiRandomLayout(NController ctl) {
		_ctl = ctl;
	}
	
	// FIXME
	public void run(ItemRegistry registry, double frac) {
		// RenderBox.DATUM_LASTLOCATION
		Rectangle2D b = getLayoutBounds(registry);
		double x, y;
		double w = b.getWidth();
		double h = b.getHeight();
		Iterator nodeIter = registry.getNodeItems();
		while ( nodeIter.hasNext() ) {
			VisualItem item = (VisualItem) nodeIter.next();
			Vertex v = _ctl.getModel().findVertex(((PNode) item.getEntity()).v().id());
			if(null != v && v.getDatum(RenderBox.DATUM_LASTLOCATION) != null) {
				Datum oldloc = v.getDatum(RenderBox.DATUM_LASTLOCATION);
				java.awt.geom.Point2D oldp = (java.awt.geom.Point2D) oldloc.get();
				setLocation(item, null, oldp.getX(), oldp.getY());
			} else {
				x = b.getX() + Math.random() * w;
				y = b.getY() + Math.random() * h;
				setLocation(item, null, x, y);
			}
		}
	} //
} // end of class RandomLayout
