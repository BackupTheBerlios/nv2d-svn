package nv2d.render;

import java.awt.Color;
import java.awt.Paint;

// import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;


public class Colorizer extends ColorFunction {
	// line colors
	public static final Color selected = Color.BLUE;
	public static final Color mouseover = Color.GREEN;
	public static final Color normal = Color.BLACK;

	// fill colors
	public static final Color fill_selected = Color.BLUE;
	public static final Color fill_mouseover = Color.DARK_GRAY;
	public static final Color fill_normal = Color.LIGHT_GRAY;

	public Paint getColor(VisualItem i) {
		// fixed should always have precedence
		if(i.isFixed()) {
			return selected;
		} else if (i.isHighlighted()) {
			return mouseover;
		} else {
			return normal;
		}
	}

	public Paint getFillColor(VisualItem i) {
		if(i.isFixed()) {
			return fill_selected;
		} else if (i.isHighlighted()) {
			return fill_mouseover;
		} else {
			return fill_normal;
		}
	}
}
