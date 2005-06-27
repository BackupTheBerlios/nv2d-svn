package nv2d.render;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefusex.controls.PanControl;

/**
 * Extends PanControl to pause an active layout while panning, and to
 * notify the RenderBox when a pan is complete.
 *
 * Extends Jeffrey Heer's PanControl in PrefuseX
 */
public class ActivePanControl extends PanControl {

    private RenderBox _renderbox;
    private boolean _layoutPaused = false;
    
    /**
     */
    public ActivePanControl(RenderBox r) {
        this(r, true);
    } //
    
    /**
     * Constructs an ActivePanControl which pauses an active layout
     * in the RenderBox, and notifies the RenderBox when pan is complete.
     */
    public ActivePanControl(RenderBox r, boolean repaint) {
        super(repaint);
        _renderbox = r;
    } //

    /**
     * OVERRIDES PanControl.mousePressed
     */
    public void mousePressed(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
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
     * OVERRIDES PanControl.mouseReleased
     */
    public void mouseReleased(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
	        super.mouseReleased(e);
	        
	        // restart a paused layout
	        if(_layoutPaused) {
	            _renderbox.startLayout();
	            _layoutPaused = false;
	        }
	        
	        // notify renderbox of pan event completion
	        _renderbox.panPerformed();
        }
    } //
    
}
