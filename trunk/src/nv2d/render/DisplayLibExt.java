package nv2d.render;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Node;

import edu.berkeley.guir.prefuse.util.display.DisplayLib;

/**
 * DisplayLibExt
 *  
 * Extensions to DisplayLib, allowing fitViewToBounds to animate or not.
 */
public class DisplayLibExt {

    public DisplayLibExt() {
        // don't instantiate
    } //
    
    public static void fitViewToBounds(Display display, Rectangle2D bounds, boolean animate)
    {
        fitViewToBounds(display, bounds, null, animate);
    }
    
    public static void fitViewToBounds(
        Display display, Rectangle2D bounds, Point2D center, boolean animate)
    {
        // init variables
        double w = display.getWidth(), h = display.getHeight();
        double cx = (center==null? bounds.getCenterX() : center.getX());
        double cy = (center==null? bounds.getCenterY() : center.getY());
        
        // compute half-widths of final bounding box around
        // the desired center point
        double wb = Math.max(cx-bounds.getMinX(),
                			 bounds.getMaxX()-cx);
        double hb = Math.max(cy-bounds.getMinY(),
   			 				 bounds.getMaxY()-cy);
        
        // compute scale factor
        //  - figure out if z or y dimension takes priority
        //  - then balance against the current scale factor
        double scale = Math.min(w/(2*wb),h/(2*hb)) / display.getScale();

        // animate to new display settings
        if ( center == null )
            center = new Point2D.Double(cx,cy);
        
        if(animate) {
            display.animatePanAndZoomToAbs(center,scale,2000);
        }
        else {
            // TODO - For some reason the animatePan...
            // works better when zoomed out????
//            display.zoomAbs(center,scale);
//            // TODO
//            // for some reason panning twice yields better results
//            // making a second, more fine grained shift
//            display.panToAbs(center);
//            display.panToAbs(center);
//            display.repaint();

            display.animatePanAndZoomToAbs(center,scale,20);
        }
    } //
    
} // end of class DisplayLibExt
