package nv2d.ui;

import java.awt.Container;
import java.lang.String;
import java.net.URL;
import java.util.Set;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import nv2d.graph.FilterInterface;
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
	
	// get model/view
	/**
	 * Returns the current instance of the model (as per the Model-View-Controller
	 * paradigm).  The model for NV2D is the {@link nv2d.graph.Graph} object.
	 * @return returns a {@link nv2d.graph.Graph} object.
	 */
	public Graph getModel();
	/**
	 * Returns the current instance of the view (as per the Model-View-Controller
	 * paradigm).  The view for NV2D is the {@link nv2d.render.RenderBox} object.
	 * @return returns a {@link nv2d.render.RenderBox} object.
	 */
	public RenderBox getView();
	
	/**
	 * Returns the parent container for the main panel.
	 * @return returns a content pane object which holds this controller.
	 */
	public Container getParent();
	
	/**
	 * Returns a Frame or Applet depending on the top level container.
	 * @param returns a {@link java.awt.Frame} or {@link java.awt.Applet} object
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
	 */
	public void displayOutTextBox(boolean b);
	/**
	 * Toggle the Error Messages tab.
	 * @param b On/Off
	 */
	public void displayErrTextBox(boolean b);
	
	/**
	 * Show the bottom button panel.
	 * @param b on/off
	 */
	public void displayBottomPane(boolean b);
	
	/**
	 * Get the instance of the main menu.
	 * @return a {@link NMenu} object.
	 */
	public JMenuBar getMenu();
	/**
	 * Get the instance of the JComponent containing the top level center GUI component.
	 * @return a {@link javax.swing.JTabbedPane} instance.
	 */
	public JTabbedPane getCenterPane();
	
	/**
	 * Get the instance of the JComponent containing the top level bottom GUI component.
	 * @return a {@link javax.swing.JPanel} instance */
	public JPanel getBottomPane();
	
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
}
