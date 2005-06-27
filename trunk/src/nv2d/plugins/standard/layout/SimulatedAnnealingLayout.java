package nv2d.plugins.standard.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.Dimension;

import nv2d.render.PNode;

//import nv2d.graph.Edge;
//import nv2d.graph.Vertex;
//import nv2d.graph.Graph;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceItem;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;

/**
 */
public class SimulatedAnnealingLayout extends Layout {

    protected ItemRegistry registry; // temp member variable
    
    private long m_lasttime = -1L;
    private long m_maxstep = 50L;
    private boolean m_runonce;
    private int m_iterations = 100;
    private boolean m_enforceBounds;
    
    private boolean _isRunning = false;
    
    private int MIN_RUNTIME = 2;
    
    HashMap vertexMap;
    int ncols;
    double[][] d_matrix;
    double max_edge_length;

    double[] xcoord;
    double[] ycoord;
    double[] xtemp;
    double[] ytemp;
    double[] xdelta;
    double[] ydelta;

    // stress - measures goodness of fit --> "ENERGY"
    double stress = 0;
    double old_stress = Double.MAX_VALUE;
    double pow_value = 0.00001;
    
    double t = 50;	// annealing coefficient, decreases per iteration
    double tf = 0.95;	// rate of decrease of t
    double r = 0.015;	// constant multiplier
    double crit = 0.0005;	// change in energy threshold
    double deltaE = -1;		// change in energy per iteration
    
    int ii = 0;
    
    
    /**
     * 
     * @param enforceBounds
     */
    public SimulatedAnnealingLayout(boolean enforceBounds) {
        this(enforceBounds, false);
    } //
    
