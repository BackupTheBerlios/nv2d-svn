/*
 * Orgstudies.java
 *
 * Created on February 6, 2005, 9:21 PM
 */

package nv2d.plugins;

import java.awt.Container;
import javax.swing.JPanel;
import javax.swing.JMenu;

import nv2d.graph.Graph;
import nv2d.plugins.NV2DPlugin;
import nv2d.ui.NController;

/**
 *
 * @author bshi
 */
public class Orgstudies implements NV2DPlugin {
    
    /** Creates a new instance of Orgstudies */
    public Orgstudies() {
    }
    
    	public void initialize(Graph g, Container view, NController control) {
            return;
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
            return null;
        }

	public String name() {
            return "Orgstudies";
        }

	public String description() {
            return "The orgstudies dataset provides a dataset containing a time\n" +
                    "dimension in addition to the spacial graph data.  This plugin\n" +
                    "provides a time filter which filters out edges according to" +
                    "their age.";
        }

	public String author() {
            return "Bo Shi";
        }
        
	static {
		// put factory in the hashtable for detector factories.
		NPluginLoader.reg("Orgstudies", new Orgstudies());
	}
    
}
