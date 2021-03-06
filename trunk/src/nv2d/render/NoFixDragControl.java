package nv2d.render;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Adapted from PrefuseX DragControl.
 * 
 * DOES NOT FIX NODE POSITIONS, as this seems to have erratic effects
 * leaving some nodes fixed permanently.
 * 
 */
public class NoFixDragControl extends ControlAdapter {

    private VisualItem activeItem;
    protected Activity update;
    protected Point2D down = new Point2D.Double();
    protected Point2D tmp = new Point2D.Double();
    protected boolean dragged;
//    private boolean fixOnMouseOver;
    protected boolean repaint = true;
    
    /**
     * Creates a new drag control that issues repaint requests as an item
     * is dragged.
     */
    public NoFixDragControl() {
    } //
    
    /**
     * Creates a new drag control that optionally issues repaint requests
     * as an item is dragged.
     * @param repaint indicates whether or not repaint requests are issued
     *  as drag events occur. This can be set to false if other activities
     *  (for example, a continuously running force simulation) are already
     *  issuing repaint events.
     */
    public NoFixDragControl(boolean repaint) {
        this.repaint = repaint;
    } //
    
//    /**
//     * Creates a new drag control that optionally issues repaint requests
//     * as an item is dragged.
//     * @param repaint indicates whether or not repaint requests are issued
//     *  as drag events occur. This can be set to false if other activities
//     *  (for example, a continuously running force simulation) are already
//     *  issuing repaint events.
//     * @param fixOnMouseOver indicates if object positions should become
//     * fixed (made stationary) when the mouse pointer is over an item.
//     */
//    public DragControl(boolean repaint, boolean fixOnMouseOver) {
//        this.repaint = repaint;
//        this.fixOnMouseOver = fixOnMouseOver;
//    } //
    
    
    public NoFixDragControl(Activity update) {
        this.repaint = false;
        this.update = update;
    } //
    
//    /**
//     * Determines whether or not an item should have it's position fixed
//     * when the mouse moves over it.
//     * @param s whether or not item position should become fixed upon
//     *  mouse over.
//     */
//    public void setFixPositionOnMouseOver(boolean s) {
//        fixOnMouseOver = s;
//    } //
    
    public void itemEntered(VisualItem item, MouseEvent e) {
        if (!(item instanceof NodeItem)) return;
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        activeItem = item;
//        if ( fixOnMouseOver )
//            item.setFixed(true);
    } //
    
    public void itemExited(VisualItem item, MouseEvent e) {
        if (!(item instanceof NodeItem)) return;
        if ( activeItem == item ) {
            activeItem = null;
//            item.setFixed(item.wasFixed());
        }
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getDefaultCursor());
    } //
    
    public void itemPressed(VisualItem item, MouseEvent e) {
        if (!(item instanceof NodeItem)) return;
        if (!SwingUtilities.isLeftMouseButton(e)) return;
//        if ( !fixOnMouseOver )
//            item.setFixed(true);
        dragged = false;
        Display d = (Display)e.getComponent();
        down = d.getAbsoluteCoordinate(e.getPoint(), down);
    } //
    
    public void itemReleased(VisualItem item, MouseEvent e) {
        if (!(item instanceof NodeItem)) return;
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        if ( dragged ) {
            activeItem = null;
//            item.setFixed(item.wasFixed());
            dragged = false;
        }
    } //
    
    public void itemDragged(VisualItem item, MouseEvent e) {
        if (!(item instanceof NodeItem)) return;
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        dragged = true;
        Display d = (Display)e.getComponent();
        tmp = d.getAbsoluteCoordinate(e.getPoint(), tmp);
        double dx = tmp.getX()-down.getX();
        double dy = tmp.getY()-down.getY();
        Point2D p = item.getLocation();
        item.updateLocation(p.getX()+dx,p.getY()+dy);
        item.setLocation(p.getX()+dx,p.getY()+dy);
        down.setLocation(tmp);
        if ( repaint )
            item.getItemRegistry().repaint();
        if ( update != null )
            update.runNow();
    } //
    
} // end of class DragControl
