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
 *
 * Created on Mar 6, 2005
 */

package nv2d.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import nv2d.graph.Datum;
import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.utils.Pair;

/** Assigns a color to each attribute in a particular Datum category.
 * @author bshi
 */
public class ColorLegend implements LegendInterface {
	private String _datumName;
	private Graph _g;
	
	/**
	 * Keys are datum values and values are colors.
	 */
	private Hashtable _table;
	
	private DefaultListModel _legendListModel;
	
	public ColorLegend() {
		_g = null;
		_datumName = null;
		_table = null;
		_legendListModel = null;
	}
	
	public ColorLegend(Graph g, String datumName) {
		initialize(g, datumName);
	}

	public void initialize(Graph g, String datumName) {
		_datumName = datumName;
		_g = g;
		_table = new Hashtable();

		_legendListModel = new DefaultListModel();
		
		// figure out how many unique entries there are
		Set keys = new HashSet();
		Iterator i = g.getVertices().iterator();
		while(i.hasNext()) {
			Vertex v = (Vertex) i.next();
			if(v.getDatum(_datumName) == null) {
				continue;
			}
			keys.add(v.getDatum(_datumName).get());
		}

		// automatically assign colors to everything
		float h;
		i = keys.iterator();
		int size = keys.size();
		for (int j = 0; j < size; j++) {
			h = (float) j / (float) size;
			Object key = i.next();
			Object color = Color.getHSBColor(h, 1.0f, 1.0f);
			_table.put(key, color);
						
			/* add the new legend element to the legend list.  The
			 * legend list takes a Pair object, the car() is the key
			 * and the color is the cdr(). */
			_legendListModel.addElement(new Pair(key, color));
		}
	}
	
	public void updateRendererObjects() {
		assignColors();
	}
	
	/** Assign colors to each PNode so that the renderer knows color values. */
	private void assignColors() {
		Iterator i = _g.getVertices().iterator();
		while(i.hasNext()) {
			Vertex v = (Vertex) i.next();
			Datum d = v.getDatum(_datumName);
			if(d == null) {
				continue;
			}
			v.setDatum(new Datum(nv2d.render.LegendColorizer.DATUM_LEGENDCOLOR, getColor(d.get())));
		}
	}
	
	/**
	 * @return Returns name of the attribute for this legend.
	 */
	public String getAttribute() {
		return _datumName;
	}
	
	/**
	 * @return Returns the Graph that this legend is associated with.
	 */
	public Graph getGraph() {
		return _g;
	}
	
	public DefaultListModel getListModel() {
		return _legendListModel;
	}
	
	/**
	 * Return a legend listing comprised of only the visible components.  This
	 * is useful if only a small subset of the complete model is visible and
	 * the user would like a more compact legend view.
	 */
	public DefaultListModel getFilteredListModel(Graph subset) {
		if(subset == _g) {
			return getListModel();
		}
		
		Set uniqueVals = new HashSet();
		DefaultListModel filteredModel = new DefaultListModel();
		Iterator i = subset.getVertices().iterator();
		
		while(i.hasNext()) {
			Datum d = ((Vertex) i.next()).getDatum(_datumName);
			if(d != null && !uniqueVals.contains(d.get())) {
				filteredModel.addElement(new Pair(d.get(), _table.get(d.get())));
				uniqueVals.add(d.get());
			}
		}
		return filteredModel;
	}
	
	private Color getColor(Object value) {
		return (Color) _table.get(value);
	}
}
