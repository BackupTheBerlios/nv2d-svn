/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
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
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefuse.activity.ActivityMap;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.action.ActionSwitch;
import edu.berkeley.guir.prefuse.action.Action;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * Activity Director does the following:
 * - Contains an Activity Repository, mapped a String name to an Activity Object
 * - Runs a selected set of Background Activities in different threads
 * - Runs at most 1 active foreground activity (be careful with background activities!) 
 *  
 * @author sam
 */

// TODO:
// - add Action Repository (put in map & list)
// - add Force Repository

public class ActivityDirector {
    private ActivityMap _activities;
    private String _activeName;
    boolean _isActiveSet;
    private ArrayList _backgroundList;
    
    
    /**
     * Constructor
     */
    public ActivityDirector() {
        _activities = new ActivityMap();
        _isActiveSet = false;
        _backgroundList = new ArrayList();
    }

    
    /**
     * Run
     */
    public void run() {
        if(_isActiveSet) {
            // System.out.println("Director Running: " + _activeName);
            _activities.get(_activeName).run();
        }
    }

    
    /**
     * RunNow
     */
    public void runNow() {
        if(_isActiveSet) {
//            System.out.println("Director Running: " + _activeName);
            _activities.get(_activeName).runNow();
        }
    }
    
    
    /**
     * Stop
     */
    public void stop() {
        if(_isActiveSet) {
            _activities.get(_activeName).cancel();
        }
    }
    
    
    /**
     * isRunning?
     */
    public boolean isRunning() {
        if(_isActiveSet) {
            return _activities.get(_activeName).isRunning();
        }
        else {
            return false;
        }
    }
    
    
    /**
     * Add
     */
    // TODO: handle case where names clash?
    public void add(String name, Activity a) {
        _activities.put(name, a);
//        printActivities();
    }

    
    /**
     * Remove
     */
    public void remove(String name) {
        _activities.remove(name);
    }
    
    
    /**
     * Clear
     */
    public void clear() {
        _activities.clear();
        _activeName = null;
        _isActiveSet = false;
    }
    
    
    /**
     * Set Active
     * 
     * Sets the Activity with this name as the active, running
     * Activity.
     */
    public void setActive(String name) {
        // System.out.println("Setting <" + name + "> Active");
        if(_isActiveSet) {
            // if activity is running, stop it first
            if(isRunning()) {
                stop();
            }
        }

        _activeName = name;
        _isActiveSet = true;
    }

    
    /**
     * Get Active
     * 
     * Returns the name of the "Active" Activity.
     */
    public String getActive() {
        return _activeName;
    }
    
    
    /**
     * Get Activity
     * 
     * Returns the Activity Object with the given name.
     */
    public Activity getActivity(String name) {
        return _activities.get(name);
    }

    
    /**
     * Print Activities
     */
    public void printActivities() {
        Object[] ss = _activities.keys();
        System.out.println("Activities:" + ss.length);
        for(int i=0; i<ss.length; i++) {
            System.out.println("  - " + (String)ss[i]);
        }
    }
    
    // ---- Background Methods ----
    
    /**
     * Set Background Activity
     * 
     * Sets the Activity with the given name as a Background
     * Activity, which runs in a separate background thread,
     * independent of the "Active" Activity.  There may be
     * multiple Background Activities.
     */
    public void setBackground(String name) {
        // System.out.println("Setting Background Activity: <" + name + ">");
        Activity a = _activities.get(name);
        if(!_backgroundList.contains(a)) {
            if(isBackgroundRunning()) {
                stopBackground();
            }
            _backgroundList.add(a);
        }
    }

    /**
     * isBackgroundRunning?
     * 
     * Returns true is any Activity that was set as a Background
     * Activity is currently running.
     */
    // TODO - could be implemented w/ global variable instead
    public boolean isBackgroundRunning() {
        Iterator i = _backgroundList.iterator();
        while(i.hasNext()) {
            Activity a = (Activity)i.next();
            if(a.isRunning()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * StartBackground
     */
    public void startBackground() {
        Iterator i = _backgroundList.iterator();
        while(i.hasNext()) {
            Activity a = (Activity)i.next();
            a.runNow();
        }
    }
    
    /**
     * StopBackground
     */
    public void stopBackground() {
        Iterator i = _backgroundList.iterator();
        while(i.hasNext()) {
            Activity a = (Activity)i.next();
            a.cancel();
        }
    }
    
}
