/*
 * BottomPanel.java
 *
 * Created on February 7, 2005, 4:43 PM
 */

package nv2d.ui;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;


/**
 *
 * @author  bshi
 */
public class BottomPanel extends javax.swing.JPanel {
	NController _ctl;
	Graph _g;
	
	/** Creates new form BottomPanel */
	public BottomPanel(NController ctl) {
		_ctl = ctl;
		_g = ctl.getModel();
		
		initComponents();
		setPreferredSize(new java.awt.Dimension(700, 25));
	}
	
	// TODO: does not update properly
	public void validate() {
		super.validate();
		// update needs to add the
		Object [] vnames;
		_g = _ctl.getModel();
        if(_g != null) {
            vnames = _g.getVertices().toArray();
            java.util.Arrays.sort(vnames);
        } else {
            vnames = new Object [0];
        }
		_dfVertices.removeAllItems();
		for(int i = 0; i < vnames.length; i++) {
			_dfVertices.addItem(vnames[i]);
		}
		_dfVertices.validate();
		_dfDegree.validate();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        _start = new javax.swing.JButton();
        _stop = new javax.swing.JButton();
        _center = new javax.swing.JButton();
        _reset = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        // gather the ids of all vertices
        Object [] vnames;
        if(_g != null) {
            vnames = _g.getVertices().toArray();
            java.util.Arrays.sort(vnames);
        } else {
            vnames = new Object [0];
        }
        _dfVertices = new javax.swing.JComboBox();
        _dfDegree = new javax.swing.JComboBox(new Object [] {"1 degree", "2 degrees", "3 degrees", "4 degrees", "5 degrees", "6 degrees"});
        _doFilter = new javax.swing.JButton();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        setPreferredSize(new java.awt.Dimension(2, 0));
        _start.setText(" Start ");
        _start.setToolTipText("Start the interative layout.");
        _start.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        _start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _startActionPerformed(evt);
            }
        });

        add(_start);

        _stop.setText(" Stop ");
        _stop.setToolTipText("Stop the interactive layout.");
        _stop.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        _stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _stopActionPerformed(evt);
            }
        });

        add(_stop);

        _center.setText(" Center ");
        _center.setToolTipText("Center the graph according to the center of mass of the vertices.");
        _center.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        _center.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _centerActionPerformed(evt);
            }
        });

        add(_center);

        _reset.setText(" Reset ");
        _reset.setToolTipText("Reset the graph to a random layout.");
        _reset.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        _reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _resetActionPerformed(evt);
            }
        });

        add(_reset);

        jSeparator1.setPreferredSize(new java.awt.Dimension(40, 1));
        jSeparator1.setVerifyInputWhenFocusTarget(false);
        add(jSeparator1);

        _dfVertices.setToolTipText("Select a vertex to center around.");
        _dfVertices.setPreferredSize(new java.awt.Dimension(64, 17));
        add(_dfVertices);

        _dfDegree.setToolTipText("Select the maximum degree separation from the center vertex.");
        _dfDegree.setPreferredSize(new java.awt.Dimension(64, 17));
        add(_dfDegree);

        _doFilter.setText("Filter");
        _doFilter.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        _doFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _doFilterActionPerformed(evt);
            }
        });

        add(_doFilter);

    }//GEN-END:initComponents

	private void _doFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__doFilterActionPerformed
		// TODO add your handling code here:
		if(_g == null) {
			return;
		}
		
		Object [] fargs = new Object[2];
		Vertex selectedVertex = (Vertex) _dfVertices.getSelectedItem();
		int selectedDegree = (int) _dfDegree.getSelectedIndex();
		Integer degree = null;

		try {
			// degree = Integer.valueOf(selectedDegree.substring(0, 1));
			degree = new Integer(selectedDegree + 1);
		} catch (NumberFormatException exception) {
			// show all nodes
			degree = new Integer(-1);
		}

		fargs[0] = selectedVertex;
		fargs[1] = degree;

		_ctl.setFilter(_ctl.getDegreeFilter());
		_ctl.runFilter(fargs, true);
	}//GEN-LAST:event__doFilterActionPerformed
	
    private void _startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__startActionPerformed
		// TODO add your handling code here:
		_ctl.getView().startForceDirectedLayout();
    }//GEN-LAST:event__startActionPerformed
	
    private void _stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__stopActionPerformed
		// TODO add your handling code here:
		_ctl.getView().stopForceDirectedLayout();
    }//GEN-LAST:event__stopActionPerformed
	
    private void _centerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__centerActionPerformed
		// TODO add your handling code here:
		
    }//GEN-LAST:event__centerActionPerformed
	
    private void _resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__resetActionPerformed
		// TODO add your handling code here:
		_ctl.getView().stopForceDirectedLayout();
		_ctl.getView().doRandomLayout();
    }//GEN-LAST:event__resetActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _center;
    private javax.swing.JComboBox _dfDegree;
    private javax.swing.JComboBox _dfVertices;
    private javax.swing.JButton _doFilter;
    private javax.swing.JButton _reset;
    private javax.swing.JButton _start;
    private javax.swing.JButton _stop;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
	
}
