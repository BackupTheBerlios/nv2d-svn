package nv2d.render;

import java.util.EventListener;

/**
 * Listener interface for handling transforms to
 * the display in prefuse.
 */
public interface DisplayTransformListener extends EventListener
{
    /**
     * Display Transformed
     */
    public void displayTransformed(DisplayTransformEvent e);

    
    // TODO - Bo, you may want to change this to have different
    // methods for each event.  I just used one generic displayTransformed
    // method when I was testing it.
}
