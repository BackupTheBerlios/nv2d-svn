package nv2d.plugins;

import java.util.Hashtable;

import nv2d.exceptions.PluginNotCreatedException;

public abstract class NPluginLoader
{
	// This is the list of loaded plug-ins
	public static Hashtable pluginRegistry = new Hashtable();
	public static Hashtable ioRegistry = new Hashtable();

	protected static NV2DPlugin createPlugin(String name)
			throws PluginNotCreatedException {
		/* As a general note, the only objects that should have access to the
		 * registries are the plugins.  This class should never alter the
		 * content of said objects. */
		NV2DPlugin classptr = null;

		if(!pluginRegistry.containsKey(name) || !ioRegistry.containsKey(name))
		{
			// detector not found
			String pluginPath = NPluginManager.PLUGIN_DIRECTORY.replace('/', '.') + '.' + name;
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
