package nv2d.plugins;

import java.util.Hashtable;

import nv2d.exceptions.PluginNotCreatedException;

public abstract class NPluginLoader
{
	// This is the list of loaded plug-ins
	public static Hashtable pluginRegistry = new Hashtable();

	protected static NV2DPlugin createPlugin(String name)
			throws PluginNotCreatedException {
		NV2DPlugin s = (NV2DPlugin) pluginRegistry.get(name);
		if(s == null) {
			// detector not found
			String pluginPath = NPluginManager.PLUGIN_DIRECTORY.replace('/', '.') + '.' + name;
			try {
				Class.forName(pluginPath);
				// Loading the class should add it to the detectorFactories
				// table.

				s = (NV2DPlugin) pluginRegistry.get(name);

				// NOTE: pluginRegistry.put(name, s); is not needed.  It is
				// handled by the plugin itself.

				if (s == null) {
					throw (new PluginNotCreatedException("Could not load the plugin."));
				}
			} catch(ClassNotFoundException e) {
				// We'll throw an exception to indicate that
				// the detector could not be created
				throw(new PluginNotCreatedException("Could not find the plugin [" + pluginPath + "]"));
			}
		}
		return s;
	}
}
