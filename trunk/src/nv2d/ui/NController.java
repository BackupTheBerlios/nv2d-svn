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
import javax.swing.*;

import nv2d.graph.FilterInterface;
import nv2d.graph.filter.DegreeFilter;
import nv2d.graph.Graph;
import nv2d.render.RenderBox;
import nv2d.plugins.NPluginManager;

/**
 * This interface provides access to the internals of NV2D.  Plugins need access
 * in order to extend the program.
 */
public interface NController {
	/**
	 * This method takes in string arguments provided and attempts to
	 * import data into a Graph.  If <code>args</code> is null or has
	 * no arguments, the visualization will not load a Graph.
	 * @param args The first argument must be the name of the {@link nv2d.plugins.IOInterface}
	 * importer to be used.  Any subsequent arguments will be passed
	 * to the importer plugin.
	 */
	public void initialize(String [] args);
	
	/**
	 * This method takes a {@link nv2d.ui.HistoryElement} object and loads the
	 * snapshot.  This method performs essentially the same task as
	 * <code>initialize(String [] args)</code> except that the graphs have
	 * been loaded already.
	 */
	public void initializeHistoryElement(HistoryElement h);
	
	// get model/view
	/**
	 * Returns the current instance of the model (as per the Model-View-Controller
	 * paradigm).  The model for NV2D is the {@link nv2d.graph.Graph} object.
	 * @return returns a {@link nv2d.graph.Graph} object.
	 */
	public Graph getModel();

	/**
	 * Returns the viewable subgraph of the model.
	 * @return returns a {@link nv2d.graph.Graph} object or <code>null</code>
	 *  if no graph has been loaded yet.
	 */
	public Graph getSubgraph();

	/**
	 * Returns the instance of the RenderBox
	 * @return returns a {@link nv2d.render.RenderBox} object.
	 */
	public RenderBox getRenderBox();


	/**
	 * Returns the GUI object.
	 */
	public ViewInterface getView();
	
	/**
	 * Get the main DegreeFilter object.  The Degree Filter is used as the
	 * browsing mechanism for large graphs so it it ubiquitous and no new
	 * instances should be created.  Instead, use this one
	 */
	public DegreeFilter getDegreeFilter();
	
	/**
	 * Returns a Frame or Applet depending on the top level container.
	 * @param returns a {@link java.awt.Frame} or {@link java.awt.Applet} object
	 * @deprecated Please use <code>getView().getWindow()</code>
	 */
	public Container getWindow();
	
	// other accessors
	/**
	 * Returns the instance of the plugin manager.
	 * @return returns a {@link nv2d.plugins.NPluginManager} object.
	 */
	public NPluginManager getPluginManager();
	
	// filter controls
	/**
	 * Set the active filter for the current {@link nv2d.graph.Graph} object.
	 * @param filter an implementation of the {@link nv2d.graph.FilterInterface} object.
	 */
	public void setFilter(FilterInterface filter);
	
	/**
	 * Get the active filter for the program.
	 * @return a {@link nv2d.graph.FilterInterface} object.
	 */
	public FilterInterface getFilter();
	
	/**
	 * Execute the active filter.
	 * @param args Implementations of {@link nv2d.graph.FilterInterface} make require different arguments.
	 * @param wholeSet If true, the filter will be run on the original data set loaded.  If false,
	 *        the filter will only be run on the visible graph.
	 */
	public void runFilter(Object [] args, boolean wholeSet);
	
	// UI controls
	/**
	 * Toggle the Output tab.
	 * @param b On/Off
	 * @deprecated This method is scheduled for removal
	 */
	public void displayOutTextBox(boolean b);
	/**
	 * Toggle the Error Messages tab.
	 * @param b On/Off
	 * @deprecated This method is scheduled for removal
	 */
	public void displayErrTextBox(boolean b);
	
	/**
	 * Show the bottom button panel.
	 * @param b on/off
	 * @deprecated Please use <code>getView().getBottomPane().setVisible(false)</code>
	 */
	public void displayBottomPane(boolean b);
	
	/**
	 * Get the instance of the main menu.
	 * @return a {@link NMenu} object.
	 * @deprecated Please use <code>getView().getMenu()</code>
	 */
	public JMenuBar getMenu();
	/**
	 * Get the instance of the JComponent containing the top level center GUI component.
	 * @return the center container in the GUI
	 * @deprecated Please use <code>getView().getCenterPane()</code>
	 */
	public Container getCenterPane();
	
	/**
	 * Get the instance of the JComponent containing the top level bottom GUI component.
	 * @return the bottom container of the GUI
	 * @deprecated Please use <code>getView().getBottomPane()</code>
	 */
	public Container getBottomPane();
	
	/**
	 * Get the Graph history
	 * @deprecated This method is scheduled for removal
	 */
	public ListModel getHistory();
	
	// plugin controls
	/**
	 * Load the default set of plugins.
	 */
	public void loadModules();
	/**
	 * Load the a set of modules from a given path.
	 * @param url path to a JAR archvie.
	 */
	public void loadModules(String url);
	
	/**
	 * Show a warning dialog and log the error message.
	 * @param title the title of the dialog window
	 * @param msg error message to be logged and shown
	 * @param extra details about the error
	 * @deprecated Please use <code>getView().errorPopup(...)</code>
	 */
	public void errorPopup(String title, String msg, String extra);
}
