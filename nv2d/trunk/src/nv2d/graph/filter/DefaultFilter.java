package nv2d.graph.filter;

import nv2d.graph.Graph;
import nv2d.graph.FilterInterface;

/* Identity filter */
public class DefaultFilter implements FilterInterface {

	public void initialize(Graph g, Object [] args) {
		return;
	}

	public Graph filter(Graph g) {
		return g;
	}
}
