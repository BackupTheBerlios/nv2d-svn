package nv2d.plugins.standard.layout;

import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;

import nv2d.graph.Datum;
import nv2d.render.PElement;
import nv2d.render.PNode;
import nv2d.render.RenderSettings;
import nv2d.plugins.NV2DPlugin;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;

import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.graph.Graph;

/**
 * Layout algorithm that positions graph elements along a circle.
 * 
 * Adapted from Jeffrey Heer's CircleLayout in Prefuse
 */
public class SmartCircleLayout extends Layout {
    private double m_radius; // radius of the circle layout
    private String sortType; // how to sort the Nodes
    private boolean animateLayout;
    
    private boolean DEBUG = false;
    
    /**
     * Automatically compute radius.
     */
    public SmartCircleLayout(boolean animate) {
        sortType = LayoutPlugin.STR_SORT_ALPHABETICAL;
        animateLayout = animate;
    }

    /**
     * Set radius despite display size.
     */
    public SmartCircleLayout(double radius, boolean animate) {
        m_radius = radius;
        sortType = LayoutPlugin.STR_SORT_ALPHABETICAL;
        animateLayout = animate;
        if(DEBUG) {System.out.println("Constructing SmartCircleLayout w/" + sortType);}
    }

    public double getRadius() {
        return m_radius;
    }

    public void setRadius(double radius) {
        m_radius = radius;
    }

    public String[] measuresOffered() {
        return new String[] {LayoutPlugin.STR_SORT_ALPHABETICAL};
    }
//    /**
//     * Measures Offered
//     * 
//     * Returns a array of measure names for each type of measure
//     * that it can sort by.
//     * 
//     * Only returns the names of measures that are common among
//     * all nodes.
//     */
//    public String[] measuresOffered(ItemRegistry registry) {
//        ArrayList measureList = new ArrayList();
//        
//        Graph g = registry.getFilteredGraph();
//        Iterator nodeIter = g.getNodes();
//
//        // TODO - do we really care if SNA is loaded, lets just look at the datums?
//        // if SNA is loaded, add those
//        //if (_pluginRef != null) {
//            // ** find the datum names that ALL nodes have **
//            //  - get the datum names from the first node
//            //  - then remove those not found in consecutive nodes
//            
//            // construct measureList with all datum names from first GraphElt
//            // lets only add names of datums that we can sort by, namely numbers (not URL, etc.)
//            if(nodeIter.hasNext()) {
//                NodeItem n = (NodeItem) nodeIter.next();
//                PElement p = (PElement) n.getEntity();
//                nv2d.graph.GraphElement geData = p.getNV2DGraphElement();
//                java.util.Set s = geData.getVisibleDatumSet();
//                Iterator i = s.iterator();
//                while(i.hasNext()) {
//                    Datum d = (Datum)i.next();
//                    Object o = d.get();
//                    //if(DEBUG) {System.out.println("Is " + o.toString() + " a number?");}
//                    if(isNumber(o)) {
//                        //if(DEBUG) {System.out.println("  - YES, adding to measures");}
//                        measureList.add(d.name());                        
//                    }
//                    else {
//                        //if(DEBUG) {System.out.println("  - NO");}
//                    }
//                }
//            }
//
//            // go through each subsequent GraphElt, and remove from measureList
//            // any datum names that are NOT found.
//            while(nodeIter.hasNext()) {
//                NodeItem nn = (NodeItem) nodeIter.next();
//                PElement p = (PElement) nn.getEntity();
//                nv2d.graph.GraphElement geData = p.getNV2DGraphElement();
//                java.util.Set s = geData.getVisibleDatumSet();
//                Iterator i = s.iterator();
//                ArrayList tempList = new ArrayList(measureList);
//                while(i.hasNext()) {
//                    Datum d = (Datum)i.next();
//                    if(tempList.contains(d.name())) {
//                        tempList.remove(d.name());
//                    }
//                }
//                if(!tempList.isEmpty()) {
//                    Iterator tIter = tempList.iterator();
//                    while(tIter.hasNext()) {
//                        measureList.remove((String)tIter.next());
//                    }
//                }
//            }
//            
//            // now measureList has only the common measures from all NodeItems
//        //}
//        
//        // add Alphabetize as a measure to sort by
//        measureList.add(0, LayoutPlugin.STR_SORT_ALPHABETICAL);
//       
//        String[] measures = new String[measureList.size()];
//        Iterator mIter = measureList.iterator();
//        for (int i = 0; mIter.hasNext(); i++) {
//            measures[i] = (String)mIter.next();
//        }
//        
//        return measures;
//    }

    
    /**
     * isNumber
     * 
     * Returns true if the Object is an:
     * Integer, Long, Double, or Float
     */
    public boolean isNumber(Object o) {
        return (o instanceof Integer) || (o instanceof Double) || (o instanceof Long) || (o instanceof Float);
    }
    
    
    public void setActiveMeasure(String m) {
        //if(DEBUG) {System.out.println("SmartCircleLayout --> setting Active: " + m);}
        sortType = m;
    }
    
