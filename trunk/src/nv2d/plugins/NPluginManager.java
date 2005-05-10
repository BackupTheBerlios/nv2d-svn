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

package nv2d.plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.Class;
import java.lang.ClassLoader;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import nv2d.exceptions.PluginNotCreatedException;
import nv2d.exceptions.JARAccessException;
import nv2d.utils.JarListing;

public class NPluginManager {
	// public static final String PLUGIN_DIRECTORY = "nv2d/plugins/standard";
	// public static final String PLUGIN_DIRECTORY = "./standard/";
	private static String PLUGIN_DIRECTORY = "nv2d/plugins/standard";
	
	public final static String [] DEFAULT_PLUGINS = {
		"nv2d.plugins.standard.DefaultImporter",
		"nv2d.plugins.standard.GraphmlImporter",
		"nv2d.plugins.standard.SNA",
		"nv2d.plugins.standard.NFileIO",
		"nv2d.plugins.standard.Orgstudies",
		"nv2d.plugins.standard.layout.LayoutPlugin"
	};

	
	private static Set _securityList;
	
	private int verbose = 0;
	
	public static final int PLUGIN_TYPE_PLAIN = 1;
	public static final int PLUGIN_TYPE_IO = 2;
	
	// This is the list of loaded plug-ins
	private static Hashtable pluginRegistry = new Hashtable();
	private static Hashtable ioRegistry = new Hashtable();
	
	public NPluginManager() {
		_securityList = new HashSet();
		addSecureLocation("www.netvis.org");
		addSecureLocation("web.mit.edu/bshi");
		addSecureLocation("web.mit.edu/prentice");
	}
	
	public void addSecureLocation(String loc) {
		_securityList.add(loc);
	}
	
	public void remSecureLocation(String loc) {
		_securityList.remove(loc);
	}
	
	public boolean isValidLocation(String loc) {
		Iterator i = _securityList.iterator();
		while(i.hasNext()) {
			String s = (String) i.next();
			if(loc.matches("jar:\\w+://" + s.replaceAll(".", "\\.") + ".*")) {
				return true;
			}
		}
		return false;
	}
	
	public Object [] secureLocations() {
		return _securityList.toArray();
	}
	
	public NV2DPlugin getp(String name) {
		return ((NV2DPlugin) pluginRegistry.get(name));
	}
	
	/* Iterator for all plugin modules */
	public Iterator pluginIterator() {
		return pluginRegistry.values().iterator();
	}
	
	public Iterator ioIterator() {
		return ioRegistry.values().iterator();
	}

	public IOInterface getIOInterface(String name) {
		return (IOInterface) ioRegistry.get(name);
	}
	
	public NV2DPlugin getNV2DPlugin(String name) {
		return (NV2DPlugin) pluginRegistry.get(name);
	}

	public int type(String name) {
		if(pluginRegistry.containsKey(name)) {
			return PLUGIN_TYPE_PLAIN;
		} else if (ioRegistry.containsKey(name)) {
			return PLUGIN_TYPE_IO;
		}
		return 0;
	}
	
	public void register(String name, NV2DPlugin plug) {
		if(plug instanceof IOInterface) {
			ioRegistry.put(name, plug);
		} else {
			pluginRegistry.put(name, plug);
		}
	}

	/*
	public void load(String directory) {
		if(directory != null) {
			PLUGIN_DIRECTORY = directory;
		}
		
		// Look for *.class files in a particular directory
		// and load them in using createPlugin
		File dir = new File(PLUGIN_DIRECTORY);
		if (!dir.exists() || dir.list() == null) {
			// File or directory does not exist
			System.out.println("The plugin directory [" + PLUGIN_DIRECTORY + "] was not found.");
			return;
		}
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		};
		
		String [] children = dir.list(filter);
		
		for (int i = 0; i < children.length; i++) {
			// Get filename of file or directory
			String filename = children[i].substring(0, children[i].length()-6);
			if (filename.indexOf('$')== -1) {
				// we have found a file corresponding to a public class
				try {
					if(pluginRegistry.containsKey(filename) || ioRegistry.containsKey(filename)) {
						// don't reload plugins
						System.err.println("Warning: plugin with name [" + filename + "] already loaded.  Ignoring.");
					} else {
						NV2DPlugin s = createPlugin(filename, PLUGIN_DIRECTORY);
						System.out.println(" * " + filename + " plug-in loaded");
					}
				} catch(PluginNotCreatedException e) {
					// System.err.println("  There was an error loading the plugin [" + filename + "]");
					// System.err.println("  -> " + e.toString());
				} catch(ClassCastException e) {
					// System.err.println("  There was an error loading the plugin [" + filename + "]");
					// System.err.println("  -> The file is not an NV2D plugin.");
				}
			}
		}
	}
	*/

