package nv2d.graph.directed;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.IllegalArgumentException;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import nv2d.graph.Vertex;
import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;

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

	public Vertex clone() {
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

}
