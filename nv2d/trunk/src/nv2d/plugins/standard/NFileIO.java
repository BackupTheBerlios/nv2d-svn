package nv2d.plugins.standard;

import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JPanel;

import nv2d.graph.Graph;
import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;
import nv2d.plugins.IOInterface;

public class NFileIO implements IOInterface {
	String _desc;
	String _name;
	String _author;

	public NFileIO() {
		_desc = new String("This IO plugins allows you to import graphs from NV2D data files.");
		_name = new String("NFileIO");
		_author= new String("Bo Shi");
	}

	/** Construct a new graph from the data. */
	public Graph getData(String [] args) throws IOException {
		return null;
	}

	/** Return an array of strings describing the arguments which this plugin
	 * needs to establish a connection and to get the data to build the graph.
	 * A user supplied list of arguments (properly mapped to the array supplied
	 * by this method) needs to be supplied to the method
	 * <code>getData()</code>.  This method allows plugin writers some freedom
	 * in implementing the features they need.
	 * */
	public String [] requiredArgs() {
		String [] r = new String[1];
		r[0] = "Supply a valid URL (http://path/to/data or file:///path/to/data etc.)";
		return r;
	}

	public void initialize(/* Model, View */) {
		System.out.print("\n--> initialize()\n");
	}

	public void heartbeat() {
		System.out.print("\n--> heartbeat()\n");
	}

	public void cleanup() {
		System.out.print("\n--> cleanup()\n");
	}

	public JPanel ui() {
		return null;
	}

	public JMenu menu() {
		return null;
	}

	public String require() {
		return "";
	}

	public String name() {
		return _name;
	}
	public String description() {
		return _desc;
	}
	public String author() {
		return _author;
	}
   
	// Note that the following routine is static and has no name, which
	// means it will only be run when the class is loaded
	static {
		// put factory in the hashtable for detector factories.
		NPluginLoader.ioRegistry.put("NFileIO", new SNA());
	}
}
