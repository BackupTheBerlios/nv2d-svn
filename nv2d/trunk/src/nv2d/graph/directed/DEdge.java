package nv2d.graph.directed;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.IllegalArgumentException;

import nv2d.graph.Edge;
import nv2d.graph.Vertex;
import nv2d.graph.Graph;
import nv2d.utils.Pair;

public class DEdge extends Edge implements Serializable {
	transient private Pair _v;
	private float _len;

	public DEdge(DVertex source, DVertex dest, float length) {
		super("DirectedEdge ["
			+ source.id() + "]-->["
			+ dest.id() + "]");

		if(source == null || dest == null || source.equals(dest)) {
			throw new IllegalArgumentException("Could not create edge");
		}

		_v = new Pair(source, dest);
		_len = length;
	}

	public Vertex getSource() {
		return (Vertex) _v.car();
	}

	public Vertex getDest() {
		return (Vertex) _v.cdr();
	}

	public float length() {
		return _len;
	}

	public void setLength(float l) {
		_len = l;
	}

	public Pair getEnds() {
		return _v;
	}

	public Vertex getOpposite(Vertex v) {
		if(v.equals(_v.car())) {
			return (Vertex) _v.cdr();
		} else if(v.equals(_v.cdr())) {
			return (Vertex) _v.car();
		}
		// v is not part of the edge
		// throw an exception
		throw (new IllegalArgumentException("DirectedEdge.getOpposite() -- argument must be a vertex which is incident to the edge"));
	}

	/** <p>This class (along withany implementation of Vertex) is suspected of
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
		out.writeObject(_v);
		out.writeObject(null);
	}

	private void readObject(ObjectInputStream in) 
		throws IOException, ClassNotFoundException {
		_v = (Pair) in.readObject();
	}
}
