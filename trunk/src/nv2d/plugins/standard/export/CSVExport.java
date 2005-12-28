/**
 * NV2D - Social Network Visualization
 * CSVExport.java - June 11, 2005
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

package nv2d.plugins.standard.export;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import nv2d.graph.Vertex;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.Datum;

/* Copyright (C) 2005 Eddie Fagin */
public class CSVExport {
    
    public CSVExport() {}
    
    private FileExport _out = null;
    private Set _datumList = new HashSet(); 
    // Specifies the CSV field separator
    private String _fs = ";";
      
    /** Takes a graph object g and exports it to a file in CSV format. */
    public void export(Graph g){
        _out = new FileExport();
        _out.open();
        
        // If the user did not select/create a file, stop exporting.
        if (_out.file() == null) {
            return;
        }
        
        Set vertices = g.getVertices();
        Iterator i = vertices.iterator();
        
        // Sets up the ordering for the datum list, based on the first node
        _datumList.addAll(((Vertex)i.next()).getVisibleDatumSet());
        
        // Print datum definition line
        i = _datumList.iterator();
        if (!i.hasNext()) {
            _out.println("\"\"");
        } else {
            while (i.hasNext()) {
                _out.print("\"" + ((Datum) i.next()).name() + "\"");
                if (i.hasNext()) {
                    _out.print(_fs);
                } else {
                    _out.println("");
                }
            }
        }
        
        i = vertices.iterator();
        while (i.hasNext()) {
            printNode((Vertex) i.next(), g);
        }
        
        _out.close();
    }
    
    /** Prints a single vertex and all data associated with it (one line
     * in the CSV format). */
    private void printNode(Vertex v, Graph g){       
        Vertex[] near = new Vertex[1];
        Edge[] out = new Edge[1];
        
        near = (Vertex[]) v.neighbors().toArray(near);
        out = (Edge[]) v.outEdges().toArray(out);
        
        double[] lengths = new double[near.length];
        
        // Print ID of node
        _out.print("\"" + v.toString() + "\"" + _fs + "\"");
        
        // Print IDs of neighbors in a comma-separated list and store their
        // respective lengths in an array
        for (int j = 0; j < near.length; j++) {
            if (near[j] != null) {
                _out.print(near[j].toString());
                
                for (int k = 0; k < out.length; k++) {
                    if (g.getEdges().contains(out[k]) && out[k].getOpposite(v).equals(near[j])) {
                        lengths[j] = out[k].length();
                        break;
                    }
                }
                
            } else {
                lengths[j] = 1;
            }
            
            if (j != near.length - 1) {
                _out.print(",");
            }
        }
        _out.print("\"" + _fs + "\"");
        
        // Print lengths of edges, repectively
        for (int j = 0; j < lengths.length; j++) {
            _out.print(""+lengths[j]);
            if (j == lengths.length - 1) {
                _out.print("\"");
            } else {
                _out.print(",");
            }
        }
        
        // Print display ID
        if (v.displayId() != null) {
            _out.print(_fs + "\"" + v.displayId() + "\"");
        }
        
        // Print data
        if (_datumList.size() != 0) {
            _out.print(_fs);
        }
        
        Iterator iSet = _datumList.iterator();
        while(iSet.hasNext()) {
            Datum d = (Datum) iSet.next();
            if (v.getDatum(d.name()) != null) {
                _out.print("\"" + v.getDatum(d.name()).get().toString() + "\"");
            } else {
                _out.print("\"\"");
            }
            if (iSet.hasNext()) {
                _out.print(_fs);
            }
        }
        
        _out.println("");
    }
}
