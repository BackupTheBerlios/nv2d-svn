/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * $Id$
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

/** This is the panel which displays a listing of the most recent graphs
 * viewed.
 *
 * @author  bshi
 */
public class HistoryUI extends javax.swing.JPanel {
	private javax.swing.ListModel _historyElements;
	
	/** Creates new form HistoryUI */
	public HistoryUI(javax.swing.ListModel historyElements) {
		_historyElements = historyElements;
		initComponents();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        _scrollPane = new javax.swing.JScrollPane();
        _jlist = new javax.swing.JList(_historyElements);
        _jlist.setCellRenderer(new HistoryListRenderer());
        jPanel1 = new javax.swing.JPanel();
        _loadHistoryElement = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        _scrollPane.setViewportView(_jlist);

        add(_scrollPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        _loadHistoryElement.setText("Load");
        _loadHistoryElement.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        _loadHistoryElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _loadHistoryElementActionPerformed(evt);
            }
        });

        jPanel1.add(_loadHistoryElement);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents

	private void _loadHistoryElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__loadHistoryElementActionPerformed
		if(_jlist.getSelectedValue() == null) {
			return;
		}
		HistoryElement h = (HistoryElement) _jlist.getSelectedValue();
		h.load();
	}//GEN-LAST:event__loadHistoryElementActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList _jlist;
    private javax.swing.JButton _loadHistoryElement;
    private javax.swing.JScrollPane _scrollPane;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
	
}

class HistoryListRenderer extends javax.swing.DefaultListCellRenderer {
	public java.awt.Component getListCellRendererComponent(javax.swing.JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean hasFocus) {
		final javax.swing.JLabel label =
				(javax.swing.JLabel)super.getListCellRendererComponent(list,
				value,
				index,
				isSelected,
				hasFocus);
		
		assert(value instanceof HistoryElement);
		
		final HistoryElement h = (HistoryElement) value;
		label.setIcon(h.getIcon());
		label.setText(h.getDesc());
		
		label.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
		label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
		return(label);
	}
}