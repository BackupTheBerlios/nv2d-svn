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

package nv2d.plugins;

import java.awt.Container;
import javax.swing.JMenu;

import nv2d.graph.Graph;
import nv2d.ui.NController;

public interface NV2DPlugin {
	/** This method is invoked by the plugin manager after NV2D core
	 * initialization is done [most importantly, the current Model (the graph)
	 * is done].  We need to pass this the model, view, controller.
	 * This method should only be called upon loading of the plugin.  Actions
	 * which need to be taken when a graph is loaded should be delegated
	 * to <code>reloadAction</code>.  Only objects which will require persistence
	 * should be instantiated in this method.
	 *
	 * Model --> Graph
	 * View -->  (Container --> RenderBox)
	 * Controller --> NController (top level program object)
	 * */
	public void initialize(Graph g, Container view, NController control);
	
	/**
	 * This method is called each time a graph is reloaded.
	 */
	public void reloadAction(Graph g);

	/** If this plugin has set any DATUM's, this method should clean them up
	 * here.
	 * */
	public void cleanup();

	/** Return a handle to the menu for this plugin.  The menu returned will be
	 * accessible under "Plugins"-->"Plugin Name".  This method may return
	 * null, in which case the plugin is indicating that it does not require a
	 * menu.
	 * */
	public JMenu menu();

	/** Provide a list of names (space delimited) of the prerequisite modules
	 * required by a given plugin. */
	public String require();

	/** Provide a short name for this plugin. */
	public String name();

	/** Provide a short description for this plugin. */
	public String description();

	/** Provide the author's name. */
	public String author();

}
