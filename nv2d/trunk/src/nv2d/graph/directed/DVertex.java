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

package nv2d.graph.directed;

/*
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
*/
// import java.io.Serializable;
import java.lang.IllegalArgumentException;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import nv2d.graph.Vertex;
import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;

public class DVertex extends Vertex {
	// the intersection of the following two sets should be null
	transient private Set _inEdges;
	transient private Set _outEdges;

	public DVertex(String id) {
		super(id);
		_inEdges = new HashSet();
		_outEdges = new HashSet();
	}

	public Set inEdges() {
		return _inEdges;
	}

	public Set outEdges() {
		return _outEdges;
	}

	public Set neighbors() {
		HashSet set = new HashSet();
		Iterator it;

		for(it = _inEdges.iterator(); it.hasNext(); ) {
			set.add(((Edge) it.next()).getOpposite(this));
		}

		for(it = _outEdges.iterator(); it.hasNext(); ) {
			set.add(((Edge) it.next()).getOpposite(this));
		}
		
		return set;
	}

	public GraphElement clone(Graph destGraph) {
		DVertex v = new DVertex(id());
		Set attr = getDatumSet();
		Iterator i = attr.iterator();
		while(i.hasNext()) {
			Datum d = (Datum) i.next();
			if(!d.name().matches("__.*:.*")) {
				// not a 'reserved' system datum
				v.setDatum(d);
			}
		}
		return v;
	}

	void addInEdge(Edge e) {
		// make sure arguments are okay
		if(e.getClass() == DEdge.class && ((DEdge) e).getDest().equals(this)) {
			_inEdges.add(e);
			return;
		}

		throw new IllegalArgumentException("Could not add edge to this vertex");
	}

	void addOutEdge(Edge e) {
		// make sure arguments are okay
		if(e.getClass() == DEdge.class && ((DEdge) e).getSource().equals(this)) {
			_outEdges.add(e);
			return;
		}
		throw new IllegalArgumentException("Could not add edge to this vertex");
	}

	boolean removeEdge(Edge e) {
		return (_inEdges.remove(e) || _outEdges.remove(e));
	}



	/** <p>This class (along withany implementation of Edge) is suspected of
	 * causing stack overflows (there is a recursive/circular structure to how
	 * data is organized and this causes problems).</p>
	 *
	 * <p>Update:  The data organization was not the problem.  The problem was
	 * that a class (render.VertexNode) overrode a PNode method and this caused
	 * the stack overflow. -- 6.19.2004</p>
	 *
	 * Here, we use a serialization method which is more friendly to our
	 * data organization.  For details, see URL
	 * <http://swjscmail1.java.sun.com/cgi-bin/wa?A2=ind9909&L=rmi-users&F=&S=&P=34494>.
	 * */
	/*
	private void writeObject(ObjectOutputStream out) throws IOException {
		Iterator i = _inEdges.iterator();
		while(i.hasNext()) {
			out.writeObject(i.next());
		}
		Iterator i2 = _outEdges.iterator();
		while(i2.hasNext()) {
			out.writeObject(i2.next());
		}
		out.writeObject(null);
	}

	private void readObject(ObjectInputStream in) 
		throws IOException, ClassNotFoundException {
		Iterator i = _inEdges.iterator();
		while(i.hasNext()) {
			Edge e = (Edge) i.next();
			e = (Edge) in.readObject();
		}
		Iterator i2 = _outEdges.iterator();
		while(i2.hasNext()) {
			Edge e = (Edge) i2.next();
			e = (Edge) in.readObject();
		}
	}
	*/

}
