package nv2d.render;

import java.awt.geom.Point2D;
import javax.swing.event.EventListenerList;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;


// TODO - this can all be moved into the RenderBox.
// I did it this way for now so that it is easily removable and kind of
// abstracted away until we decide how we want to use it.


/**
 * ActiveDisplay, adds DisplayTransformEvent handling to Display.
 * 
 * TODO - could put Rotation into this class, along with any other
 * core RenderBox methods.
 */
public class ActiveDisplay extends Display {
    protected DisplayTransformListener t_listener;
    protected EventListenerList t_listenerList = new EventListenerList();

    public ActiveDisplay() {
        this(null);
    }

    /**
     * Creates a new ActiveDisplay instance associated with the ItemRegistry.
     * 
     * @param registry - the ItemRegistry from which this Display should get 
     * the items to visualize.
     */
    public ActiveDisplay(ItemRegistry registry) {
        super(registry);
    }

    
    // --- TRANSFORM METHODS -------------------------------------------------
    // overriding super methods to fire a DisplayTransformEvent when these
    // transforms are performed.

    /**
     * Pans the view provided by this display in absolute (i.e. non-screen)
     * coordinates.
     * 
     * @param dx - the amount to pan along the x-dimension, in absolute co-ords
     * @param dy - the amount to pan along the y-dimension, in absolute co-ords
     */
    public void panAbs(double dx, double dy) {
//        System.out.println("PanAbs - firing event");
        super.panAbs(dx, dy);
        DisplayTransformEvent event = new DisplayTransformEvent(this,
                DisplayTransformEvent.PAN_TRANSFORM_EVENT);
        fireDisplayTransformEvent(event);
    } //

    /**
     * Pans the display view to center on the provided point in absolute (i.e.
     * non-screen) coordinates.
     * 
     * @param x - the x-point to center on, in absolute co-ords
     * @param y - the y-point to center on, in absolute co-ords
     */
    public void panToAbs(Point2D p) {
//        System.out.println("PanToAbs - firing event");
        super.panToAbs(p);
        DisplayTransformEvent event = new DisplayTransformEvent(this,
                DisplayTransformEvent.PAN_TRANSFORM_EVENT);
        fireDisplayTransformEvent(event);
    } //

    /**
     * Zooms the view provided by this display by the given scale, anchoring the
     * zoom at the specified point in absolute coordinates.
     * 
     * @param p - the anchor point for the zoom, in absolute (i.e. non-screen)
     *            co-ordinates
     * @param scale - the amount to zoom by
     */
    public void zoomAbs(final Point2D p, double scale) {
//        System.out.println("ZoomAbs - firing event");
        super.zoomAbs(p, scale);
        DisplayTransformEvent event = new DisplayTransformEvent(this,
                DisplayTransformEvent.ZOOM_TRANSFORM_EVENT);
        fireDisplayTransformEvent(event);
    } //


    public void animatePanAbs(double dx, double dy, long duration) {
//        System.out.println("AnimatePanAbs - firing event");
        super.animatePanAbs(dx, dy, duration);
        DisplayTransformEvent event = new DisplayTransformEvent(this,
                DisplayTransformEvent.PAN_TRANSFORM_EVENT);
        fireDisplayTransformEvent(event);
    } //

    public void animateZoomAbs(final Point2D p, double scale, long duration) {
//        System.out.println("AnimateZoomAbs - firing event");
        super.animateZoomAbs(p, scale, duration);
        DisplayTransformEvent event = new DisplayTransformEvent(this,
                DisplayTransformEvent.ZOOM_TRANSFORM_EVENT);
        fireDisplayTransformEvent(event);
    } //

    public void animatePanAndZoomToAbs(final Point2D p, double scale,
            long duration) {
//        System.out.println("AnimatePanZoomToAbs - firing event");
        super.animatePanAndZoomToAbs(p, scale, duration);
        DisplayTransformEvent event = new DisplayTransformEvent(this,
                DisplayTransformEvent.PAN_ZOOM_TRANSFORM_EVENT);
        fireDisplayTransformEvent(event);
    } //

    // TODO - could extend DisplayTransformed to include resize events
    //    
    //	public void setSize(int width, int height) {
    //	public void setSize(Dimension d) {
    //  public void reshape(int x, int y, int w, int h) {
    // 
    //  -- or override the animation inner class: --
    //  private class TransformActivity extends Display.TransformActivity {}

    
    // --- DISPLAY TRANSFORM LISTENER METHODS ------------------------------
    // Allows an ActiveDisplay to have listeners for DisplayTransformEvents.

    public void addDisplayTransformListener(DisplayTransformListener dtl) {
        System.out.println("Adding DisplayTransform Listener");
        t_listenerList.add(DisplayTransformListener.class, dtl);
    }

    public void removeDisplayTransformListener(DisplayTransformListener dtl) {
        System.out.println("Removing DisplayTransform Listener");
        t_listenerList.remove(DisplayTransformListener.class, dtl);
    }

    public void fireDisplayTransformEvent(DisplayTransformEvent e) {
//        System.out.println("Firing Event: " + e.getEventType());
        Object[] listeners = t_listenerList.getListenerList();

        // each listener occupies two list elts:
        // (1) listener class
        // (2) listener instance

        // iterate over all listeners
        for (int i = 0; i < listeners.length; i += 2) {
            // if the listener is a DisplayTranform (which it is)
            // invoke the method on the listener
            if (listeners[i] == DisplayTransformListener.class) {
                ((DisplayTransformListener) listeners[i + 1])
                        .displayTransformed(e);
            }
        }
    }

} // end of class ActiveDisplay
