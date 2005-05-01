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

import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.DefaultNode;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.Edge;

public class PGraph extends DefaultGraph {
	public static final String DATUM_POBJ = "__prefuse:linkobj";

	private Graph _g;

	public PGraph(Graph g) {
		super(g == null || g.isDirected());

		_g = g;

		if(g == null) {
			return;
		}

		Iterator vi = g.getVertices().iterator();
		while(vi.hasNext()) {
			addNode(new PNode((Vertex) vi.next()));
		}
		Iterator ei = g.getEdges().iterator();
		while(ei.hasNext()) {
			Edge e = (Edge) ei.next();
			DefaultNode car = PNode.v2p((Vertex) e.getEnds().car());
			DefaultNode cdr = PNode.v2p((Vertex) e.getEnds().cdr());
			addEdge(new PEdge(e, car, cdr));
		}
	}
}
