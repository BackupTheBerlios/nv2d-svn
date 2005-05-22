/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * $Id$
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package nv2d.render;

import java.util.HashMap;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.event.ControlListener;
import edu.berkeley.guir.prefuse.Display;


/**
 * Control Manager
 * 
 * Manages the currently running ControlListeners for a Display.
 *  
 * @author sam
 */
public class ControlManager {
    private Display _display;
    private HashMap _controls;
    
    
    /**
     * Constructor
     */
    public ControlManager(Display d) {
        _controls = new HashMap();
        _display = d;
    }
    
    
    /**
     * Add Control
     * 
     * Adds a ControlListener with a given name, ensuring that no other 
     * ControlListener with that name is running. 
     */
    public void addControl(String name, ControlListener c) {
        // if there is a control by this name, remove it
        if(_controls.containsKey(name)) {
            _display.removeControlListener((ControlListener)_controls.get(name));
            _controls.remove(name);
        }
        
        // add new control
        _controls.put(name, c);
        _display.addControlListener(c);
    }

    
    /**
     * Remove Control
     * 
     * Removing a control that does not exist produces no result.
     */
    public void removeControl(String name) {
        _display.removeControlListener((ControlListener)_controls.get(name));
        _controls.remove(name);
    }
    
    
    /**
     * Remove All Controls
     */
    public void removeAllControls() {
        Iterator keys = _controls.keySet().iterator();
        while(keys.hasNext()) {
            String keyName = (String)keys.next();
            _display.removeControlListener((ControlListener)_controls.get(keyName));
            _controls.remove(keyName);            
        }
    }
    
    /**
     * Get Control
     *
    public ControlListener getControl(String name) {
        return (ControlListener)_controls.get(name);
    }
    */
    
    
    /**
     * Clear
     */
    public void clear() {
        _controls.clear();
        _display = null;
    }
    
    
    /**
     * Prints the current controls that are running
     *
     */
    public void printControls() {
        Iterator keys = _controls.keySet().iterator();
        System.out.println("Current Controls: ");
        while(keys.hasNext()) {
            System.out.println(" - " + (String)(keys.next()));
        }
        
    }
}