	public void loadFromJar(ClassLoader parent, String url) throws JARAccessException {
		URLClassLoader loader = null;
		String pname = null;
		String fullname = null;
		
		// the java load classes have some syntax peculiarties, namely that
		// to load a jar file, the URL must begin with 'jar:' and end with '!/'
		if(!url.startsWith("jar:")) {
			url = new String("jar:" + url);
		}
		
		if(!url.endsWith(".jar!/")) {
			url = new String(url + "!/");
		}
		
		// check if the url is allowed by the _securityList
		if(!isValidLocation(url)) {
			String errmsg = "The url for the JAR file is not allowed by the security manager.  Please add it's path in the security manager.";
			System.err.println(errmsg);
			throw new JARAccessException(errmsg);
		}
		
		try {
			loader = new URLClassLoader(new URL[] { new URL(url) }, parent);
		} catch (MalformedURLException ex) {
			String errmsg = "The url for the JAR file [" + url + "] is malformed";
			System.err.println(errmsg);
			System.err.println(ex.toString());
			throw new JARAccessException(errmsg);
		}
		Enumeration e = JarListing.getPluginListing(url, PLUGIN_DIRECTORY);
		if(e == null) {
			throw new JARAccessException("No plugins found");
		}
		for(; e.hasMoreElements();) {
			load(loader, (String) e.nextElement());
		}
	}
	
	/** Load and register a local plugin. */
	public boolean load(String className) {
		String pname = className.substring(className.lastIndexOf(".") + 1, className.length());
		boolean success = false;
		
		if(pluginRegistry.containsKey(pname) || ioRegistry.containsKey(pname)) {
			// don't reload plugins
			System.err.println("Warning: plugin with name [" + pname + "] already loaded.  Ignoring.");
			return false;
		}
		
		try {
			Class c = Class.forName(className);
			NV2DPlugin s = (NV2DPlugin) c.newInstance();
			register(s.name(), s);
			success = true;
			System.out.println("Loaded plugin [" + pname + "]");
		} catch (InstantiationException ex) {
			if(verbose > 2) {
				System.err.println("  The plugin [" + pname + "] could not be instantiated");
				System.err.println(ex.toString());
			}
		} catch (IllegalAccessException ex) {
			if(verbose > 2) {
				System.err.println("  There was an IllegalAccessException while loading plugin [" + pname + "]");
				System.err.println(ex.toString());
			}
		} catch(LinkageError error) {
			System.err.println("  The plugin [" + className + "] could not be linked");
			System.err.println(error.toString());
		} catch(ClassNotFoundException error) {
			System.err.println("  The plugin [" + className + "] could not be found");
			System.err.println(error.toString());
		}
		return success;
	}
	
	public boolean load(ClassLoader loader, String fullpath) {
		boolean success = false;
		String pname = extractName(fullpath);
		String fullname = extractPath(fullpath);
		try {
			if(pname == null || pname.length() < 1 || pname.indexOf("$") >= 0) {
				// internal classes have the file name format
				// PublicClass$InternalClass.class, and should not be
				// considered.
				return false;
			}
			
			if(pluginRegistry.containsKey(pname) || ioRegistry.containsKey(pname)) {
				// don't reload plugins
				System.err.println("Warning: plugin with name [" + pname + "] already loaded.  Ignoring.");
			} else {
				// Load class from class loader. argv[0] is the name of the class to be loaded
				Class c = loader.loadClass(fullname);
				// Create an instance of the class just loaded
				NV2DPlugin s = (NV2DPlugin) c.newInstance();
				register(s.name(), s);
				success = true;
			}
		} catch (ClassNotFoundException ex) {
			System.err.println("  The plugin [" + pname + "] could not be found");
			System.err.println(ex.toString());
		} catch (InstantiationException ex) {
			if(verbose > 2) {
				System.err.println("  The plugin [" + pname + "] could not be instantiated");
				System.err.println(ex.toString());
			}
		} catch (IllegalAccessException ex) {
			if(verbose > 2) {
				System.err.println("  There was an IllegalAccessException while loading plugin [" + pname + "]");
				System.err.println(ex.toString());
			}
		} catch (ExceptionInInitializerError ex) {
			System.err.println("  The plugin [" + pname + "] could not be initialized");
			System.err.println(ex.toString());
		} catch (SecurityException  ex) {
			System.err.println("  The plugin [" + pname + "] has insufficient permission to be loaded");
			System.err.println(ex.toString());
		} catch(ClassCastException ex) {
			System.err.println("  The class [" + pname + "] does not seem to be a valid NV2D plugin");
			System.err.println(ex.toString());
		}
		

			
		if(success) {
			System.out.println("Loaded plugin [" + pname + "]");
		}
		
		return success;
	}
	
	private String extractName(String fullpath) {
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
	
	private String extractPath(String fullpath) {
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
}
