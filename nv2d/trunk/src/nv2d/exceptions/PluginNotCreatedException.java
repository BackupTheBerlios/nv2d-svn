package nv2d.exceptions;

public class PluginNotCreatedException extends Exception {
	public PluginNotCreatedException (String msg) {
		super("Plugin could not be instantiated.\n   " + msg);
	}
}
