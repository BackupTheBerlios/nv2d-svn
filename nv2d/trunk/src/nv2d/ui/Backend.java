package nv2d.ui;

import java.io.IOException;
import javax.swing.*;

import nv2d.graph.Graph;
import nv2d.plugins.IOInterface;
import nv2d.plugins.NPluginManager;
import nv2d.plugins.NPluginLoader;

public class Backend {
	static NPluginManager pm;
	static Graph g;
	static String usage = "Backend [path to plugins] [io_plugin] [io parameters ...]";

	public static void errormsg() {
		System.out.println(usage);
	}

	/* Current cmd-line:
	 * Backend [path to plugins] [io_plugin] [io parameters ...]
	 */
	public static void main(String[] args) {
		pm = new NPluginManager();

		if(args.length < 2) {
			System.out.println("Please provide a plugin directory");
			errormsg();
			return;
		}

		// load all the modules
		pm.load(args[0]);

		// select io module
		String io_mod = args[1];
		if(pm.type(io_mod) != NPluginLoader.PLUGIN_TYPE_IO) {
			System.out.println("Could not find IO-Plugin '" + io_mod + "'");
			errormsg();
			return;
		}

		IOInterface io = pm.get(io_mod);
		System.out.println("Getting data through the [" + io_mod + "] IO-Plugin");

		String [] io_args = new String[args.length - 2];
		// fill out IO arguments (= all but first two)
		for(int i = 3; i < args.length; i++) {
			io_args[i - 3] = args[i];
		}
		String [] reqArgs = io.requiredArgs();
		if(reqArgs.length != io_args.length) {
			System.out.println("This IO-Plugin requires " + reqArgs.length + " arguments.");
			for(int j = 0; j < reqArgs.length; j++) {
				System.out.println("   [" + j + "] " + reqArgs[j]);
			}
			errormsg();
			return;
		}
		try {
			g = (Graph) io.getData(io_args);
		} catch (IOException ioe) {
			System.out.println("There was an error importing data from this IO-Plugin.");
			System.out.print("-> " + ioe.toString());
			errormsg();
			return;
		}

		/* initialize */
		init_sequence();
	}

	public static void init_sequence() {
		/* initialize modules */
		pm.all_initialize(g);

		/* load data file if specified, otherwise query user for datafile */
	}
}
