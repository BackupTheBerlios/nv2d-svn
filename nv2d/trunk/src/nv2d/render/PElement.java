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

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultNode;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Vertex;
import nv2d.graph.GraphElement;
import nv2d.graph.directed.DEdge;

public interface PElement {
	public void setPathElement(boolean b);
	public void setSelected(boolean b);

	public boolean isPathElement();
	public boolean isSelected();

	public GraphElement getNV2DGraphElement();
}