    /**
     * RUN
     */
    public void run(ItemRegistry registry, double frac) {
        if(DEBUG) {System.out.println("Running SmartCircleLayout" + frac);}
        Graph g = registry.getFilteredGraph();

        Rectangle2D r = super.getLayoutBounds(registry);
        double height = r.getHeight();
        double width = r.getWidth();
        double cx = r.getCenterX();
        double cy = r.getCenterY();

        // set radius
        double radius = m_radius;
        if (radius <= 0) {
            radius = 0.45 * (height < width ? height : width);
        }

        Collection nodes;


        SortedMap sm = new TreeMap();
        Iterator nodeIter = g.getNodes();

        // If SortType Alphabetical, do it
        if(DEBUG) {System.out.println("is sortType null?");}
        if(DEBUG) {System.out.println(sortType == null);}
        if(sortType.equals(LayoutPlugin.STR_SORT_ALPHABETICAL)) {
            //if(DEBUG) {System.out.println(" - doing Alphabetical");}
			for (int i=0; nodeIter.hasNext(); i++) {
			    NodeItem n = (NodeItem)nodeIter.next();
			    PNode pn = (PNode) n.getEntity();
			    String name = pn.v().toString();
			    
			    sm.put(name, n);
			}
        }
        // otherwise its a measured sort
        else {
// if SNA is loaded
//        if (_pluginRef != null) {
            //if(DEBUG) {System.out.println(" - doing " + sortType);}
            for (int i = 0; nodeIter.hasNext(); i++) {
                NodeItem n = (NodeItem) nodeIter.next();
                PElement p = (PElement) n.getEntity();
                nv2d.graph.GraphElement geData = p.getNV2DGraphElement();
                Iterator iter = geData.getVisibleDatumSet().iterator();
                //if(DEBUG) {System.out.println("Node: " + geData.displayId());}
                while(iter.hasNext()) {
                    Datum d = (Datum)iter.next();
                    if(sortType.equals(d.name())) {
                        Double key = (Double)d.get();
                        if(!sm.containsKey(key)) {
                            //if(DEBUG) {System.out.println("Putting: " + key);}
                            sm.put(key, n);
                        }
                        else {
                            // TODO - this i fix is not pretty
                            Double newkey = new Double(key.doubleValue() + ((double)i) * 10E-8);
                            //if(DEBUG) {System.out.println("Putting:" + newkey);}
                            sm.put(newkey, n);
                        }
                    }
                }
            }
        }
        
        nodes = sm.values();

        // Place nodes in Circle Formation
        int nn = g.getNodeCount();
        Iterator nIter = nodes.iterator();
        for (int i = 0; nIter.hasNext(); i++) {
            NodeItem n = (NodeItem) nIter.next();
            //if(DEBUG) {System.out.println("drawNode: " + n.getAttribute("id") + " i:" + i);}
            // Start on the lefthand side and move around
            double angle = ((2 * Math.PI * i - 1) / nn) + Math.PI;
            double x = Math.cos(angle) * radius + cx;
            double y = Math.sin(angle) * radius + cy;
            //this.setLocation(n, null, x, y);
            if(animateLayout) {
                n.updateLocation(x, y);
            }
            else {
                n.setLocation(x, y);
            }
        }

    } // run

} // end of class SmartCircleLayout

    
    
    
    
    
    
    
//    
//    if(DEBUG) {System.out.println("S: " + s.size() + " " + s.toString());}
//    Object[] datums = geData.getDatumSet().toArray();
//    if(DEBUG) {System.out.println("Datums: " + datums.length);}
//
//    for (int j = 0; j < datums.length; j++) {
//        Datum d = (Datum) datums[j];
//        if(DEBUG) {System.out.println("j=" + j);}
//        //			    Datum name: Degree
//        //			    Datum name: In-Degree
//        //			    Datum name: Betweenness
//        // TODO why dont i see betweeness, closeness, and degree
//        if(DEBUG) {System.out.println("Datum name: " + d.name());}
//        if ((d.name()).equals("In-Degree")) {
//            if(DEBUG) {System.out.println("- Putting: " + d.name());}
//            sm.put(d.get().toString(), n);
//            //break;
//        }
//    }
//}
//}








//		// Place nodes in Circle Formation
//		Iterator nodeIter = g.getNodes();
//		for (int i=0; nodeIter.hasNext(); i++) {
//		    NodeItem n = (NodeItem)nodeIter.next();
//		    PNode pn = (PNode) n.getEntity();
//		    String name = pn.v().toString();
//		    if(DEBUG) {System.out.println("CIRCLE Layout: PN: \n" + pn.v());}
//		    
//		    double angle = (2*Math.PI*i) / nn;
//		    double x = Math.cos(angle)*radius + cx;
//		    double y = Math.sin(angle)*radius + cy;
//		    this.setLocation(n, null, x, y);
//		}
    