    /**
     * 
     * @param enforceBounds
     * @param runonce
     */
    public SimulatedAnnealingLayout(boolean enforceBounds, boolean runonce) {
        m_enforceBounds = enforceBounds;
        m_runonce = runonce;
    } //
    
    
    /**
     */
    public void run(ItemRegistry registry, double frac) {
        this.registry = registry;
        
        // on first time through initialize state variables for this graph
        if(!_isRunning) {
            initialize();
            _isRunning = true;
        }
        
        // TODO - handle run once?
        
        // --- run animated ---
        // get timestep
        //if(m_lasttime == -1) {
        //    m_lasttime = System.currentTimeMillis() - 20;
        //}
        //long time = System.currentTimeMillis();
        //long timestep = Math.min(m_maxstep, time-m_lasttime);
        
        runSimAnneal();
        updateNodePositions(vertexMap, xcoord, ycoord);
        
        // clear temp member variable
        this.registry = null;
        if ( frac == 1.0 ) {
            reset(registry);
        }
    }

    
    /**
     * 
     *
     */
    private void initialize() {
        System.out.println("SimAnneal INIT");
        // vertexMap: VNode --> int
        vertexMap = new HashMap();
        
    	// -- map Vertices to indices in array --
		Iterator iter = registry.getNodeItems();
		int index = 0;
		while(iter.hasNext()) {
			PNode pnode = (PNode) ((VisualItem) iter.next()).getEntity();
			nv2d.graph.Vertex v = pnode.v();
    	    vertexMap.put(v, new Integer(index));
    	    index++;
		}
    	
    	ncols = index;
    	
        d_matrix = new double[ncols][ncols];

        // - initialize to all -1, indicating those nodes aren't adjacent
        for(int a=0; a<ncols; a++) {
            for(int b=0; b<ncols; b++) {
                d_matrix[a][b] = 0;
            }
        }
        
        // lets try to get Entities --> PNode --> Vertex, edge, etc.
		Iterator node_iter = registry.getNodeItems();
		max_edge_length = Double.MIN_VALUE;
		while(node_iter.hasNext()) {
			PNode pnode = (PNode) ((VisualItem) node_iter.next()).getEntity();
			nv2d.graph.Vertex v = pnode.v();
			int v_index = ((Integer)vertexMap.get(v)).intValue();
			Iterator e_iter = v.outEdges().iterator();
			while(e_iter.hasNext()) {
			    nv2d.graph.Edge e = (nv2d.graph.Edge)e_iter.next();
			    nv2d.graph.Vertex v_op = e.getOpposite(v);
			    int v_op_index = ((Integer)vertexMap.get(v_op)).intValue();
			    d_matrix[v_index][v_op_index] = e.length();
			    
			    max_edge_length = Math.max(max_edge_length, e.length());
			}
		}
		
		System.out.println("Max Edge Length: " + max_edge_length);
		
		// TODO - decide how to handle non-complete matrix
		// now subtract maxlength - each cell
	    for(int a=0; a<ncols; a++) {
	        for(int b=0; b<ncols; b++) {
	            d_matrix[a][b] = max_edge_length - d_matrix[a][b];
	        }
	    }
		
    	// sanity check
        for(int a=0; a<ncols; a++) {
            System.out.println();
            for(int b=0; b<ncols; b++) {
                System.out.print(d_matrix[a][b] + " ");
            }
        }
        System.out.println("\n\n");

        //------------------------------
        // -- initialize coord arrays --
        xcoord = new double[ncols];
        ycoord = new double[ncols];
        xtemp = new double[ncols];
        ytemp = new double[ncols];
        xdelta = new double[ncols];
        ydelta = new double[ncols];
        
        // TODO - place at CENTER OF SCREEN
        Display d = registry.getDisplay(0);
        Dimension size = d.getSize();
        double d_scale = d.getScale();
        double d_x = d.getDisplayX();
        double d_y = d.getDisplayY();
        
        System.out.println("size: " + size + " x: " + d_x + " y:" + d_y + " scale:" + d_scale);
        
        for(int i=0; i<ncols; i++) {
            xcoord[i] = 10*Math.random() - 5 + size.getWidth()/2;	// TODO - place random value from -5 to 5
            ycoord[i] = 10*Math.random() - 5 + size.getHeight()/2;
            xtemp[i] = xcoord[i];
            ytemp[i] = ycoord[i];
        }
        
        pow_value = Math.pow(ncols, 1.1);
        if(pow_value == 0) {
            pow_value = 0.00001;
        }
        
        ii = 0;
    } // -- end initialize
    
    
    /**
     * 
     * @param timestep
     */
    private void runSimAnneal() {
        System.out.println("runSimAnneal");
        // -- iterate until the change in enery falls below threshold --
        // -- or below 100/log(n) iterations --
//        System.out.println("math.log/ncols : " + 100/Math.log10(ncols));
//good        for(int ii=0; (ii<(100/Math.log10(ncols))) && (-deltaE >= crit); ii++) {
////        for(int ii=0; ii<10000; ii++) {//(ii<(100/Math.log10(ncols))) && (-deltaE >= crit); ii++) {
        
        
        long start_time = System.currentTimeMillis();
        long current_time = start_time;
        
        while((current_time - start_time) < MIN_RUNTIME) {
            // adjust annealing coefficient
            t *= tf;
            for(int i=0; i<ncols; i++) {
                xdelta[i] = 0;
                ydelta[i] = 0;
            }
            
            double sstar = 0;
            double tstar = 0;
            
            for(int i=0; i<ncols; i++) {
                for(int j=0; j<ncols; j++) {
                    if(i != j) {
                        // calculate current distance
                        double hd = Math.pow(xtemp[i]-xtemp[j], 2) + Math.pow(ytemp[i]-ytemp[j], 2);
                        double cd = Math.sqrt(hd);	// sqrt of current distance between nodes
                        // TODO
                        double rd = d_matrix[i][j]; // actual distance between nodes
//                        if(rd != -1) {

                        sstar += Math.pow(cd-rd, 2);	// sum of squared errors
                        tstar += hd;	// sum of current distances
                        
                        if(cd != 0) {
                            // with current and real distance,
                            // compute a rate of change for each coord
                            double change = ((rd/cd)-1) * (xtemp[j]-xtemp[i]);
                            xdelta[i] -= change;
                            xdelta[j] -= change;
                            
                            change = ((rd/cd)-1) * (ytemp[j]-ytemp[i]);
                            ydelta[i] -= change;
                            ydelta[j] -= change;
                        }
//                        }
                    }
                }
            }
            
            // compute new stress
            if(tstar != 0) {
                stress = Math.sqrt(sstar/tstar);
            }
            
            // adjust coords
            for(int i=0; i<ncols; i++) {
                xtemp[i] = xcoord[i] + r*t*xdelta[i];
                ytemp[i] = ycoord[i] + r*t*ydelta[i];
            }

            // compute change in energy (stress)
            deltaE = stress - old_stress;
            System.out.println("Stress:" + stress + " OldS:" + old_stress + " DeltaE:" + deltaE);
            
            //System.out.println(it + "\t:" + tstar + " " + sstar + " " + stress + " " + old_stress + " " + deltaE);
            
            // if energy is lower, update coordinates to layout
            // update rule!
            // TODO
            // else ---> randomization, take step anyway
            if(deltaE < 0 || (Math.random() < 0.1)) {
                System.out.println("  - LOWER ENERGY, updating");
                for(int i=0; i<ncols; i++) {
                    xcoord[i] = xtemp[i];
                    ycoord[i] = ytemp[i];
                }
                old_stress = stress;
            }

         
            ii++;
            System.out.println("ii:" + ii);
            current_time = System.currentTimeMillis();
        }

    } //


