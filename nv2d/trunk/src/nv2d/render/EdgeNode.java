package nv2d.render;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import nv2d.graph.Edge;
import nv2d.graph.GraphElement;
import nv2d.graph.Vertex;
import nv2d.utils.Pair;

public class EdgeNode extends RElement implements PropertyChangeListener {
	VertexNode _source;
	VertexNode _dest;

	public EdgeNode(Edge owner)
	{
		super(owner);

		_source = getRElement((Vertex) owner.getEnds().car());
		_dest = getRElement((Vertex) owner.getEnds().cdr());

		// Listen for changes in vertex position so that edge is updated
		_source.addPropertyChangeListener(this);
		_dest.addPropertyChangeListener(this);
	}

	public VertexNode getRElement(Vertex v)
	{
		return (VertexNode) v.getDatum(RenderConstants.DATUM_RELEMENT_POINTER).get();
	}

	/** Whenever the endpoints may have been moved, it is best to call this
	 * method so that it can sync with it's endpoints. */
	public void update()
	{
		VertexNode car, cdr;

		car = getRElement( (Vertex) ((Edge) owner()).getEnds().car() );
		cdr = getRElement( (Vertex) ((Edge) owner()).getEnds().cdr() );

		setPathTo(new Line2D.Double(car.getBounds().getCenter2D(),
					cdr.getBounds().getCenter2D()));
	}

	public void propertyChange(PropertyChangeEvent event)
	{
		update();
	}
}
