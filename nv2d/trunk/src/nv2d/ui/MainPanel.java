package nv2d.ui;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;

import nv2d.exceptions.JARAccessException;
import nv2d.graph.FilterInterface;
import nv2d.graph.Graph;
import nv2d.graph.filter.DefaultFilter;
import nv2d.graph.filter.DegreeFilter;
import nv2d.render.RenderBox;
import nv2d.plugins.IOInterface;
import nv2d.plugins.NPluginManager;
import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;

public class MainPanel implements NController {
	private NPluginManager _pm;
	private Graph _g;	// current view
	private Graph _originalGraph;	// original full set
	private RenderBox _r;
	private NMenu _menu;
	private JTabbedPane _tabs;
	private FilterInterface _filter;
	private Set _allowedURLs;

	private JComponent _outTextBox, _errTextBox;

	private NPrintStream _err, _out;

	public MainPanel() {
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
				UIManager.put(oStringKey, new Font("Dialog", Font.PLAIN, 11));
			}
		}

		// Important: this must be the order (loadmodules then renderbox as last two)
                _pm = new NPluginManager();
		_filter = new DefaultFilter();
		_r = new RenderBox(this);
		_menu = new NMenu(this, _r);
		_tabs = new JTabbedPane();
		
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
			_tabs.add("Fatal Error", new JLabel("Due to security restrictions, this applet cannot load the appropriate plugins."));
			return;
		}
	}

	public JTabbedPane getTabs() {
		return _tabs;
	}

	public void start() {
		try {
			initialize(null);
		} catch (java.security.AccessControlException e) {
			_tabs.add("Fatal Error", new JLabel("Due to security restrictions, this applet cannot load the appropriate plugins."));
			return;
		}
	}

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

				io = _pm.getIOInterface(ioName);
				try {
					_g = (Graph) io.getData(ioArgs);
				} catch (IOException ioe) {
					System.err.println("There was an error importing data.  Perhaps your arguments were invalid.");
					_g = null;
				}
			}
			_originalGraph = _g;
		}

		if(_g != null && _g.numVertices() > DegreeFilterUI.THRESHHOLD) {
			// filter it to 2 degrees using degree filter
			setFilter(new DegreeFilter());
			// if clause tests for existence of vertices in graph, so next() can be used
			runFilter(new Object[] {_g.getVertices().iterator().next(), new Integer(1)});
			// notify user
			JOptionPane.showMessageDialog(null,
				"We don't recommend showing over " + DegreeFilterUI.THRESHHOLD + " vertices at onetime.\nYour graph has been filtered using the degree filter.\nChange the settings to show all vertices at the same time.",
				"Too Many Vertices",
				JOptionPane.WARNING_MESSAGE);
			// runFilter() runs reinitModules()
		} else {
			reinitModules();
		}
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

	public void setFilter(FilterInterface filter) {
		if(filter != null) {
			_filter = filter;
		}
	}

	public FilterInterface getFilter() {
		return _filter;
	}

	public JMenuBar getMenu() {
		return _menu;
	}

	public void runFilter(Object [] args) {
		_filter.initialize(_originalGraph, args);
		_g = _filter.filter(_originalGraph);
		reinitModules();
	}

	public void loadModules() {
		// loadModules("jar:http://web.mit.edu/bshi/www/N2.jar!/");
		loadModules("jar:http://web.mit.edu/bshi/www/N2.jar!/");
	}

	public void loadModules(String url) {
		// pass in parent class loader (necessary for Applets)
		try {
			_pm.loadFromJar(getClass().getClassLoader(), url);
		} catch (JARAccessException exception) {
			JOptionPane.showMessageDialog(null,
				exception.toString(),
				"Could not load plugins",
				JOptionPane.WARNING_MESSAGE);
		}

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

	public Graph getModel() {
		return _originalGraph;
	}

	public RenderBox getView() {
		return _r;
	}
        
        public NPluginManager getPluginManager() {
            return _pm;
        }

	private void reinitModules() {
		_r.clear();

		// we now supposedly have a graph, reinit all modules
		Iterator j = _pm.pluginIterator();
		while(j.hasNext()) {
			((NV2DPlugin) j.next()).initialize(_g, _r, this);
		}


		// start things up
		_r.initialize(_g);
	}
}
