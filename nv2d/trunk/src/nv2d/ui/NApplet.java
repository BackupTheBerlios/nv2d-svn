package nv2d.ui;

import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.*;

import nv2d.graph.Graph;
import nv2d.render.RenderBox;
import nv2d.plugins.IOInterface;
import nv2d.plugins.NPluginManager;
import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;

public class NApplet extends JApplet implements NController {
	public static final String DEFAULT_PLUGIN_DIR = "build/nv2d/plugins/standard";
	
	private NPluginManager _pm;
	private Graph _g;
	private RenderBox _r;
	private NMenu _menu;
	private JTabbedPane _tabs;

	private JComponent _outTextBox, _errTextBox;

	private NPrintStream _err, _out;

	public void init() {
		/* The following font bit is taken from
		 * http://forum.java.sun.com/thread.jsp?thread=125315&forum=57&message=330309
		 * Thanks to 'urmasoft' for the post
		 */
		Hashtable oUIDefault = UIManager.getDefaults();
		Enumeration oKey = oUIDefault.keys();
		String oStringKey = null;

		while (oKey.hasMoreElements()) {
			oStringKey = oKey.nextElement().toString();
			if (oStringKey.endsWith("font") || oStringKey.endsWith("acceleratorFont")) {
				UIManager.put(oStringKey, new Font("Dialog", Font.PLAIN, 10));
			}
		}

		// Important: this must be the order (loadmodules then renderbox as last two)
		_r = new RenderBox();
		_menu = new NMenu(this, _r);
		_tabs = new JTabbedPane();

		_tabs.setPreferredSize(new Dimension(700, 500));

		// trap output to standard streams and display them in a text box
		JTextArea errTxt = new JTextArea();
		JTextArea outTxt = new JTextArea();
		JScrollPane sp1 = new JScrollPane(errTxt);
		JScrollPane sp2 = new JScrollPane(outTxt);
		_err = new NPrintStream(System.err);
		_out = new NPrintStream(System.out);
		System.setOut(_out);
		System.setErr(_err);
		_err.addNotifyClient(errTxt);
		_out.addNotifyClient(outTxt);
		_tabs.add("Display", _r);
		_tabs.add("Output", sp2);
		_tabs.add("Errors", sp1);
		_outTextBox = sp2;
		_errTextBox = sp1;

		try {
			loadModules();
		} catch (java.security.AccessControlException e) {
			getContentPane().add(new JLabel("Due to security restrictions, this applet cannot load the appropriate plugins."));
			setVisible(true);
			return;
		}

		getContentPane().add(_r);
		setJMenuBar(_menu);
		setVisible(true);
	}

	public void start() {
		try {
			initialize(null);
		} catch (java.security.AccessControlException e) {
			getContentPane().add(new JLabel("Due to security restrictions, this applet cannot load the appropriate plugins."));
			return;
		}
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

	public void displayOutTextBox(boolean b) {
		if(b) {
			_tabs.add("Output", _outTextBox);
		} else {
			_tabs.remove(_outTextBox);
		}
		_tabs.validate();
		_tabs.repaint();
	}

	public void displayErrTextBox(boolean b) {
		if(b) {
			_tabs.add("Errors", _errTextBox);
		} else {
			_tabs.remove(_errTextBox);
		}
		_tabs.validate();
		_tabs.repaint();
	}
	
	public void loadModules() {
		_pm = new NPluginManager();

		// pass in parent class loader (necessary for Applets)
		_pm.loadFromJar(getClass().getClassLoader(), "jar:http://web.mit.edu/bshi/www/N2.jar!/");

		/* add module UI to top level UI */
		Iterator j = _pm.pluginIterator();
		while(j.hasNext()) {
			NV2DPlugin plugin = (NV2DPlugin) j.next();
			plugin.initialize(_g, _r, this);
			if(plugin.menu() != null) {
				_menu.addPluginMenu(plugin.menu());
			}
		}

		/* initialize IO plugins */
		j = _pm.ioIterator();
		while(j.hasNext()) {
			IOInterface io = (IOInterface) j.next();
			io.initialize(null, _r, this);
			if(io.menu() != null) {
				_menu.addImporterMenu(io.menu());
			}
		}
	}
}
