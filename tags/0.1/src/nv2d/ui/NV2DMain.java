package nv2d.ui;

import java.io.IOException;
import java.util.Iterator;
import javax.swing.*;

import nv2d.graph.Graph;
import nv2d.render.RenderBox;
import nv2d.plugins.IOInterface;
import nv2d.plugins.NPluginManager;
import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;

public class NV2DMain extends JFrame implements NController {
	public static final String DEFAULT_PLUGIN_DIR = "build/nv2d/plugins/standard";
	
	private NPluginManager _pm;
	private Graph _g;
	private RenderBox _r;
	private NMenu _menu;

	public NV2DMain() {
		// Important: this must be the order (loadmodules then renderbox as last two)
		_r = new RenderBox();
		_menu = new NMenu(_r);

		loadModules();

		initialize(null);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(_r);
		setJMenuBar(_menu);
		setTitle("NV2D");
		pack();
		setVisible(true);
	}

	/** This method takes in string arguments provided and attempts to
	 * import data into a Graph.  If <code>args</code> is null or has
	 * not arguments, the visualization will not load a Graph.  Otherwise,
	 * the first argument must be the name of the <code>IOInterface</code>
	 * importer to be used.  Any subsequent arguments will be passed
	 * to the importer object. */
	public void initialize(String [] args) {
		if(args == null || args.length < 1) {
			_g = null;
		} else {
			String ioName = args[0];
			String [] ioArgs = new String[args.length - 1];
			IOInterface io;

			for(int j = 1; j < args.length; j++) {
				ioArgs[j - 1] = args[j];
			}

			if(_pm.type(ioName) != NPluginLoader.PLUGIN_TYPE_IO) {
				System.err.println("Could not find IO-Plugin '" + ioName + "'");
				_g = null;
			} else {

				io = _pm.get(ioName);
				try {
					_g = (Graph) io.getData(ioArgs);
				} catch (IOException ioe) {
					System.err.println("There was an error importing data.  Perhaps your arguments were invalid.");
					_g = null;
				}
			}
		}

		_r.clear();

		// we now supposedly have a graph, reinit all modules
		Iterator j = _pm.pluginIterator();
		while(j.hasNext()) {
			((NV2DPlugin) j.next()).initialize(_g, _r, this);
		}


		// start things up
		_r.initialize(_g);
	}

	/* Current cmd-line:
	 * Backend [path to plugins] [io_plugin] [io parameters ...]
	 */
	public static void main(String [] args) {
		new NApplet();
	}


	public void loadModules() {
		_pm = new NPluginManager();

		_pm.load(DEFAULT_PLUGIN_DIR);

		/* add module UI to top level UI */
		Iterator j = _pm.pluginIterator();
		while(j.hasNext()) {
			NV2DPlugin plugin = (NV2DPlugin) j.next();
			plugin.initialize(_g, _r, this);
			if(plugin.menu() != null) {
				_menu.addModuleMenu(plugin.menu());
			}
		}

		/* initialize IO plugins */
		j = _pm.ioIterator();
		while(j.hasNext()) {
			IOInterface io = (IOInterface) j.next();
			io.initialize(null, _r, this);
			if(io.menu() != null) {
				_menu.addModuleMenu(io.menu());
			}
		}
	}
}
