/*
 * Orgstudies.java
 *
 * Created on February 6, 2005, 9:21 PM
 */

package nv2d.plugins.standard;

import java.awt.Container;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.Edge;
import nv2d.graph.Datum;
import nv2d.graph.FilterInterface;
import nv2d.plugins.NV2DPlugin;
import nv2d.plugins.NPluginLoader;
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
            _graph = g;
            _view = view;
            _ctl = control;
            
            _inited = false;

            run(_graph);
	}

	public void heartbeat() {
            return;
        }

	public void cleanup() {
            return;
        }

	public JPanel ui() {
            return null;
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
        
	static {
		// put factory in the hashtable for detector factories.
		NPluginLoader.reg("Orgstudies", new Orgstudies());
	}

	private void run(Graph g) {
		// check whether orgstudies data is available
		if(g == null || g.getVertices().size() < 1) {
			// TODO: warning message
			return;
		}

		if(null == ((Vertex) _graph.getVertices().iterator().next()).getDatum(DATUM_LP)) {
			// TODO: warning message
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
        
	public Graph filter(Graph g) {
		// TODO: g is ignored; we really should fix this
            // this method can only be called when the function has been initialized
            assert(_inited);
            
            String op = (String) _filterArgs[0];
            String year = (String) _filterArgs[1];

            Set collection = new HashSet();
            Iterator i = g.getEdges().iterator();
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
            
            return g.subset(collection);
        }
        
        public Object [] getYearListing() {
            return _yearListing;
        }
        
        private void bringUpUI() {
            if(_inited) {
                new OrgstudiesUI(new java.awt.Frame(), _ctl, this).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                    "You need an Orgstudies dataset in order to use this function",
                    "Orgstudies",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        
        /* swing components */
        
}
