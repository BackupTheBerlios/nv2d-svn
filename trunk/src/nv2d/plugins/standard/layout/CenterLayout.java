package nv2d.plugins.standard.layout;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.VisualItem;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;

import nv2d.graph.Datum;
import nv2d.render.PElement;
import nv2d.render.PNode;
import nv2d.render.RenderSettings;
import nv2d.plugins.NV2DPlugin;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;

import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.util.display.DisplayLib;
import java.awt.geom.Point2D;


public class CenterLayout extends Layout {
    
    private boolean _animate;
    
    /**
     */
    public CenterLayout(boolean animate) {
        _animate = animate;
    }


    /**
     * RUN
     */
    public void run(ItemRegistry registry, double frac) {
        System.out.println("Running CenterLayout");
        Display display = registry.getDisplay(0);
        Iterator nodes = registry.getFilteredGraph().getNodes();

        // -- get display size and center --
        Rectangle2D r = super.getLayoutBounds(registry);
        double height = r.getHeight();
        double width = r.getWidth();
        double cx = r.getCenterX();
        double cy = r.getCenterY();
        System.out.println("Display Coords: h:" + height + " w:" + width + " cx:" + cx + " cy:" + cy);
        
        // -- get centroid of the graph --
        Point2D centroid = DisplayLib.getCentroid(registry, nodes, new Point());
        double xCentroid = centroid.getX();
        double yCentroid = centroid.getY();
        System.out.println("Centroid:" + xCentroid + ", " + yCentroid);
        
        // -- shift every point in the graph by the difference
        // -- between the centroid and the display center --
        
        // 2 methods
        // (a) panAbs
        // (b) move every points abs coords
        
        double panx = xCentroid;
        double pany = yCentroid;
        if(_animate) {
            display.animatePanToAbs(new Point((int)panx, (int)pany), 500);
        }
        else {
            display.panToAbs(new Point((int)panx, (int)pany));
        }
    } // run

    
    
//	public void doCenterLayout() {
//		if(_empty) {
//			return;
//		}
//		
//		int ct = 0;
//		double x = 0, y = 0;
//		Iterator nodeIter = _registry.getNodeItems();
//		while ( nodeIter.hasNext() ) {
//			VisualItem item = (VisualItem) nodeIter.next();
//			x += item.getLocation().getX();
//			y += item.getLocation().getY();
//			ct++;
//		}
//		x = x / (double) ct;
//		y = y / (double) ct;
//		panToAbs(new java.awt.geom.Point2D.Double(x, y));
//		repaint();
//	}

}