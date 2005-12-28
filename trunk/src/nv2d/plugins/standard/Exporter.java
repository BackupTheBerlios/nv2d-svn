/**
 * NV2D - Social Network Visualization
 * Exporter.java - June 7, 2005
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import nv2d.plugins.IOInterface;
import nv2d.ui.NController;
import nv2d.graph.Graph;
import nv2d.plugins.standard.export.*;
import nv2d.plugins.NV2DPlugin;

/** Base class to export data.
 * Copyright (C) 2005 Eddie Fagin
 */
public class Exporter implements NV2DPlugin{
    private String _desc;
    private String _name;
    private String _author;
    
    private NController _control;
    private boolean _wholeGraph = false;
    
    private Graph _graph = null;
    
    public Exporter() {
        _desc = new String("Exports current graph");
        _name = new String("Export to...");
        _author= new String("Eddie Fagin");
    }

    /** Constructs and returns the JMenu object needed to access the exporter
     * functions. */
    public JMenu menu() {
        JMenu mod = new JMenu(name());
        
        // Options to export the whole graph or just the visible part.
        // Note: this section does not need to be changed when adding a
        // new exporter.
        JMenu full = new JMenu("Full Graph");
        JMenu part = new JMenu("Visible Graph");
        mod.add(full);
        mod.add(part);
        
        // The following are menu extensions for exporters - needs extension
        // when new exporters are introduced.
        JMenuItem gmlf = new JMenuItem("GraphML");
        full.add(gmlf);
        gmlf.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                       _wholeGraph = true;
                       process("gml");
                    }
        });
        JMenuItem gmlp = new JMenuItem("GraphML");
        part.add(gmlp);
        gmlp.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                       _wholeGraph = false;
                       process("gml");
                    }
        });
        
        JMenuItem csvf = new JMenuItem("CSV");
        full.add(csvf);
        csvf.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                       _wholeGraph = true;
                       process("csv");
                    }
        });
        JMenuItem csvp = new JMenuItem("CSV");
        part.add(csvp);
        csvp.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                       _wholeGraph = false;
                       process("csv");
                    }
        });       
        
        return mod;
    }
    
    /** Handles the action event by running the appropriate exporter. */
    private void process(String arg) {
        if (_wholeGraph) {
            _graph = _control.getModel();
        }
        else {
            _graph = _control.getSubgraph();
        }
        
        // If no graph has been loaded yet, say so and exit exporter
        if (_graph == null) {
            System.out.println("No graph file loaded! Aborting export.");
            return;
        }
        
        if (arg.equals("gml")) {
            GraphMLExport exporter = new GraphMLExport();
            exporter.export(_graph);
        } 
        else if (arg.equals("csv")) {
            CSVExport exporter = new CSVExport();
            exporter.export(_graph);
        }
    }
    
    public void initialize(Graph g, Container view, NController control) {
        _control = control;
    }   
    public void heartbeat() {
    }   
    public void cleanup() {
        System.out.print("--> cleanup()\n");
    }  
    public JPanel ui() {
        return null;
    }
    public String require() {
        return "";
    }
    public String name() {
        return _name;
    }
    public String description() {
        return _desc;
    }
    public String author() {
        return _author;
    }
    public void reloadAction(Graph g) {
    }
}



