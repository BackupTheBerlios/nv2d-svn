package nv2d.plugins;

import java.io.IOException;

import nv2d.graph.Graph;

public interface IOInterface extends NV2DPlugin {
	/** Construct a new graph from the data. */
	public Graph getData(String [] args) throws IOException;

	/** Return an array of strings describing the arguments which this plugin
	 * needs to establish a connection and to get the data to build the graph.
	 * A user supplied list of arguments (properly mapped to the array supplied
	 * by this method) needs to be supplied to the method
	 * <code>getData()</code>.  This method allows plugin writers some freedom
	 * in implementing the features they need.
	 * */
	public String [] requiredArgs();
}
