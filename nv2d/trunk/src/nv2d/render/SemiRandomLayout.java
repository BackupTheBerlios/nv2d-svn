package nv2d.render;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.assignment.Layout;

import nv2d.graph.Vertex;
import nv2d.graph.Datum;
import nv2d.render.PNode;
import nv2d.ui.NController;

/**
 * Performs a random layout of <b>new</b> graph nodes within the layout's
 * bounds.  Nodes which have been displayed before get restored to their
 * previous location.  This is a modified version of Prefuse's RandomLayout
 * class.
 *
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 * @author Bo Shi
 */
public class SemiRandomLayout extends Layout {
	NController _ctl;
	
	public SemiRandomLayout(NController ctl) {
		_ctl = ctl;
	}
	
	// FIXME
	public void run(ItemRegistry registry, double frac) {
		// RenderBox.DATUM_LASTLOCATION
		Rectangle2D b = getLayoutBounds(registry);
		double x, y;
		double w = b.getWidth();
		double h = b.getHeight();
		Iterator nodeIter = registry.getNodeItems();
		while ( nodeIter.hasNext() ) {
			VisualItem item = (VisualItem) nodeIter.next();
			Vertex v = _ctl.getModel().findVertex(((PNode) item.getEntity()).v().id());
			if(null != v && v.getDatum(RenderBox.DATUM_LASTLOCATION) != null) {
				Datum oldloc = v.getDatum(RenderBox.DATUM_LASTLOCATION);
				java.awt.geom.Point2D oldp = (java.awt.geom.Point2D) oldloc.get();
				setLocation(item, null, oldp.getX(), oldp.getY());
			} else {
				x = b.getX() + Math.random() * w;
				y = b.getY() + Math.random() * h;
				setLocation(item, null, x, y);
			}
		}
	} //
} // end of class RandomLayout
