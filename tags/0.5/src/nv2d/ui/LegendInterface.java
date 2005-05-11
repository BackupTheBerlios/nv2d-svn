/*
 * LegendInterface.java
 *
 * Created on May 8, 2005, 12:33 PM
 */

package nv2d.ui;

import java.awt.Color;
import javax.swing.DefaultListModel;

import nv2d.graph.Graph;

/**
 *
 * @author bshi
 */
public interface LegendInterface {

	/** 
	 * Initialize data structures and scan a graph to generate key-value
	 * pairings.  In order to update rendering, use <code>updateRenderer()</code>. */
	public void initialize(Graph g, String datumName);

	/** 
	 * Update rendering object properties to reflect initialized key-value
	 * pairs (i.e. <code>PNode</code> objects) */
	public void updateRendererObjects();
	
	/**
	 * Return the <code>Datum</code> name associated with this legend.
	 * @return name of the attribute for this legend.
	 */
	public String getAttribute();
	
	/**
	 * Return the parent graph of this legend.
	 * @return the <code>Graph</code> that this legend is associated with.
	 */
	public Graph getGraph() ;
	
	/**
	 * Get a list model to show in a UI.  Each element of the list must be a
	 * {@link nv2d.utils.Pair} object.  The <code>car()</code> must be a key
	 * (obtained with the <code>get()</code> method in {@link nv2d.graph.Datum}
	 * and the <code>cdr()</code> must be an Icon object.
	 */
	public DefaultListModel getListModel();
	
	/**
	 * Return a legend listing comprised of only the visible components.  This
	 * is useful if only a small subset of the complete model is visible and
	 * the user would like a more compact legend view.
	 */
	public DefaultListModel getFilteredListModel(Graph subset);
}