    private void updateNodePositions(HashMap map, double[] xcoord, double[] ycoord) {
        Rectangle2D bounds = getLayoutBounds(registry);
        double x1=0, x2=0, y1=0, y2=0;
        if ( bounds != null ) {
            x1 = bounds.getMinX(); y1 = bounds.getMinY();
            x2 = bounds.getMaxX(); y2 = bounds.getMaxY();
        }
        
        // update positions
        Iterator iter = registry.getNodeItems();
        while ( iter.hasNext() ) {
            // TODO - Prefuse Version
//            NodeItem  nitem = (NodeItem)iter.next();
            
            // TODO - Bo Version
            VisualItem vitem = ((VisualItem) iter.next());
			PNode pnode = (PNode) vitem.getEntity();
			nv2d.graph.Vertex v = pnode.v();
            
            
            //if ( nitem.isFixed() ) {
            //    if ( Double.isNaN(nitem.getX()) )
            //setLocation(nitem,null,0.0,0.0);
            //    continue;
            //}
            
            // get index of node
            // TODO - Prefuse Version
            //int index = ((Integer)map.get((Node)nitem)).intValue();
            // TODO - Bo Version
            int index = ((Integer)map.get(v)).intValue();
            
            double x = xcoord[index];
            double y = ycoord[index];
            
            if ( m_enforceBounds && bounds != null) {
                if ( x > x2 ) x = x2;
                if ( x < x1 ) x = x1;
                if ( y > y2 ) y = y2;
                if ( y < y1 ) y = y1;
            }
            // TODO - prefuse vs. bo
//            setLocation(nitem,null,x,y);
            setLocation(vitem,null,x,y);
            
            

        }
    } //
    
    
    
    
    
    
    
    
    
//    private void updateNodePositions() {
//        Rectangle2D bounds = getLayoutBounds(registry);
//        double x1=0, x2=0, y1=0, y2=0;
//        if ( bounds != null ) {
//            x1 = bounds.getMinX(); y1 = bounds.getMinY();
//            x2 = bounds.getMaxX(); y2 = bounds.getMaxY();
//        }
//        
//        // update positions
//        Iterator iter = registry.getNodeItems();
//        while ( iter.hasNext() ) {
//            NodeItem  nitem = (NodeItem)iter.next();
//            if ( nitem.isFixed() ) {
//                if ( Double.isNaN(nitem.getX()) )
//                    setLocation(nitem,null,0.0,0.0);
//                continue;
//            }
//            
//            ForceItem fitem = (ForceItem)nitem.getVizAttribute("forceItem");
//            double x = fitem.location[0];
//            double y = fitem.location[1];
//            
//            if ( m_enforceBounds && bounds != null) {
//                if ( x > x2 ) x = x2 - m_bounds_cushion;
//                if ( x < x1 ) x = x1 + m_bounds_cushion;
//                if ( y > y2 ) y = y2 - m_bounds_cushion;
//                if ( y < y1 ) y = y1 + m_bounds_cushion;
//            }
//            setLocation(nitem,null,x,y);
//        }
//    } //
    
    
    public void reset(ItemRegistry registry) {
        Iterator iter = registry.getNodeItems();
        while ( iter.hasNext() ) {
            NodeItem nitem = (NodeItem)iter.next();
            ForceItem fitem = (ForceItem)nitem.getVizAttribute("forceItem");
            if ( fitem != null ) {
                fitem.location[0] = (float)nitem.getEndLocation().getX();
                fitem.location[1] = (float)nitem.getEndLocation().getY();
                fitem.force[0]    = fitem.force[1]    = 0;
                fitem.velocity[0] = fitem.velocity[1] = 0;
            }
        }
        m_lasttime = -1L;
    } //
        
    
} // end of class SmartForceDirectedLayout






