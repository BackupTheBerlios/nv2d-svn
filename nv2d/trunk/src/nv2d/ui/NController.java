package nv2d.ui;

import java.lang.String;
import java.net.URL;
import java.util.Set;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;

import nv2d.graph.FilterInterface;
import nv2d.graph.Graph;
import nv2d.render.RenderBox;
import nv2d.plugins.NPluginManager;

/* NController contains the graph, renderbox, and filter */
/**
 * This interface provides access to the internals of NV2D.  Plugins need access in order to extend the program.
 */
public interface NController {
	/**
	 * This method takes in string arguments provided and attempts to
	 * import data into a Graph.  If <code>args</code> is null or has
	 * no arguments, the visualization will not load a Graph.
	 * @param args The first argument must be the name of the {@link IOInterface}
	 * importer to be used.  Any subsequent arguments will be passed
	 * to the importer plugin.
	 */
	public void initialize(String [] args);

	// get model/view
	public Graph getModel();
	public RenderBox getView();
        
        // other accessors
        public NPluginManager getPluginManager();

	// filter controls
	public void setFilter(FilterInterface filter);
	public FilterInterface getFilter();
	public void runFilter(Object [] args);

	// UI controls
	public void displayOutTextBox(boolean b);
	public void displayErrTextBox(boolean b);
	public JMenuBar getMenu();
	public JTabbedPane getTabs();

	// plugin controls
	public Set findPlugins(String url);
	public void allowURL(URL url);
	public boolean isURLAllowed(URL url);
}
