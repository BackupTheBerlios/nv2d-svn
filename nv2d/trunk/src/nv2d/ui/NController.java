package nv2d.ui;

import java.lang.String;

import nv2d.graph.FilterInterface;

/* NController contains the graph, renderbox, and filter */
public interface NController {
	public void initialize(String [] args);

	public void setFilter(FilterInterface filter);
	public FilterInterface getFilter();
	public void runFilter(Object [] args);

	public void displayOutTextBox(boolean b);
	public void displayErrTextBox(boolean b);
}
