/*
 * Orgstudies.java
 *
 * Created on February 6, 2005, 9:21 PM
 */

package nv2d.plugins;

import java.awt.Container;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JMenu;

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

    
	public void initialize(Graph g, Container view, NController control) {
            _graph = g;
            _view = view;
            _ctl = control;
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
            return null;
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
		if(g.getVertices().size() < 1) {
			// TODO: warning message
			return;
		}

		if(null == ((Vertex) _graph.getVertices().iterator().next()).getDatum(DATUM_LP)) {
			// TODO: warning message
			return;
		}
		
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
                            if(edge.getOpposite(v).id().equals(pair[0])) {
                                edge.setDatum(new Datum(DATUM_LP, pair[1]));
                                done = true;
                                break;
                            }
                        }
                        
                        // for each entry in the datum, there should be a corresponding outEdge
                        assert(done);
                    }
                    
                    // we should be able to delete the node datum DATUM_LP now
                    v.remDatum(DATUM_LP);
                }
	}
        
        // FilterInterface methods
        
        /** Initialize the filter and its arguments. */
    	public void initialize(Graph g, Object [] args) {
            // recognized symbols
            // and or < > <= >= ==
            // # of arguments must be even (<1983, >=1984, etc)
        }
        
	public Graph filter(Graph g) {
            return null;
        }
}