// TODO - PREFUSE VERSION

//Graph graph = registry.getFilteredGraph();
//
//// -- indecize the vertices --
//
//// vertexMap: VNode --> int
//HashMap vertexMap = new HashMap();
//
//// -- map Vertices to indices in array --
//Iterator it = graph.getNodes();
//int index = 0;
//while(it.hasNext()) {
//    Node node = (Node)it.next();
//    vertexMap.put(node, new Integer(index));
//    index++;
//}
//
//int ncols = index;
//
//// -- create distance matrix --
//// double [x][y]
//// x = current node
//// y = all outgoing connected nodes
//double[][] d_matrix = new double[ncols][ncols];
//
//// - initialize to all -1, indicating those nodes aren't adjacent
//for(int a=0; a<ncols; a++) {
//    for(int b=0; b<ncols; b++) {
//        d_matrix[a][b] = -1;
//    }
//}
//
//// get all nodes
//// for each node:
//	// iterate over all outgoing edges
//	// place those distances in the matrix
//it = graph.getNodes();        
//while(it.hasNext()) {
//    Node node = (Node)it.next();
//    int node_index = ((Integer)vertexMap.get(node)).intValue();
//    Iterator e_iter = node.getEdges();
//    while(e_iter.hasNext()) {
//        Edge e = (Edge)e_iter.next();
//        Node op_node = e.getSecondNode();
//        // TODO: ensure outgoing edge
//        //if(!op_node.equals(node)) {
//	        int op_index = ((Integer)vertexMap.get(op_node)).intValue();
//	        //nv2d.graph.Edge ent_e = 
//	        d_matrix[node_index][op_index] = 150;    	            
//        //}
//    }
//}







// perform different actions if this is a run-once or
// run-continuously layout
//if ( m_runonce ) {
//    Point2D anchor = getLayoutAnchor(registry);
//    Iterator iter = registry.getNodeItems();
//    while ( iter.hasNext() ) {
//        NodeItem  nitem = (NodeItem)iter.next();
//        nitem.setLocation(anchor);
//    }
//    m_fsim.clear();
//    initSimulator(registry, m_fsim);
//    for ( int i = 0; i < m_iterations; i++ )
//        m_fsim.runSimulator(50);
//    updateNodePositions();
//} 
//else {
