package nv2d.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;

public class DegreeFilterUI {
	// if the number of vertices is over this, the filter automaticall kick in
	public static final int THRESHHOLD = 100;

	public static JDialog getJDialog(NController nctl) {
		final Graph g = nctl.getModel();
		final NController ctl = nctl;
		if(g == null) {
			JOptionPane.showMessageDialog(null,
				"You must load a graph before using this filter",
				"Filter Message",
				JOptionPane.WARNING_MESSAGE);
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
		final JComboBox vertices = new JComboBox(g.getVertices().toArray());
		final JComboBox degree = new JComboBox(vlist);
		JButton confirm = new JButton("Filter");
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

				ctl.runFilter(fargs);
			}
		});

		// make the jdialog
		JDialog dialog = new JDialog();
		dialog.getContentPane().add(ui);
		dialog.setTitle("Degree Filter");
		return dialog;
	}
}
