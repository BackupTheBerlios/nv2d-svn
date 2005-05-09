/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * $Id$
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
import nv2d.plugins.NV2DPlugin;

public class MainPanel implements NController {
	private NPluginManager _pm;
	private Graph _g;	// current view
	private Graph _originalGraph;	// original full set
	private RenderBox _r;
	private ViewInterface _view;

	private FilterInterface _filter;
	private Set _allowedURLs;
	
	private DefaultListModel _history;
	
	private ViewFactory _viewFactory;

	/* color legend variables */
	private LegendMap _legendMap;
	
	private DegreeFilter _degreeFilter = new DegreeFilter();
	
	public MainPanel(RootPaneContainer rootPaneContainer) {
		/* initialize the history mechanism */
		_history = new DefaultListModel();
		
		/* Important: this must be the order (loadmodules then renderbox as last two) */
		_pm = new NPluginManager();
		_filter = new DefaultFilter();
		_r = new RenderBox(this);
		_viewFactory = new ViewFactory(this);

		_view = new NGUI(this, rootPaneContainer);
		
		_view.addComponent(_viewFactory.getHistoryPane(), "History", ViewInterface.SIDE_PANEL);
		_view.addComponent(_viewFactory.getLayoutPane(), "Layout", ViewInterface.BOTTOM_PANEL);
		
		_view.gui().setPreferredSize(new Dimension(700, 500));
		
		loadModules();
	}
	
	public ListModel getHistory() {
		return _history;
	}
	
	public void initializeHistoryElement(HistoryElement h) {
		if(h == null) {
			return;
		}
		
		_r.clear();
		_originalGraph = h.getModel();
		_g = h.getSubgraph();

		if(_originalGraph != null) {
			_legendMap = new LegendMap(_originalGraph);
			_view.getMenu().setLegendMenu(_legendMap);
		}

		this.reinitModules(false);	// do not save in history
		_view.validate();
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
			
			if(_pm.type(ioName) != NPluginManager.PLUGIN_TYPE_IO) {
				System.err.println("Could not find IO-Plugin '" + ioName + "'");
				_g = null;
			} else {
				
				io = _pm.getIOInterface(ioName);
				try {
					_g = (Graph) io.getData(ioArgs);
				} catch (IOException ioe) {
					_view.errorPopup("Error Loading Graph", "There was an error importing data.  New graph not loaded", null);
					_g = _originalGraph;
				}
			}
			_originalGraph = _g;

			if(_originalGraph != null) {
				_legendMap = new LegendMap(_originalGraph);
				_view.getMenu().setLegendMenu(_legendMap);
			}
		}
		
		if(_g != null && _g.numVertices() > DegreeFilterUI.THRESHHOLD) {
			// filter it to 2 degrees using degree filter
			setFilter(_degreeFilter);
			// if clause tests for existence of vertices in graph, so next() can be used
			runFilter(new Object[] {_g.getVertices().iterator().next(), new Integer(1)}, true);
			// notify user
			_view.errorPopup("Too Many Vertices",
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
		_view.validate();
	}
	
	public LegendMap getLegendMap() {
		return _legendMap;
	}
	
	public void setFilter(FilterInterface filter) {
		if(filter != null) {
			_filter = filter;
		}
	}
	
	public FilterInterface getFilter() {
		return _filter;
	}
	
	public void runFilter(Object [] args, boolean wholeSet) {
		if(_originalGraph == null) {
			_view.errorPopup("No Graph Loaded", "You must load a graph before using a filter.", null);
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
			_view.errorPopup("Too Many Vertices",
					"We don't recommend showing over " + DegreeFilterUI.THRESHHOLD + " vertices at onetime.\nYour graph has been filtered using the degree filter.\nChange the settings to show all vertices at the same time.",
					null);
		}
		
		reinitModules(true);
	}
	
	public void loadModules() {
		//loadModules("jar:http://web.mit.edu/bshi/www/N2.jar!/");
		for(int j = 0; j < _pm.DEFAULT_PLUGINS.length; j++) {
			_pm.load(_pm.DEFAULT_PLUGINS[j]);
		}
		modulesPostLoad();
	}
	
	public void loadModules(String url) {
		// pass in parent class loader (necessary for Applets)
		try {
			_pm.loadFromJar(getClass().getClassLoader(), url);
		} catch (JARAccessException exception) {
			_view.errorPopup("Could not load plugins", exception.toString(), null);
		}

		modulesPostLoad();
	}
	
	public Graph getModel() {
		return _originalGraph;
	}

	public Graph getSubgraph() {
		return _g;
	}
	
	public RenderBox getRenderBox() {
		return _r;
	}

	public ViewInterface getView() {
		return _view;
	}
	
	public ViewFactory getViewFactory() {
		return _viewFactory;
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
			((NV2DPlugin) j.next()).reloadAction(_g);
		}
		// start things up
		_r.initialize(_g);
		
		// save this graph in the history list
		if(saveHistory && _g != null && _originalGraph != null) {
			_history.addElement(new HistoryElement(this));
		}
	}

	/**
	 * Perform first-time initialization for all plugins.  This method is called
	 * after the classloader finishes loading all the plugin classes.
	 */
	private void modulesPostLoad() {
		_view.getMenu().resetPluginMenu();
		_view.getMenu().resetImporterMenu();
		
		/* add module UI to top level UI */
		Iterator j = _pm.pluginIterator();
		while(j.hasNext()) {
			NV2DPlugin plugin = (NV2DPlugin) j.next();
			plugin.initialize(_g, _r, this);
			if(plugin.menu() != null) {
				_view.getMenu().addPluginMenu(plugin.menu());
			}
		}
		
		/* initialize IO plugins */
		j = _pm.ioIterator();
		while(j.hasNext()) {
			IOInterface io = (IOInterface) j.next();
			io.initialize(null, _r, this);
			if(io.menu() != null) {
				_view.getMenu().addImporterMenu(io.menu());
			}
		}
	}
}
