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

package nv2d.plugins;

import java.util.Hashtable;
import java.lang.Class;
import java.lang.ClassCastException;
import java.lang.InstantiationException;
/*
import java.lang.ClassLoader;
import java.net.URL;
import java.net.URLClassLoader;
*/

import nv2d.exceptions.PluginNotCreatedException;

public abstract class NPluginLoader {
	public static final int PLUGIN_TYPE_PLAIN = 1;
	public static final int PLUGIN_TYPE_IO = 2;
	
	// This is the list of loaded plug-ins
	protected static Hashtable pluginRegistry = new Hashtable();
	protected static Hashtable ioRegistry = new Hashtable();
	
	/** Register a plug-in. */
	public static void reg(String name, NV2DPlugin plug) {
		try {
			IOInterface ioplug = (IOInterface) plug;
			ioRegistry.put(name, plug);
			return;
		} catch(ClassCastException e) {
			pluginRegistry.put(name, plug);
		}
	}
	
	/** Get a module type.  If the module has not been loaded, a
	 * zero (or false) is returned.  Otherwise, the type of the module is
	 * returned (see the PLUGIN_TYPE_* fields). */
	public int type(String name) {
		if(pluginRegistry.containsKey(name)) {
			return PLUGIN_TYPE_PLAIN;
		} else if (ioRegistry.containsKey(name)) {
			return PLUGIN_TYPE_IO;
		}
		return 0;
	}
	
	public IOInterface getIOInterface(String name) {
		return (IOInterface) ioRegistry.get(name);
	}
	
	public NV2DPlugin getNV2DPlugin(String name) {
		return (NV2DPlugin) pluginRegistry.get(name);
	}
	
	public String extractName(String fullpath) {
		int start = fullpath.lastIndexOf("/") + 1;
		int end = fullpath.length() - 6;
		if(start < 0) {
			start = 0;
		}
		if(end < 0 || start >= end) {
			// invalid (no .class extension)
			return null;
		}
		return fullpath.substring(start, end);
	}
	
	public String extractPath(String fullpath) {
		int start = 0;
		int end = fullpath.length() - 6;
		if(start < 0) {
			start = 0;
		}
		if(end < 0 || start >= end) {
			// invalid (no .class extension)
			return null;
		}
		return fullpath.substring(start, end).replace('/','.');
	}
	
	protected static NV2DPlugin createPlugin(String name, String path)
	throws PluginNotCreatedException {
		/* As a general note, the only objects that should have access to the
		 * registries are the plugins.  This class should never alter the
		 * content of said objects. */
		NV2DPlugin classptr = null;
		
		if(!pluginRegistry.containsKey(name) || !ioRegistry.containsKey(name)) {
			// detector not found
			String pluginPath = path.replace('/', '.').substring(path.indexOf("nv2d"), path.length()) + '.' + name;
			
			try {
				Class.forName(pluginPath);
				// successful loading the class should add it to one of the
				// *Registry tables
				
				Object s = pluginRegistry.get(name);
				Object t = ioRegistry.get(name);
				
				if (s == null && t == null) {
					throw (new PluginNotCreatedException("Could not load the plugin."));
				}
				
				classptr = (NV2DPlugin) (s == null ? t : s);
			} catch(ClassNotFoundException e) {
				// We'll throw an exception to indicate that
				// the detector could not be created
				throw(new PluginNotCreatedException("Could not find the plugin [" + pluginPath + "]"));
			}
		}
		return classptr;
	}
	
	protected static NV2DPlugin createPlugin(Class c, String name)
	throws PluginNotCreatedException {
		NV2DPlugin classptr = null;
		try {
			classptr = (NV2DPlugin) c.newInstance();
			Object s = pluginRegistry.get(name);
			Object t = ioRegistry.get(name);
			
			if (s == null && t == null) {
				throw (new PluginNotCreatedException("Could not load the plugin."));
			}
			
			classptr = (NV2DPlugin) (s == null ? t : s);
		} catch (IllegalAccessException e) {
			// System.err.println("The class '" + name + "' could not be accessed.");
			// System.err.println(e.toString());
			throw(new PluginNotCreatedException("NPluginLoader: IllegalAccessException: " + name));
		} catch (InstantiationException e) {
			// System.err.println("The class '" + name + "' is not a valid NV2D plugin.");
			// System.err.println(e.toString());
			throw(new PluginNotCreatedException("NPluginLoader: InstantiationException: " + name));
		} catch (ClassCastException e) {
			// System.err.println("The class '" + name + "' is not a valid NV2D plugin.");
			// System.err.println(e.toString());
			throw(new PluginNotCreatedException("NPluginLoader: ClassCastException: " + name));
		}
		return classptr;
	}
}
