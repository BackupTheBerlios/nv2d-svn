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
import java.util.Set;

import nv2d.exceptions.PluginNotCreatedException;
import nv2d.exceptions.JARAccessException;
import nv2d.utils.JarListing;

public class NPluginManager extends NPluginLoader {
	// public static final String PLUGIN_DIRECTORY = "nv2d/plugins/standard";
	// public static final String PLUGIN_DIRECTORY = "./standard/";
	private static String PLUGIN_DIRECTORY = "nv2d/plugins/standard";
	
	private static Set _securityList;
	
	public NPluginManager() {
		_securityList = new HashSet();
		addSecureLocation("www.netvis.org");
		addSecureLocation("web.mit.edu/bshi");
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
			if(loc.matches("jar:\\w+://" + s.replace(".", "\\.") + ".*")) {
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
	
	public void all_heartbeat() {
		
	}
	
	public void all_cleanup() {
		
	}
	
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
	
	/* TODO: Reduce redundancy -> create a generic loader method which takes in a ClassLoader as an argument,
	 * then create other methods to generate instances of a ClassLoader such as URLClassLoader
	 * which can be passed to loadFromJar.
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
			boolean success = true;
			try {
				// pname = ((String) e.nextElement()).replace('/', '.');
				String enum_str = (String) e.nextElement();
				pname = extractName(enum_str);
				fullname = extractPath(enum_str);
				
				if(pname == null || pname.length() < 1 || pname.indexOf("$") >= 0) {
					// internal classes have the file name format
					// PublicClass$InternalClass.class, and should not be
					// considered.
					continue;
				}

				if(pluginRegistry.containsKey(pname) || ioRegistry.containsKey(pname)) {
					// don't reload plugins
					System.err.println("Warning: plugin with name [" + pname + "] already loaded.  Ignoring.");
				} else {
					// Load class from class loader. argv[0] is the name of the class to be loaded
					Class c = loader.loadClass(fullname);
					// Create an instance of the class just loaded
					NV2DPlugin s = createPlugin(c, pname);
				}
			} catch (ClassNotFoundException ex) {
				System.err.println("  The plugin [" + pname + "] could not be found");
				System.err.println(ex.toString());
				success = false;
			} catch(PluginNotCreatedException ex) {
				// System.err.println("  There was an error loading the plugin [" + pname + "]");
				// System.err.println("  -> " + ex.toString());
				success = false;
			} catch(ClassCastException ex) {
				// System.err.println("  There was an error loading the plugin [" + pname + "]");
				// System.err.println("  -> The file is not an NV2D plugin.");
				success = false;
			}
			if(success) {
				System.out.println("Loaded plugin [" + pname + "]");
			}
		}
	}
}
