package nv2d.render;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.umd.cs.piccolo.nodes.PPath;

import nv2d.graph.Datum;
import nv2d.graph.GraphElement;
import nv2d.utils.Pair;

/** The rendering of network elements should have some tie to the network
 * elements they represent.  This will be used for filtering. */

public class RElement extends PPath /* implements Serializable */ {
	protected transient GraphElement _owner;

	public RElement() {
		_owner = null;
	}

	public RElement(GraphElement o) {
		setOwner(o);
	}

	public GraphElement owner() {
		return _owner;
	}

	public void setOwner(GraphElement o) {
		_owner = o;
		o.setDatum(new Datum(RenderConstants.DATUM_RELEMENT_POINTER, this));
	}

	/** To get rid of stack overflows due to the high level of recursion used
	 * with our numerous circular pointers.  For details, visit the URL
	 * <http://swjscmail1.java.sun.com/cgi-bin/wa?A2=ind9909&L=rmi-users&F=&S=&P=34494>.
	 *
	 * <p>Update:  RElement was not the problem.  It turns out that we need to
	 * be <b>extremely</b> careful in overriding Piccolo class methods.</p> */
	/*
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(_owner);
		out.writeObject(null);
	}

	private void readObject(ObjectInputStream in) 
		throws IOException, ClassNotFoundException {
		_owner = (GraphElement) in.readObject();
	}
	*/
}
