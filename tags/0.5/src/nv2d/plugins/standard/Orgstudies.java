/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * $Id$
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

package nv2d.plugins.standard;

import java.awt.Container;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.Edge;
import nv2d.graph.Datum;
import nv2d.graph.FilterInterface;
import nv2d.plugins.NV2DPlugin;
import nv2d.ui.NController;

/**
 *
 * @author bshi
 */
public class Orgstudies implements NV2DPlugin, FilterInterface {
	private Graph _graph;
	private Container _view;
	private NController _ctl;
	public static final String DATUM_LP = "Last Published";
	
	private boolean _inited;
	private Object [] _yearListing;
	private Object [] _filterArgs;
	
	public void initialize(Graph g, Container view, NController control) {
		_view = view;
		_ctl = control;
		
		reloadAction(g);
	}
	
	public void reloadAction(Graph g) {
		_inited = false;
		_graph = g;
		run(_graph);
	}
	
	public void cleanup() {
		return;
	}
	
	public JMenu menu() {
		JMenu mod = new JMenu(name());
		JMenuItem open = new JMenuItem("Filter by Time");
		mod.add(open);
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bringUpUI();
			}
		});
		return mod;
	}
	
	public String require() {
		System.out.println("This plugin requires specific orgstudies data which can be\n" +
				"retrieved from http://web.mit.edu/bshi/Public/nv2d/os.csv.  Use the\n" +
				"NFileIO IO interface to import the data.");
		
		return null;
	}
	
	public String name() {
		return "Orgstudies";
	}
	
	public String description() {
		return "The orgstudies dataset provides a dataset containing a time\n" +
				"dimension in addition to the spacial graph data.  This plugin\n" +
				"provides a time filter which filters out edges according to\n" +
				"their age.";
	}
	
	public String author() {
		return "Bo Shi";
	}
	
	private void run(Graph g) {
		// check whether orgstudies data is available
		if(g == null || g.getVertices().size() < 1) {
			return;
		}
		
		if(null == ((Vertex) _graph.getVertices().iterator().next()).getDatum(DATUM_LP)) {
			return;
		}
		
		HashSet years = new HashSet();
		
		Iterator i = g.getVertices().iterator();
		while(i.hasNext()) {
			Vertex v = (Vertex) i.next();
			// parse the edge data string
			String edgeDataStr = (String) v.getDatum(DATUM_LP).get();
			
			if(edgeDataStr.length() < 1) {
				continue;
			}
			
			// process all the entries and outedges
			// explode using ',' delimiter
			String [] edgeData = edgeDataStr.split(",");
			for(int j = 0; j < edgeData.length; j++) {
				String [] pair = edgeData[j].split("=");
				// pair[0] is the nodeID
				// pair[1] is the year to attribute
				
				// find the appropriate edge and set the datum
				boolean done = false;
				Iterator i2 = v.outEdges().iterator();
				while(i2.hasNext()) {
					Edge edge = (Edge) i2.next();
					if(pair[0].length() > 0 && edge.getOpposite(v).id().equals(pair[0])) {
						edge.setDatum(new Datum(DATUM_LP, pair[1]));
						years.add(pair[1]);
						done = true;
						break;
					}
				}
				
				// for each entry in the datum, there should be a corresponding outEdge
				assert(done);
			}
			
			// we should be able to delete the node datum DATUM_LP now
			// Nope, can't do it, it's our way of testing whether this is a
			// valid graph to perform operations on. -bs
			// v.remDatum(DATUM_LP);
		}
		_inited = true;
		_yearListing = years.toArray();
		Arrays.sort(_yearListing);
	}
	
	// FilterInterface methods
	
	/** Initialize the filter and its arguments. */
	public void initialize(Graph g, Object [] args) {
		// recognized symbols
		// and or < > <= >= ==
		// # of arguments must be even (<1983, >=1984, etc)
		assert(args.length == 2);
		// this method can only be called when the function has been initialized
		assert(_inited);
		
		_filterArgs = args;
	}
	
	public Object [] lastArgs() {
		return _filterArgs;
	}
	
	public Graph filter() {
		// this method can only be called when the function has been initialized
		assert(_inited);
		
		String op = (String) _filterArgs[0];
		String year = (String) _filterArgs[1];
		
		Set collection = new HashSet();
		Iterator i = _graph.getEdges().iterator();
		while(i.hasNext()) {
			Edge e = (Edge) i.next();
			String eYear = (String) e.getDatum(DATUM_LP).get();
			
			if(op.equals("=")) {
				if(eYear.compareTo(year) == 0) {
					collection.add(e);
				}
			} else if(op.equals("<")) {
				if(eYear.compareTo(year) < 0) {
					collection.add(e);
				}
			} else if(op.equals(">")) {
				if(eYear.compareTo(year) > 0) {
					collection.add(e);
				}
			} else if(op.equals("<=")) {
				if(eYear.compareTo(year) <= 0) {
					collection.add(e);
				}
			} else if(op.equals(">=")) {
				if(eYear.compareTo(year) >= 0) {
					collection.add(e);
				}
			}
		}
		
		return _graph.subset(collection);
	}
	
	public Object [] getYearListing() {
		return _yearListing;
	}
	
	private void bringUpUI() {
		if(_inited) {
			new OrgstudiesUI(new java.awt.Frame(), _ctl, this).setVisible(true);
		} else {
			_ctl.getView().errorPopup(
					"Orgstudies",
					"You need an Orgstudies dataset in order to use the Orgstudies time filter",
					null);
		}
	}
}
