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
 *
 * Created on March 16, 2005, 2:14 PM
 */

package nv2d.ui;

import java.awt.*;
import javax.swing.*;

import nv2d.utils.Pair;

/**
 *
 * @author  bshi
 */
public class ColorLegendUI extends javax.swing.JPanel {
	private ColorLegend _legend;
	
	/** Creates new form ColorLegendUI */
	public ColorLegendUI(ColorLegend legend) {
		_legend = legend;
		
		initComponents();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        _scrollPane = new javax.swing.JScrollPane();
        _jlist = new javax.swing.JList(_legend.getListModel());
        _jlist.setCellRenderer(new ColorLegendListRenderer());
        _optionsComboBox = new javax.swing.JComboBox();
        _topPanel = new javax.swing.JPanel();
        _legendAttribute = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        _scrollPane.setViewportView(_jlist);

        add(_scrollPane, java.awt.BorderLayout.CENTER);

        _optionsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _optionsComboBoxActionPerformed(evt);
            }
        });

        add(_optionsComboBox, java.awt.BorderLayout.SOUTH);

        _topPanel.setLayout(new java.awt.BorderLayout());

        _legendAttribute.setText("Color Legend");
        _topPanel.add(_legendAttribute, java.awt.BorderLayout.WEST);

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 10));
        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        _topPanel.add(jButton1, java.awt.BorderLayout.EAST);

        add(_topPanel, java.awt.BorderLayout.NORTH);

    }//GEN-END:initComponents

	private void _optionsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__optionsComboBoxActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event__optionsComboBoxActionPerformed

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jButton1ActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList _jlist;
    private javax.swing.JLabel _legendAttribute;
    private javax.swing.JComboBox _optionsComboBox;
    private javax.swing.JScrollPane _scrollPane;
    private javax.swing.JPanel _topPanel;
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
	
}

class ColorLegendListRenderer extends DefaultListCellRenderer {
	public Component getListCellRendererComponent(JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean hasFocus) {
		final JLabel label =
				(JLabel)super.getListCellRendererComponent(list,
				value,
				index,
				isSelected,
				hasFocus);
		
		// pair.car() -> key
		// pair.cdr() -> color
		final Pair pair = (Pair) value;
		label.setIcon(new ColorIcon((Color) pair.cdr()));
		label.setText((String) pair.cdr());
		
		label.setVerticalTextPosition(SwingConstants.TOP);
		label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
		return(label);
	}
}