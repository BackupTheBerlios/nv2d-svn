package nv2d.render;

import java.lang.Math;
import java.awt.geom.Point2D;

import nv2d.graph.GraphElement;
import nv2d.graph.Vertex;

public class VertexNode extends RElement {
	protected double _radius;

	public VertexNode(double radius, Vertex owner) {
		super(owner);

		_radius = radius;
		setPathToEllipse(0.0f, 0.0f, (float) radius * 2.0f, (float) radius * 2.0f);
	}

	/** We will display the children with offsets around us */
	public void layoutChildren() {
		for(int i = 0; i < getChildrenCount(); i++) {
			getChild(0).setBounds(getChild(0).getBounds().setOrigin(getX() + _radius, getY() + _radius));
		}
	}

	/** *** IMPORTANT ***  Overriding some methods causes a stack overflow.
	 * The <code>translate</code> method is overridden so we get a chance to
	 * update our variable keeping track of the center of the node. */
	/* public void translate(float dx, float dy) {
		double cx = (double) dx + _radius / 2.0;
		double cy = (double) dy + _radius / 2.0;
		_loc.setLocation(_loc.getX() + cx,
				_loc.getY() + cy);

		translate(dx, dy);
	} */
}
