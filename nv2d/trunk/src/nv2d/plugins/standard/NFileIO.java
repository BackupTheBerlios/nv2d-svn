package nv2d.plugins.standard;

import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JPanel;

import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;
import nv2d.plugins.IOInterface;

import nv2d.graph.Graph;

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

	/** 
	 * Requires a URL location to read a file.
	 * */
	public String [] requiredArgs() {
		String [] r = new String[1];
		r[0] = "Supply a valid URL (http://path/to/data or file:///path/to/data etc.)";
		return r;
	}

	public void initialize(Graph g/* Model, View */) {
		System.out.print("--> initialize()\n");
	}

	public void heartbeat() {
		System.out.print("--> heartbeat()\n");
	}

	public void cleanup() {
		System.out.print("--> cleanup()\n");
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
		NPluginLoader.reg("NFileIO", new NFileIO());
	}
}
