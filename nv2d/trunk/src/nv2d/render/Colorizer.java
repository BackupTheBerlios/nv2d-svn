package nv2d.render;

import java.awt.Color;
import java.awt.Paint;

// import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;


public class Colorizer extends ColorFunction {
	// line colors
	public static final Color selected = Color.BLACK;
	public static final Color mouseover = Color.GREEN;
	public static final Color normal = Color.BLACK;
	public static final Color apsp = Color.RED;

	// fill colors
	public static final Color fill_selected = Color.BLACK;
	public static final Color fill_mouseover = Color.DARK_GRAY;
	public static final Color fill_normal = Color.LIGHT_GRAY;
	public static final Color fill_apsp = Color.YELLOW; // all pairs shortest paths start and end vertices
	public static final Color fill_apspSource = new Color(0, 100, 0); // all pairs shortest paths start and end vertices
	public static final Color fill_apspEnd = Color.RED; // all pairs shortest paths start and end vertices

	public Paint getColor(VisualItem i) {
		if(i.getEntity() instanceof PEdge) {
			PEdge p = (PEdge) i.getEntity();
			if(p.isPathElement()) {
				return apsp;
			}
		}
		if(i.getEntity() instanceof PNode) {
			PNode p = (PNode) i.getEntity();
			if(p.isPathElement()) {
				return apsp;
			}
		}
		return fill_normal;
	}

	public Paint getFillColor(VisualItem i) {
		if(!(i.getEntity() instanceof PNode)) {
			return fill_normal;
		}

		PNode p = (PNode) i.getEntity();
		if(p.isStartPoint()) {
			return fill_apspSource;
		} else if (p.isEndPoint()) {
			return fill_apspEnd;
		} else if (p.isPathElement()) {
			return fill_apsp;
		}
		return fill_normal;
		/*
		if(i.isFixed()) {
			return fill_selected;
		} else if (i.isHighlighted()) {
			return fill_mouseover;
		} else {
			return fill_normal;
		}
		*/
	}
}
