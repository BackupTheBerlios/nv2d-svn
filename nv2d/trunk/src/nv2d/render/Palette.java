package nv2d.render;

import java.awt.geom.Dimension2D;
import java.lang.Math;
import java.util.Iterator;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.graph.Vertex;

public class Palette extends PCanvas {
	/** Initialize the graph with the Canvas.  GraphElement's get their own
	 * layer and other display data get their own layer. */
	public void initialize(Graph g) {
		Dimension2D d = getCamera().getViewBounds().getSize();
		double X = d.getWidth();
		double Y = d.getHeight();

		Iterator itv = g.getVertices().iterator();

		while(itv.hasNext()) {
			// pick a random spot for the Piccolo Node object
			VertexNode cn = new VertexNode(RenderConstants.DEFAULT_VERTEX_RADIUS, (Vertex) itv.next());

			// add a Label
			PText label = new PText(cn.owner().id());
			double offset = 2.0 * RenderConstants.DEFAULT_VERTEX_RADIUS;
			cn.addChild(label);

			cn.centerBoundsOnPoint(X * Math.random(), Y * Math.random());
			label.offset(offset, offset);
			cn.signalBoundsChanged();
			getLayer().addChild(cn);
		}

		Iterator ite = g.getEdges().iterator();
		while(ite.hasNext()) {
			EdgeNode en = new EdgeNode((Edge) ite.next());
			en.update();

			getLayer().addChild(en);
		}

		addInputEventListener(new MouseHandler());
	}
}
