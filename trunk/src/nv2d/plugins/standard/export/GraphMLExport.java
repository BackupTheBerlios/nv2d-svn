/**
 * NV2D - Social Network Visualization
 * GraphMLExport.java - June 11, 2005
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
 * * SPECIAL NOTE *
 * The GraphML specification can be found at http://graphml.graphdrawing.org.
 */

package nv2d.plugins.standard.export;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.net.URL;

import nv2d.graph.Vertex;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.Datum;

/* Copyright (C) 2005 Eddie Fagin */
public class GraphMLExport {
    FileExport _out;
    private Set _datumList;
    
    public GraphMLExport() {
        _out = null;
        _datumList = new HashSet();
    }
    

    /** Takes a graph object g and exports it to a file in GraphML format. */
    public void export(Graph g) {
        _out = new FileExport();
        _out.open();
        
        // If the user did not select/create a file, stop exporting.
        if (_out.file() == null) {
            return;
        }
        
        // Standard GraphML header
        _out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        _out.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">");
        _out.println(" <key id=\"len\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\" />");
        _out.println(" <key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\" />");
        
        // Key information for extra data stored in nodes
        Set vertices = g.getVertices();
        
        Iterator i = vertices.iterator();
        _datumList.addAll(((Vertex)i.next()).getVisibleDatumSet());
        i = _datumList.iterator();
        while (i.hasNext()) {
            Datum data = ((Datum) i.next());
            String dataName = replaceEscapeChars(data.name());
            String dataType = type(data);
            
            _out.print(" <key id=\"" + dataName +
                    "\" for=\"node\" attr.name=\"" + dataName +
                    "\"");
            if (dataType != null) {
                _out.println(" attr.type=\"" + dataType + "\" />");
            }
            else {
                _out.println("/>");
            }
        }
        
        // Specifies whether graph is directed
        _out.println(" <graph edgedefault=\"" + 
                (g.isDirected()?"directed":"undirected") +
                "\">");
        
        printVertices(g);
        printEdges(g);
        
        // Closing remarks
        _out.println(" </graph>");
        _out.println("</graphml>");
        
        _out.close();
    }
    
    private void printVertices(Graph g){
        Iterator i = g.getVertices().iterator();
        while(i.hasNext()) {
            Vertex v = (Vertex)i.next();
            
            //Print node ID
            _out.println(" <node id=\"" + replaceEscapeChars(v.id()) + "\">");
            
            // Print display ID
            if (v.displayId() != null) {
                _out.println("  <data key=\"name\">" + replaceEscapeChars(v.displayId()) + "</data>");
            }
            
            // Print data 
            Iterator iSet = _datumList.iterator();
            while(iSet.hasNext()) {
                Datum d = (Datum) iSet.next();
                String name = replaceEscapeChars(d.name());
                if (v.getDatum(name) != null) {
                _out.println("  <data key=\"" + name + "\">" + 
                        replaceEscapeChars(v.getDatum(name).get().toString()) + "</data>");
                }
            }
            
            // Close node tag
            _out.println(" </node>");
        }      
    }
    
    private void printEdges(Graph g){
        Iterator i = g.getEdges().iterator();
        int count = 0;
        while(i.hasNext()) {
            Edge e = (Edge)i.next();
            _out.println(" <edge source=\"" + replaceEscapeChars(e.getEnds().car().toString()) + "\" " +
                    "target=\"" + replaceEscapeChars(e.getEnds().cdr().toString()) + "\">");
            _out.println("  <data key=\"len\">" + e.length() + "</data>");
            _out.println(" </edge>");
            count++;
        }
    }
    
    /** Takes a datum object and returns a string reflecting its type. 
     * In the GraphML specification, the type of the GraphML-Attribute can be 
     * either boolean, int, long, float, double, or string. */
    private String type(Datum d) {
        if (d.get() instanceof Integer) {
            return "int";
        }
        else if (d.get() instanceof Long) {
            return "long";
        }
        else if (d.get() instanceof Float) {
            return "float";
        }
        else if (d.get() instanceof Double) {
            return "double";
        }
        else if (d.get() instanceof URL) {
            return "string";
        }
        else if (d.get() instanceof Boolean) {
            return "boolean";
        }
        return "string"; // default, arbitrary
    }
    
    /** XML files have a special syntax for the escape characters shown below.
     * This method makes sure the output file conforms to the XML standard by 
     * converting escape characters to entity references. */
    private String replaceEscapeChars(String s) {
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < s.length(); j++) {
            switch(s.charAt(j)) {
                case '\"':
                    buf.append("&quot;");
                    break;
                case '\'':
                    buf.append("&apos;");
                    break;
                case '&':
                    buf.append("&amp;");
                    break;
                case '<':
                    buf.append("&lt;");
                    break;
                case '>':
                    buf.append("&gt;");
                    break;
                default:
                    buf.append(s.charAt(j));
            }
        }
        return buf.toString();
    }
}
