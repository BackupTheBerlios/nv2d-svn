package nv2d.plugins.standard;

import javax.swing.JMenu;
import javax.swing.JPanel;

import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;

public class SNA implements NV2DPlugin  {
	String _desc;
	String _name;
	String _author;

	public SNA() {
		_desc = new String("This plugin calculates basic social network analysis measures for a graph and it's elements.");
		_name = new String("SNA");
		_author= new String("Bo Shi");
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
		NPluginLoader.pluginRegistry.put("SNA", new SNA());
	}
}
