package nv2d.plugins;

import java.awt.Container;
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

import nv2d.graph.Graph;
import nv2d.exceptions.PluginNotCreatedException;
import nv2d.ui.NController;
import nv2d.utils.JarListing;

public class NPluginManager extends NPluginLoader
{
	// public static final String PLUGIN_DIRECTORY = "nv2d/plugins/standard";
	// public static final String PLUGIN_DIRECTORY = "./standard/";
	private static String PLUGIN_DIRECTORY = "nv2d/plugins/standard";

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

	public void load(String directory)
	{
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
					NV2DPlugin s = createPlugin(filename, PLUGIN_DIRECTORY);
					System.out.println(" * " + filename + " plug-in loaded");
				} catch(PluginNotCreatedException e) {
					System.err.println("  There was an error loading the plugin [" + filename + "]");
					System.err.println("  -> " + e.toString());
				} catch(ClassCastException e) {
					System.err.println("  There was an error loading the plugin [" + filename + "]");
					System.err.println("  -> The file is not an NV2D plugin.");
				}
			}
		}
	}

	public boolean loadFromJar(ClassLoader parent, String url) {
		URLClassLoader loader = null;
		String pname = null;
		String fullname = null;
		try {
			loader = new URLClassLoader(new URL[] { new URL(url) }, parent);
		} catch (MalformedURLException ex) {
			System.err.println("  The url for the JAR file [" + url + "] is malformed");
			System.err.println(ex.toString());
			return false;
		}
		Enumeration e = JarListing.getPluginListing(url, PLUGIN_DIRECTORY);
		if(e == null) {
			return false;
		}
		for(e = e; e.hasMoreElements();) {
			try {
				// pname = ((String) e.nextElement()).replace('/', '.');
				String enum_str = (String) e.nextElement();
				pname = extractName(enum_str);
				fullname = extractPath(enum_str);

				if(pname == null || pname.length() < 1 || pname.indexOf("$") >= 0) {
					continue;
				}
				System.out.println("Attempting to load plugin [" + pname + "]");

				// Load class from class loader. argv[0] is the name of the class to be loaded
				Class c = loader.loadClass (fullname);
				// Create an instance of the class just loaded
				NV2DPlugin s = createPlugin(c, pname);
			} catch (ClassNotFoundException ex) {
				System.err.println("  The plugin [" + pname + "] could not be found");
				System.err.println(ex.toString());
			} catch(PluginNotCreatedException ex) {
				System.err.println("  There was an error loading the plugin [" + pname + "]");
				System.err.println("  -> " + ex.toString());
			} catch(ClassCastException ex) {
				System.err.println("  There was an error loading the plugin [" + pname + "]");
				System.err.println("  -> The file is not an NV2D plugin.");
			}
		}
		return true;
	}
}
