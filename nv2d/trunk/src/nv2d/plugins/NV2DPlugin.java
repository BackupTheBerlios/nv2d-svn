package nv2d.plugins;

import javax.swing.JPanel;
import javax.swing.JMenu;

public interface NV2DPlugin {
	/* We need to pass this the model, view, controller.
	 * Model --> Graph 
	 * View --> TBD
	 * */
	void initialize(/* Model, View */);

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

	/** Provide a short name for this plugin. */
	public String name();

	/** Provide a short description for this plugin. */
	public String description();

	/** Provide the author's name. */
	public String author();

}
