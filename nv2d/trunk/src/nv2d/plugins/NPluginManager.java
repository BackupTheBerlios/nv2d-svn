package nv2d.plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;

import nv2d.exceptions.PluginNotCreatedException;

public class NPluginManager extends NPluginLoader
{
	public static final String PLUGIN_DIRECTORY = "nv2d/plugins/standard";

	public static void main(String args[])
	{
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
					NV2DPlugin s = createPlugin(filename);
					System.out.println(filename + " plug-in loaded");
					System.out.println("Description: " + s.description());
				} catch(PluginNotCreatedException e) {
					System.out.println("There was an error loading the plugin [" + filename + "]");
					System.out.println("  -> " + e.toString());
				} catch(ClassCastException e) {
					System.out.println("There was an error loading the plugin [" + filename + "]");
					System.out.println("  -> The file is not an NV2D plugin.");
				}
			}
		}

		// Now print some info about the loaded plug-ins
		System.out.println(pluginRegistry.size() + " plug-in(s) loaded. Names are:");
		for ( Enumeration e = pluginRegistry.keys() ; e.hasMoreElements() ; ) {
			System.out.println(e.nextElement());
		}
	}
}
