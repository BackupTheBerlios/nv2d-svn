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
