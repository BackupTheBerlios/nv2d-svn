package nv2d.plugins;

import java.util.Hashtable;
import java.lang.ClassCastException;

import nv2d.exceptions.PluginNotCreatedException;

public abstract class NPluginLoader
{
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

	public IOInterface get(String name) {
		return (IOInterface) ioRegistry.get(name);
	}

	protected static NV2DPlugin createPlugin(String name, String path)
			throws PluginNotCreatedException {
		/* As a general note, the only objects that should have access to the
		 * registries are the plugins.  This class should never alter the
		 * content of said objects. */
		NV2DPlugin classptr = null;

		if(!pluginRegistry.containsKey(name) || !ioRegistry.containsKey(name))
		{
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
}
