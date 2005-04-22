/*
 * LegendColorizer.java
 *
 * Created on March 18, 2005, 4:15 PM
 */

package nv2d.render;

import java.awt.*;

import nv2d.ui.NController;
import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.Datum;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;

/**
 *
 * @author bshi
 */
public class LegendColorizer extends ColorFunction {
	public static final String DATUM_LEGENDCOLOR = "__colorlegend:vertexcolor";

	private Graph _model;
	
	public LegendColorizer(NController ctl) {
		_model = ctl.getModel();
	}
	
	public Paint getFillColor(VisualItem i) {
		if(i.getEntity() instanceof PNode) {
			PNode p = (PNode) i.getEntity();
			Vertex v = _model.findVertex(p.v().id());
			Datum d = null;
			
			if(v != null) {
				d = v.getDatum(DATUM_LEGENDCOLOR);
			}
			// Datum d = p.v().getDatum(DATUM_LEGENDCOLOR);
			if(d != null) {
				return (Color) d.get();
			}
		}
		return Colorizer.fill_normal;
	}
}
