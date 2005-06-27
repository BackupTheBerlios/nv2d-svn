package nv2d.plugins.standard.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.action.assignment.Layout;

/**
 * LayoutActionList
 *
 * Wrapper for ActionList which keeps a reference to the Layout Object placed in this
 * LayoutActionList via setLayout.  
 *  
 * @author sam
 */
public class LayoutActionList extends ActionList {
    Layout _layout;

    
    /**
     * Creates a new run-once ActionList that operates on the 
     * given ItemRegistry.
     */
    public LayoutActionList(ItemRegistry registry) {
        this(registry, 0);
    }
    
    
    /**
     * Creates a new ActionList of specified duration and default
     * step time of 20 milliseconds.
     */
    public LayoutActionList(ItemRegistry registry, long duration) {
        super(registry, duration, Activity.DEFAULT_STEP_TIME);
    }
    
    
    /**
     * Creates a new ActionList of specified duration and step time.
     */
    public LayoutActionList(ItemRegistry registry, long duration, long stepTime) {
        this(registry, duration, stepTime, System.currentTimeMillis());
    }
    
    
    /**
     * Creates a new ActionList of specified duration, step time, and
     * staring time.
     */
    public LayoutActionList(ItemRegistry registry, long duration, 
            long stepTime, long startTime) {
        super(registry, duration, stepTime, startTime);
    }
    
    
    /**
     * Adds this Layout to the ActionList and sets it as the
     * persistent layout.
     */
    public void setLayout(Layout l) {
        this.add(l);
        _layout = l;
    }

    /**
     * Gets the Layout set for this ActionList.
     */
    public Layout getLayout() {
        return _layout;
    }
    
}