package nv2d.ui;

import java.lang.String;
import java.net.URL;
import java.util.Set;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;

import nv2d.graph.FilterInterface;
import nv2d.graph.Graph;
import nv2d.render.RenderBox;

/* NController contains the graph, renderbox, and filter */
public interface NController {
	public void initialize(String [] args);

	// get model/view
	public Graph getModel();
	public RenderBox getView();

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
