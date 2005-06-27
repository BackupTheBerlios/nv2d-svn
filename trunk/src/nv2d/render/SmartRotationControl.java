package nv2d.render;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.geom.Point2D;

import java.util.Iterator;

import javax.swing.SwingUtilities;

import nv2d.render.PElement;
import nv2d.render.RenderBox;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.util.display.DisplayLib;


/**
 * Rotates each visible node about the visible graph's centroid.
 *
 * Adapted from Jeffrey Heer's RotationControl in Prefuse.
 */
public class SmartRotationControl extends ControlAdapter {

    private Point2D lastPoint;
    private Point2D currentPoint;
    private Point2D centroid;
    private boolean repaint = true;
    
    // TODO: optionally add Renderbox or Plugin reference to notify when mouse is released
    // can be used to automatically switch out of rotate mode
    // private nv2d.render.RenderBox rb;
    
    /**
     * Creates a new rotation control that issues repaint requests as the mouse is
     * is dragged.
     */
    public SmartRotationControl() {
        this(true);
    } //
    
    /**
     * Creates a new rotation control that optionally issues repaint requests
     * as an item is dragged.
     * @param repaint indicates whether or not repaint requests are issued
     * as rotation events occur. This can be set to false if other activities
     * (for example, a continuously running force simulation) are already
     * issuing repaint events.
     */
    public SmartRotationControl(boolean repaint) {
        this.repaint = repaint;
    } //
    
    public void mousePressed(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            display.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            lastPoint = display.getAbsoluteCoordinate(new Point((int)e.getX(), (int)e.getY()), new Point());
        }
    } //
    
    public void mouseDragged(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            ItemRegistry registry = display.getRegistry();
            Iterator nodes = registry.getFilteredGraph().getNodes();
            
            centroid = DisplayLib.getCentroid(registry, nodes, new Point());
            double xCentroid = centroid.getX();
            double yCentroid = centroid.getY();
            
            double scale = display.getScale();

            // Get current mouse point in ABSOLUTE coordinates, not Display Window coords
            currentPoint = display.getAbsoluteCoordinate(new Point((int)e.getX(), (int)e.getY()), new Point());
            
            double xDrag = currentPoint.getX();
            double yDrag = currentPoint.getY();
            double xLastDrag = lastPoint.getX();
            double yLastDrag = lastPoint.getY();
            double dxDrag = xDrag-xLastDrag;
            double dyDrag = yDrag-yLastDrag;

            // TODO: clean this up, put in separate fxn or something
            // -1 = Rotate Left (RL), 1 = Rotate Right (RR)
            // (with respect to the top of the graph)
            int direction = 1;
            
            // Determine which direction to spin based on drag vector
            // if mag(x) is greater
            if(Math.pow(dxDrag, 2.0) >= Math.pow(dyDrag, 2.0)) {
                //System.out.print("x -> ");
                // ABOVE if the vector is ABOVE the centroid
                if((double)(yDrag + yLastDrag)/2 >= yCentroid) {
                    //System.out.print("below -> ");
                    // LEFT if vector is moving to left
                    if(dxDrag <= 0) {
                        direction = -1;
                    }
                    // RIGHT
                    else {
                        direction = 1;
                    }
                }
                // BELOW
                else {
                    //System.out.print("above -> ");
                    // LEFT if vector is moving to left
                    if(dxDrag <= 0) {
                        //System.out.print("left");
                        direction = 1;
                    }
                    // RIGHT
                    else {
                        //System.out.print("right");
                        direction = -1;
                    }
                }
            }
            else {
                //System.out.print("y -> ");
                // RIGHT of centroid
                if((xDrag + xLastDrag)/2 >= xCentroid) {
                    //System.out.print("right -> ");
                    // UP vector
                    if(dyDrag <= 0) {
                        //System.out.print("up");
                        direction = 1;
                    }
                    // DOWN
                    else {
                        //System.out.print("down");
                        direction = -1;
                    }
                }
                // LEFT
                else {
                    //System.out.print("left -> ");
                    // UP vector
                    if(dyDrag <= 0) {
                        //System.out.print("up");
                        direction = -1;
                    }
                    // DOWN
                    else {
                        //System.out.print("down");
                        direction = 1;
                    }
                }
            }
            
            double angle = direction * (Math.sqrt(Math.pow(dxDrag, 2.0) + Math.pow(dyDrag, 2.0)))/40;

            
            // Set new (X2, Y2) by the following:
            // (1) r = sqrt(x^2 + y^2)
            // (2) theta = arctan(x/y)
            // (3) y2 = r * sin(theta + angle)
            // (4) x2 = r * cos(theta + angle)
           
            double x, y, r, theta, y2, x2, quo;
            nodes = registry.getFilteredGraph().getNodes();
            while(nodes.hasNext()) {
                NodeItem n = (NodeItem) nodes.next();
                x = n.getX() - xCentroid;
                y = -1 * (n.getY() - yCentroid);
                
                r = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0));
                quo = y/x;
                theta = Math.atan(quo);
                if(x < 0) {
                    theta = Math.PI + theta;
                }
                x2 = r * Math.cos(theta + angle);
                y2 = -1 * (r * Math.sin(theta + angle));

                n.setLocation(x2+xCentroid, y2+yCentroid);
                n.updateLocation(x2+xCentroid, y2+yCentroid);
            }
            
            lastPoint = currentPoint;
            if ( repaint )
                display.repaint();
        }
    } //
    
    public void mouseReleased(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
            
            // TODO - put a notifier here that notifies the layout plugin
            // that rotate has finished
        }
    } //
    
} // end of class SmartZoomControl
