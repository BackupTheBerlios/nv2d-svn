package nv2d.algorithms.shortestpaths;

import java.lang.Float;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nv2d.algorithms.AlgConst;
import nv2d.exceptions.NoPathExists;
import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.Path;
import nv2d.graph.Vertex;

/** This class takes a Graph and computes shortest paths using Dijkstra's algorithm.
 *
 * <b>Requirements</b>
 * <ol>
 * 	<li><b>Graph input:</b> The graph must be directed. NOTE: this is not enforced.  You can use a generic graph, but the results are not guaranteed to be correct.</li>
 * 	<li><b>Caching:</b> The results of a search shall not be discarded.
 * 	<ol>
 * 		<li>If a subsequent search uses the same source Vertex, the class shall look at the destination Vertex for cached information.</li>
 * 	</ol>
 * 	<li><b>Options</b></li>
 * </ol>
 * */

public class Dijkstra {
	protected boolean _cached;
	protected Graph _g;		// graph
	protected Vertex _s;	// source
	protected Set S;		// no underscore b/c they are significant.  this is a rare deviation from convention
	protected Set Q;

	public Dijkstra(Graph g) {
		_g = g;
		_s = null;
		_cached = false;
	}

	/** Get the shortest path between two nodes. 
	 * If the 
	 * */
	public Path getPath(Vertex source, Vertex dest) throws NoPathExists {
		if(!_g.getVertices().contains(source) || !_g.getVertices().contains(dest)) {
			// make sure that source and dest belong to this network
			throw new NoPathExists(source, dest);
		}

		if(_cached == true && source.equals(_s)) {
			return buildPath(dest);
		}

		_s = source;
		init(_g, _s);
		run();
		return buildPath(dest);
	}

	/** Build a path starting with endpoint.
	 * <p>We build a path from <code>_s</code> to <code>end</code>.</p>
	 * */
	private Path buildPath(Vertex end) throws NoPathExists {
		Path p = new Path(_g);
		List l = new Vector();
		l.add(end);

		if(end.equals(_s)) {
			// this is silly, but somebody might do it.
			p.addVertex(_s);
			return p;
		}

		Vertex v = predecessor(end);
		l.add(v);
		while(!v.equals(_s)) {
			v = predecessor(v);
			if(v == null) {
				// there was no predecessor -- this means that there is no path
				// between the source and dest
				throw new NoPathExists(_s, end);
			}
			l.add(v);
		}

		// current list is in reverse order, so reverse it
		for(int i = l.size() - 1; i >= 0; i--) {
			p.addVertex((Vertex) l.get(i));
		}
		return p;
	}

	/** Prepare a graph for the algorithm. */
	public void init(Graph g, Vertex s) {
		_g = g;
		_s = s;

		Iterator it = _g.getVertices().iterator();

		while(it.hasNext()) {
			Vertex v = (Vertex) it.next();
			v.setDatum(new Datum(AlgConst.DATUM_DIJKSTRA_SOURCE, _s));
			v.setDatum(new Datum(AlgConst.DATUM_DIJKSTRA_D, new Float(Float.MAX_VALUE)));
			v.setDatum(new Datum(AlgConst.DATUM_DIJKSTRA_P, null));
		}

		_s.getDatum(AlgConst.DATUM_DIJKSTRA_D).set(new Float(0.0f));
		_s.getDatum(AlgConst.DATUM_DIJKSTRA_P).set(_s);

		_cached = true;
	}

	public void run() {
		S = new HashSet();
		Q = new HashSet();
		Q.add(_s);

		// main loop
		while(!Q.isEmpty()) {
			Vertex u = cheapest(Q);
			S.add(u);
			relax(u);
		}

		_cached = true;
	}

	private void relax(Vertex u) {
		Set outEdges = u.outEdges();
		Iterator it = outEdges.iterator();
		while(it.hasNext()) {
			Edge e = (Edge) it.next();
			// for each vertex v adjacent to u and not in S
			Vertex v = e.getOpposite(u);
			if(!S.contains(v) && estimate(v) > (estimate(u) + e.length())) {
				float vd = estimate(u) + e.length();

				// v gets a new estimate (not really an estimate any more)
				v.getDatum(AlgConst.DATUM_DIJKSTRA_D).set(new Float(vd));
				// u is the predecessor of v
				v.getDatum(AlgConst.DATUM_DIJKSTRA_P).set(u);

				// add v to Q
				Q.add(v);
			}
		}
	}

	/** Remove the node with the shortest estimated distance from the
	 * <code>Set</code> and return it. */
	private Vertex cheapest(Set Q) {
		Iterator it = Q.iterator();

		Vertex cheap = (Vertex) it.next();
		while(it.hasNext()) {
			Vertex v = (Vertex) it.next();
			if(estimate(v) < estimate(cheap)) {
				cheap = v;
			}
		}

		Q.remove(cheap);
		return cheap;
	}

	private Vertex predecessor(Vertex v) {
		return (Vertex) v.getDatum(AlgConst.DATUM_DIJKSTRA_P).get();
	}

	private float estimate(Vertex v) {
		return ((Float) v.getDatum(AlgConst.DATUM_DIJKSTRA_D).get()).floatValue();
	}
}
