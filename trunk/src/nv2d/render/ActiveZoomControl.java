package nv2d.render;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefusex.controls.ZoomControl;

/**
 * Extends ZoomControl to pause active layout during zooming
 * and to notify RenderBox upon zoom completion.
 * 
 * Extends Jeffrey Heer's ZoomControl in PrefuseX
 */
public class ActiveZoomControl extends ZoomControl {
    
    private RenderBox _renderbox;
    private boolean _layoutPaused = false;
    
    /**
     */
    public ActiveZoomControl(RenderBox r) {
        this(r, true);
    } //
    
    /**
     */
    public ActiveZoomControl(RenderBox r, boolean repaint) {
        super(repaint);
        _renderbox = r;
    } //
    
    /**
     * OVERRIDES ZoomControl.mousePressed
     */
    public void mousePressed(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
	        // pause the active renderbox layout
	        if(_renderbox.isRunningActivity()) {
	            _renderbox.stopLayout();
	            _layoutPaused = true;
	        }
	        
	        // perform super method
	        super.mousePressed(e);
        }
    } //

    /**
     * OVERRIDES ZoomControl.mouseReleased
     */
    public void mouseReleased(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
	        super.mouseReleased(e);
	        
	        // restart a paused layout
	        if(_layoutPaused) {
	            _renderbox.startLayout();
	            _layoutPaused = false;
	        }
	        
	        // notify renderbox of pan event completion
	        _renderbox.zoomPerformed();
        }
    } //

}
