/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package nv2d.ui;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
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
	private Container _parentContainer;
	private Container _topLevelContainer;
	private NPluginManager _pm;
	private Graph _g;	// current view
	private Graph _originalGraph;	// original full set
	private RenderBox _r;
	private NMenu _menu;
	private JTabbedPane _tabs;
	private FilterInterface _filter;
	private Set _allowedURLs;
	private JPanel _bottomPane;
	private JPanel _historyPane;
	
	private DefaultListModel _history;
	
	private JComponent _outTextBox, _errTextBox;
	
	private NPrintStream _err, _out;
	
	private DegreeFilter _degreeFilter = new DegreeFilter();
	
	public MainPanel(Container topLevel, Container parent) {
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
		
		/* initialize the history mechanism */
		_history = new DefaultListModel();
		_historyPane = new HistoryUI(_history);
		_historyPane.setVisible(false);
		
		_parentContainer = parent;
		_topLevelContainer = topLevel;
		_bottomPane = new BottomPanel(this);
		
		// Important: this must be the order (loadmodules then renderbox as last two)
		_pm = new NPluginManager();
		_filter = new DefaultFilter();
		_r = new RenderBox(this);
		_menu = new NMenu(this, _r);
		_tabs = new JTabbedPane();
		
		
		_parentContainer.setPreferredSize(new Dimension(700, 500));
		
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
	
	public Container getParent() {
		return _parentContainer;
	}
	
	public Container getWindow() {
		return _topLevelContainer;
	}
	
	public JTabbedPane getCenterPane() {
		return _tabs;
	}
	
	public JPanel getBottomPane() {
		return _bottomPane;
	}
	
	public JPanel getHistoryPane() {
		return _historyPane;
	}
	
	public void initialize(HistoryElement h) {
		if(h == null) {
			return;
		}
		
		_r.clear();
		_originalGraph = h.getModel();
		_g = h.getSubgraph();
		this.reinitModules(false);	// do not save in history
		getBottomPane().validate();
	}
	
	public void initialize(String [] args) {
		// This MUST be run here so that old locations can be saved before
		// _originalGraph changes.
		_r.clear();
		
		if(args == null || args.length < 1) {
			_g = null;
		} else {
			String ioName = args[0];
			String [] ioArgs = new String[args.length - 1];
			IOInterface io;
			
			// first argument [0] is the importer name, all others are
			// all following arguments get sent into the importer (ioArgs)
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
					errorPopup("Error Loading Graph", "There was an error importing data.  New graph not loaded", null);
					_g = _originalGraph;
				}
			}
			_originalGraph = _g;
		}
		
		if(_g != null && _g.numVertices() > DegreeFilterUI.THRESHHOLD) {
			// filter it to 2 degrees using degree filter
			setFilter(_degreeFilter);
			// if clause tests for existence of vertices in graph, so next() can be used
			runFilter(new Object[] {_g.getVertices().iterator().next(), new Integer(1)}, true);
			// notify user
			errorPopup("Too Many Vertices",
					"We don't recommend showing over " + DegreeFilterUI.THRESHHOLD + " vertices at one time.\nYour graph has been filtered using the degree filter.\nChange the settings to show all vertices at the same time.",
					null);
			// runFilter() runs reinitModules()
		} else {
			reinitModules(true);
		}
		
		// TODO: needs a better mechanism here to update GUI elements
		// maybe have interface w/ update method
		// and keep track of all the panels in use and update all of them as needed
		// ((BottomPanel) getBottomPane()).validate();
		getBottomPane().validate();
	}
	
	public void errorPopup(String title, String msg, String extra) {
		System.err.println(msg);
		JOptionPane.showMessageDialog(null,
			msg,
			title,
			JOptionPane.WARNING_MESSAGE);
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
	
	public void displayBottomPane(boolean b) {
		_bottomPane.setVisible(b);
		_parentContainer.validate();
		_parentContainer.repaint();
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
	
	public void runFilter(Object [] args, boolean wholeSet) {
		if(_originalGraph == null) {
			errorPopup("No Graph Loaded", "You must load a graph before using a filter.", null);
		}
		// save vertex locations
		_r.doSaveVertexLocations();
		
		// if _originalGraph exists, pick it
		_filter.initialize((wholeSet ? _originalGraph : _g), args);
		_g = _filter.filter();
		
		// just in case a filter produces too many nodes, we will break it down using degree filter
		if(_g != null && _g.numVertices() > DegreeFilterUI.THRESHHOLD) {
			_filter = _degreeFilter;
			// if clause tests for existence of vertices in graph, so next() can be used
			_filter.initialize(_g, new Object[] {_g.getVertices().iterator().next(), new Integer(1)});
			_g = _filter.filter();
			
			// notify user
			errorPopup("Too Many Vertices",
					"We don't recommend showing over " + DegreeFilterUI.THRESHHOLD + " vertices at onetime.\nYour graph has been filtered using the degree filter.\nChange the settings to show all vertices at the same time.",
					null);
			// runFilter() runs reinitModules()
		}
		
		reinitModules(true);
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
			errorPopup("Could not load plugins", exception.toString(), null);
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

	public Graph getSubgraph() {
		return _g;
	}
	
	public RenderBox getView() {
		return _r;
	}
	
	public DegreeFilter getDegreeFilter() {
		return _degreeFilter;
	}
	
	public NPluginManager getPluginManager() {
		return _pm;
	}
	
	/** Reinitialize all the modules upon loading of a new graph.
	 * @param b determines whether a snapshot of the current graph
	 * should be saved as a {@link nv2d.ui.HistoryElement}.
	 */
	private void reinitModules(boolean saveHistory) {
		// we now supposedly have a graph, reinit all modules
		Iterator j = _pm.pluginIterator();
		while(j.hasNext()) {
			((NV2DPlugin) j.next()).initialize(_g, _r, this);
		}
		// start things up
		_r.initialize(_g);
		
		// save this graph in the history list
		if(saveHistory && _g != null && _originalGraph != null) {
			_history.addElement(new HistoryElement(this));
		}
	}
}
