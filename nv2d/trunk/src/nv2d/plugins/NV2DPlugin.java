package nv2d.plugins;

import java.awt.Container;
import javax.swing.JPanel;
import javax.swing.JMenu;

import nv2d.graph.Graph;
import nv2d.ui.NController;

public interface NV2DPlugin {
	/** This method is invoked by the plugin manager after NV2D core
	 * initialization is done [most importantly, the current Model (the graph)
	 * is done].  We need to pass this the model, view, controller.
	 *
	 * Model --> Graph
	 * View -->  (Container --> RenderBox)
	 * Controller --> NController (top level program object)
	 * */
	void initialize(Graph g, Container view, NController control);

	/** This method is called periodically by the plugin manager.  If you need
	 * to schedule actions, use this method.
	 * */
	void heartbeat();

	/** If this plugin has set any DATUM's, this method should clean them up
	 * here.
	 * */
	void cleanup();

	/** Return a handle to the user interface for this plugin.  This method may
	 * return null, in which case the plugin is indicating that it does not
	 * require a graphical user interface.
	 * */
	JPanel ui();

	/** Return a handle to the menu for this plugin.  The menu returned will be
	 * accessible under "Plugins"-->"Plugin Name".  This method may return
	 * null, in which case the plugin is indicating that it does not require a
	 * menu.
	 * */
	JMenu menu();

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
