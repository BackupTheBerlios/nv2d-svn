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
    private boolean _isActiveSet;
    private boolean _isBackgroundRunning;
    private ArrayList _backgroundList;
    private ArrayList _runWithLayoutList;
    
    
    /**
     * Constructor
     */
    public ActivityDirector() {
        _activities = new ActivityMap();
        _isActiveSet = false;
        _backgroundList = new ArrayList();
        _isBackgroundRunning = false;
        _runWithLayoutList = new ArrayList();
    }

    
    /**
     * Clear
     */
    public void clear() {
        stop();
        stopBackground();
        
        _activities.clear();
        _activeName = null;
        _isActiveSet = false;
        
        _backgroundList.clear();
        _isBackgroundRunning = false;
        
        _runWithLayoutList.clear();
    }
    
    
    /**
     * Run
     */
    public void run() {
        if(_isActiveSet) {
//            System.out.println("Director Running: " + _activeName);
            _activities.get(_activeName).run();
            
            // run activities with layout
            if(!_runWithLayoutList.isEmpty()) {
                Iterator i = _runWithLayoutList.iterator();
                while(i.hasNext()) {
                    Activity a = (Activity)i.next();
                    a.run();
                }
            }
        }
    }

    
    /**
     * RunNow
     */
    public void runNow() {
        if(_isActiveSet) {
//            System.out.println("Director Running: " + _activeName);
            _activities.get(_activeName).runNow();
            
            // run activities with layout
            if(!_runWithLayoutList.isEmpty()) {
                Iterator i = _runWithLayoutList.iterator();
                while(i.hasNext()) {
                    Activity a = (Activity)i.next();
                    a.runNow();
                }
            }
        }
    }

    /**
     * RUN an Action
     * 
     * Stops the current "Active" activity and runs the specified
     * Action.
     */
    public void runNow(Action a, ItemRegistry reg, double frac) {
        if(this.isRunning()) {
            stop();
        }
        _isActiveSet = false;
        _activeName = null;
        a.run(reg, frac);
    }

    /**
     * RUN an Action
     * 
     * Runs the given Action in the background.
     */
    public void runNowInBackground(Action a, ItemRegistry reg, double frac) {
        a.run(reg, frac);
    }
    
    /**
     * Stop
     */
    public void stop() {
        if(_isActiveSet) {
            _activities.get(_activeName).cancel();
            
            // cancel activities that run with layout
            if(!_runWithLayoutList.isEmpty()) {
                Iterator i = _runWithLayoutList.iterator();
                while(i.hasNext()) {
                    Activity a = (Activity)i.next();
                    a.cancel();
                }
            }
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
     * 
     * If an activity with the same name exists, it will be replaced.
     * If that activity is running, it will be stopped.  If that
     * activity is a background activity, it will be removed from
     * the background list.
     */
    public void add(String name, Activity a) {
        // check if it already exists
        Activity check = _activities.get(name);
        if(check != null) {
            if(check.isRunning()) {
                check.cancel();
            }
            
            // if it is in the background list, remove the old one
            // and insert the new one
            if(_backgroundList.contains(check)) {
                _backgroundList.remove(check);
                _backgroundList.add(a);
            }
            
            if(_runWithLayoutList.contains(check)) {
                _runWithLayoutList.remove(check);
                _runWithLayoutList.add(a);
            }
        }
        
        // now write the new activity
        // note: will overwrite previous activity
        _activities.put(name, a);
    }

    
    /**
     * Remove
     * 
     * Removes an Activity with specified name from the repository.
     * If the Activity is running, it will be stopped.  If the Activity
     * is in the background, it will be removed.
     */
    public void remove(String name) {
        // get the activity that is to be removed
        Activity a = _activities.get(name);
        
        // if the Activiy is "Active", reset "Active" status
        if(name.equals(_activeName)) {
            _isActiveSet = false;
            _activeName = null;
        }

        // if the Activity is "Background", remove it from list
        removeFromBackground(name);
        
        // if the Activity is "runWithLayout", remove from list
        removeFromRunWithLayout(name);
        
        // if Activity is currently running, stop it
        if(a.isRunning()) {
            a.cancel();
        }
        
        _activities.remove(name);
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
        
        // TODO: could test if this activity is running in background
        // and warn the user!
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
     * isActiveSet
     * 
     * Returns the name of the "Active" Activity.
     */
    public boolean isActiveSet() {
        return _isActiveSet;
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
        //System.out.println("Activities:" + ss.length);
        for(int i=0; i<ss.length; i++) {
            //System.out.println("  - " + (String)ss[i]);
        }
    }
    
    
    // ---- Run With Layout Methods ----
    /**
     * Set Run With Layout Activity
     * 
     * Sets the Activity with the given name as a "Run With layout"
     * Activity, which runs in a separate background thread,
     * independent of the "Active" Activity.  There may be
     * multiple "Run With Layout" Activities.
     */
    public void setRunWithLayout(String name) {
        //System.out.println("Setting RunWithLayout Activity: <" + name + ">");
        Activity a = _activities.get(name);
        if(!_runWithLayoutList.contains(a)) {
            // TODO: is this threadsafe here?
            _runWithLayoutList.add(a);

            // restart if running
            if(isRunning()) {
                stop();
                runNow();
            }            
        }
    }

    public void addRunWithLayout(String name, Activity a) {
        add(name, a);
        setRunWithLayout(name);
    }
    
    /**
     * Remove Activity From Background
     * 
     * Removes an Activity from the Background, but leaves
     * it in the repository.  Use <remove> to completely remove
     * the Activity.  If this Activity is running, it will be stopped.
     */
    public boolean removeFromRunWithLayout(String name) {
        Activity a = _activities.get(name);
        if(a != null && _runWithLayoutList.contains(a)) {
            if(a.isRunning()) {
                a.cancel();
            }
            _runWithLayoutList.remove(a);
            
            return true;
        }
        else {
            return false;
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
     * Remove Activity From Background
     * 
     * Removes an Activity from the Background, but leaves
     * it in the repository.  Use <remove> to completely remove
     * the Activity.  If this Activity is running, it will be stopped.
     */
    public boolean removeFromBackground(String name) {
        Activity a = _activities.get(name);
        if(a != null && _backgroundList.contains(a)) {
            if(a.isRunning()) {
                a.cancel();
            }
            _backgroundList.remove(a);
            
            // check if background list is done running
            if(_backgroundList.isEmpty()) {
                _isBackgroundRunning = false;
            }
            
            return true;
        }
        else {
            return false;
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
        return _isBackgroundRunning;
//        Iterator i = _backgroundList.iterator();
//        while(i.hasNext()) {
//            Activity a = (Activity)i.next();
//            if(a.isRunning()) {
//                return true;
//            }
//        }
//        return false;
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
        
        _isBackgroundRunning = true;
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
        
        _isBackgroundRunning = false;
    }
    
    
//    /**
//     * Print Background Activities
//     */
//    public void printBackgroundActivities() {
//        Iterator i = _backgroundList.iterator();
//        System.out.println("Background Activities: " + _backgroundList.size());
//        while(i.hasNext()) {
//            System.out.println("  - " + ((Activity)i.next())...);            
//        }
//    }
}
