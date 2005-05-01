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

import edu.berkeley.guir.prefuse.event.ControlListener;


// TODO - setup ControlSchemes so Renderbox can blindly switch to a scheme
// without handling the internal details.
// For now the Control Manager is essentially a HashMap mapping names
// to ControlListener objects.  Keeping a store of them allows them
// to be easily added and removed to support different functionality.

/**
 * Does the following:
 * - Stores a ControlListener Repository
 * 
 * - Future: store ControlSchemes
 *  
 * @author sam
 */
public class ControlManager {
    private HashMap _controls;
//    private HashMap _schemes;
//    private Display _display;
//    private String _activeScheme;
//    private boolean _isSchemeSet;
    
    
    /**
     * Constructor
     */
    public ControlManager() { // (Display d) {
        _controls = new HashMap();
//        _schemes = new HashMap();
//        _display = d;
//        _isSchemeSet = false;
    }
    
    
    /**
     * Add Control
     */
    public void addControl(String name, ControlListener c) {
        _controls.put(name, c);
    }

    
    /**
     * Remove Control
     */
    public void removeControl(String name) {
        _controls.remove(name);
    }
    
    /**
     * Get Control
     *
     */
    public ControlListener getControl(String name) {
        return (ControlListener)_controls.get(name);
    }
    
    /*
    public void addScheme(String name, ControlScheme cs) {
        
    }
    */
    
    /**
     * Clear
     */
    public void clear() {
//        _schemes.clear();
        _controls.clear();
//        _display = null;
    }
    
    
    /*
     * Set Scheme
     *
    public void setScheme(String name) {
        System.out.println("Setting Scheme to: <" + name + ">");
        _activeScheme = name;
        _isSchemeSet = true;
    }

    public String getSchemeName() {
        return _activeScheme;
    }
    */
    
}
