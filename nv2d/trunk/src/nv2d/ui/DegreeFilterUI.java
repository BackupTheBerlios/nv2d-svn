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
 */

package nv2d.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;

public class DegreeFilterUI {
	// if the number of vertices is over this, the filter automatically kick in
	public static final int THRESHHOLD = 100;
	
	public static JDialog getJDialog(NController nctl) {
		final Graph g = nctl.getModel();
		final NController ctl = nctl;
		
		if(g == null) {
			nctl.errorPopup("No Graph Loaded", "You must load a graph before using a filter.", null);
			return null;
		}
		
		JPanel ui;
		
		// initialize the ui
		int numVertices = g.numVertices();
		String [] vlist = null;
		ui = new JPanel();
		
		if(numVertices <= THRESHHOLD) {
			String [] tmp = {
				"1 degree",
						"2 degrees",
						"3 degrees",
						"4 degrees",
						"5 degrees",
						"6 degrees",
						"All Vertices"};
						vlist = tmp;
		} else {
			String [] tmp = {
				"1 degree",
						"2 degrees",
						"3 degrees",
						"4 degrees",
						"5 degrees",
						"6 degrees"};
						vlist = tmp;
		}
		
		// create the swing elements for the dialog
		Object [] vnames = g.getVertices().toArray();
		Arrays.sort(vnames);
		final JComboBox vertices = new JComboBox(vnames);
		final JComboBox degree = new JComboBox(vlist);
		final JButton confirm = new JButton("Filter");
		final JCheckBox wholeSet = new JCheckBox("Run filter on whole dataset", true);
		
		degree.setToolTipText("Vertices will be filtered according to the degree of separation between the vertex and the central vertex.");
		vertices.setToolTipText("Set the center vertex.");
		
		if(numVertices <= THRESHHOLD) {
			degree.setSelectedIndex(6);
		}
		
		vertices.setBorder(BorderFactory.createTitledBorder("Set Vertex"));
		vertices.setPreferredSize(new Dimension(100, 50));
		degree.setBorder(BorderFactory.createTitledBorder("Set Degree"));
		degree.setPreferredSize(new Dimension(100, 50));
		
		ui.add(vertices);
		ui.add(degree);
		ui.add(confirm);
		// ui.setBorder(BorderFactory.createTitledBorder("Center on Vertex"));
		// ui.setLayout(new GridLayout(3, 1));
		// ui.setPreferredSize(new Dimension(150, 75));
		
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object [] fargs = new Object[2];
				Vertex selectedVertex = (Vertex) vertices.getSelectedItem();
				String selectedDegree = (String) degree.getSelectedItem();
				Integer degree = null;
				
				try {
					degree = Integer.valueOf(selectedDegree.substring(0, 1));
				} catch (NumberFormatException exception) {
					// show all nodes
					degree = new Integer(-1);
				}
				
				fargs[0] = selectedVertex;
				fargs[1] = degree;
				
				ctl.runFilter(fargs, wholeSet.isSelected());
			}
		});
		
		// make the jdialog
		JDialog dialog = new JDialog();
		dialog.getContentPane().add(ui);
		dialog.getContentPane().add(wholeSet, "South");
		dialog.setTitle("Degree Filter");
		dialog.setModal(true);
		return dialog;
	}
}
