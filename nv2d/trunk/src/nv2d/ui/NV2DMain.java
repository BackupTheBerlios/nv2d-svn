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
	NPluginManager pm;
	Graph g;
	RenderBox r;
	String usage = "Backend [path to plugins] [io_plugin] [io parameters ...]";
	NMenu menu;

	public NV2DMain(String [] args) {
		// Important: this must be the order (loadmodules then renderbox as last two)
		r = new RenderBox();
		menu = new NMenu(r);

		initialize(args);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(r);
		setJMenuBar(menu);
		setTitle("NV2D");
		pack();
		setVisible(true);
	}

	public void initialize(String [] args) {
		System.out.println("   1");
		r.clear();
		System.out.println("   2");
		// reload modules
		loadModules(args);

		// start things up
		System.out.println("   3");
		r.initialize(g);
		System.out.println("   4");
	}

	/* Current cmd-line:
	 * Backend [path to plugins] [io_plugin] [io parameters ...]
	 */
	public static void main(String [] args) {
		new NV2DMain(args);
	}


	public void loadModules(String [] args) {
		pm = new NPluginManager();

		if(args.length < 2) {
			System.err.println("Please provide a plugin directory");
			errormsg();
			return;
		}

		// load all the modules
		pm.load(args[0]);

		// select io module
		String io_mod = args[1];
		if(pm.type(io_mod) != NPluginLoader.PLUGIN_TYPE_IO) {
			System.err.println("Could not find IO-Plugin '" + io_mod + "'");
			errormsg();
			return;
		}

		IOInterface io = pm.get(io_mod);
		io.initialize(null, r, this);
		System.out.println("Getting data through the [" + io_mod + "] IO-Plugin");

		String [] io_args = new String[args.length - 2];


		// fill out IO arguments (= all but first two)
		for(int i = 2; i < args.length; i++) {
			io_args[i - 2] = args[i];
		}
		String [] reqArgs = io.requiredArgs();
		if(reqArgs.length != io_args.length) {
			System.err.println("This IO-Plugin requires " + reqArgs.length + " arguments.");
			for(int j = 0; j < reqArgs.length; j++) {
				System.err.println("   [" + j + "] " + reqArgs[j]);
			}
			errormsg();
			return;
		}
		try {
			g = (Graph) io.getData(io_args);
		} catch (IOException ioe) {
			System.err.println("There was an error importing data from this IO-Plugin.");
			System.err.print("-> " + ioe.toString());
			errormsg();
			return;
		}

		/* initialize modules */
		pm.all_initialize(g, r, this);

		/* add module UI to top level UI */
		Iterator j = pm.iterator();
		while(j.hasNext()) {
			NV2DPlugin plugin = (NV2DPlugin) j.next();
			if(plugin.menu() != null) {
				System.out.println("Adding menu from module " + plugin.name());
				menu.addModuleMenu(plugin.menu());
			}
		}
		// finally, add module menu
		if(io.menu() != null) {
			menu.addModuleMenu(io.menu());
		}

		/* load data file if specified, otherwise query user for datafile */
	}

	void errormsg() {
		System.err.println(usage);
	}

}
