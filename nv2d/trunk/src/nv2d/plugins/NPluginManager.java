package nv2d.plugins;

import java.awt.Container;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Iterator;

import nv2d.graph.Graph;
import nv2d.exceptions.PluginNotCreatedException;

public class NPluginManager extends NPluginLoader
{
	// public static final String PLUGIN_DIRECTORY = "nv2d/plugins/standard";
	// public static final String PLUGIN_DIRECTORY = "./standard/";
	private static String PLUGIN_DIRECTORY = "standard";

	public NV2DPlugin getp(String name) {
		return ((NV2DPlugin) pluginRegistry.get(name));
	}

	public void all_initialize(Graph g, Container view) {
		/* TODO: check that requirements are met */

		/* init all the valid plugins */
		System.out.println("Initializing all plugins:");
		for ( Enumeration e = pluginRegistry.keys() ; e.hasMoreElements() ; ) {
			String pname = (String) e.nextElement();
			System.out.print("[" + pname + "] ");
			getp(pname).initialize(g, view);
		}
	}

	/* Iterator for all plugin modules */
	public Iterator iterator() {
		return pluginRegistry.values().iterator();
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
					System.out.println("  There was an error loading the plugin [" + filename + "]");
					System.out.println("  -> " + e.toString());
				} catch(ClassCastException e) {
					System.out.println("  There was an error loading the plugin [" + filename + "]");
					System.out.println("  -> The file is not an NV2D plugin.");
				}
			}
		}
	}
}
