package nv2d.graph.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nv2d.graph.FilterInterface;
import nv2d.graph.Graph;
import nv2d.graph.Edge;
import nv2d.graph.Vertex;

public class DegreeFilter implements FilterInterface {
	private int _deg;
	private boolean _inited;
	private Vertex _center;
	private Graph _g;

	public DegreeFilter() {
		_inited = false;
	}

	public void initialize(Graph g, Vertex center, int i) {
		if(g == null) {
			System.err.println("Error: Graph is null.");
			_inited = false;
			return;
		}
		if(i > 1) {
			_deg = i;
		} else {
			System.err.println("Error: attempted to set degree less than 1 (setting to 1)");
			_deg = 1;
		}
		if(g.getVertices().contains(center)) {
			_center = center;
		} else {
			System.err.println("Error: attempted to set a center Vertex which does not exist in this graph.  Using random center");
			Iterator iter = g.getVertices().iterator();
			if(iter.hasNext()) {
				_center = (Vertex) iter.next();
			} else {
				_inited = false;
				return;
			}
		}
		_inited = true;
	}

	public Graph filter(Graph g) {
		if(!_inited) {
			System.err.println("Error: Degree filter has not been initialized.");
			return null;
		}
		int i = _deg;
		Set vertices = new HashSet();
		Set tmpSet = new HashSet();
		vertices.add(_center);

		tmpSet = _center.outEdges();
		vertices.addAll(tmpSet);
		i--;

		// grab vertices
		for(int j = i; j > 0; j--) {
			tmpSet = getOutEdges(tmpSet);
			vertices.addAll(tmpSet);
		}
		return g.subset(vertices);
	}

	private Set getOutEdges(Set s) {
		Set outSet = new HashSet();
		Iterator i = s.iterator();
		while(i.hasNext()) {
			outSet.addAll(((Vertex) i.next()).outEdges());
		}
		return outSet;
	}
}
