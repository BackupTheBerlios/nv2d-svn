package nv2d.graph.filter;

import nv2d.graph.Graph;
import nv2d.graph.FilterInterface;

/* Identity filter */
public class DefaultFilter implements FilterInterface {
	Graph _g;

	public void initialize(Graph g, Object [] args) {
		_g = g;
	}
	
	public Object [] lastArgs() {
		return new Object[0];
	}

	public Graph filter() {
		return _g;
	}
}
