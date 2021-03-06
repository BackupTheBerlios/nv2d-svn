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

import java.lang.StringBuffer;
import java.util.Iterator;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import nv2d.plugins.IOInterface;
import nv2d.plugins.NV2DPlugin;
import nv2d.ui.NController;

/**
 * This class creates the dialog for the GUI which manipulates the plugin manager
 * for NV2D.  The GUI allows the user to add "secure" or trusted servers from which
 * plugins can be downloaded and also allows the user to disable or enable plugins.
 * @author bshi
 */
public class PluginManagerUI extends javax.swing.JDialog {
	private NController _ctl;
	
	/**
	 * Creates new form PluginManagerUI
	 * @param parent Parent for this dialog.
	 * @param ctl The controller provides access to the program's plugin manager instance.
	 */
	public PluginManagerUI(java.awt.Frame parent, NController ctl) {
		super(parent, true);
		_ctl = ctl;
		initComponents();
		initContent();
	}
	
	/** This method is called from the constructor to fill in the components
	 * from the {@link nv2d.plugins.NPluginManager}.
	 */
	public void initContent() {
		// clear the panes of interest
		_managerListPanel.removeAll();
		// fill in the list of available importers
		Iterator i = _ctl.getPluginManager().ioIterator();
		while(i.hasNext()) {
			final IOInterface io = (IOInterface) i.next();
			final JLabel label = new JLabel(io.name() + (io.author() != null || io.author().length() > 0 ? " - [" + io.author() + "]" : ""));
			label.addMouseListener(new MouseListener() {
				Color nonselected = new Color(51, 51, 51);
				Color selected = Color.BLUE;
				
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {
					label.setForeground(label.getForeground() == selected ? nonselected : selected);
					
					StringBuffer text = new StringBuffer(io.name() + '\n');
					text.append("Author: " + io.author() + '\n');
					
					text.append("Required Arguments:\n");
					for(int i = 0; i < io.requiredArgs().length; i++) {
						text.append("   " + (i + 1) + ": " + io.requiredArgs()[i] + '\n');
					}
					
					text.append("\nDescription:\n" + io.description());
					_managerDescTxt.setText(text.toString());
				}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
			
			// add it to _managerListPanel
			_managerListPanel.add(label);
		}
		
		_managerListPanel.add(new JSeparator());
		
		// fill in the list of available plugins
		i = _ctl.getPluginManager().pluginIterator();
		while(i.hasNext()) {
			final NV2DPlugin pl = (NV2DPlugin) i.next();
			// TODO: initialize the state
			final JLabel label = new JLabel(pl.name() + (pl.author() != null || pl.author().length() > 0 ? " - [" + pl.author() + "]" : ""));
			JCheckBox cbox = new JCheckBox("");
			label.addMouseListener(new MouseListener() {
				Color nonselected = new Color(51, 51, 51);
				Color selected = Color.BLUE;
				
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {
					label.setForeground(label.getForeground() == selected ? nonselected : selected);
					
					StringBuffer text = new StringBuffer(pl.name() + '\n');
					text.append("Author: " + pl.author() + '\n');
					
					text.append("\nDescription:\n" + pl.description());
					_managerDescTxt.setText(text.toString());
				}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
			
			// add it to _managerListPanel
			_managerListPanel.add(label);
		}
		
		_managerListPanel.repaint();
		
		// fill in default security list
		_secList.setListData(_ctl.getPluginManager().secureLocations());
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        _manager = new javax.swing.JPanel();
        display = new javax.swing.JSplitPane();
        _managerListSP = new javax.swing.JScrollPane();
        _managerListPanel = new javax.swing.JPanel();
        _managerDescSP = new javax.swing.JScrollPane();
        _managerDescTxt = new javax.swing.JTextArea();
        _load = new javax.swing.JPanel();
        _loadLabel = new javax.swing.JLabel();
        _loadURI = new javax.swing.JTextField();
        _loadExec = new javax.swing.JButton();
        _sec = new javax.swing.JPanel();
        _secList = new javax.swing.JList();
        _secAdd = new javax.swing.JButton();
        _secRem = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        _manager.setLayout(new java.awt.BorderLayout());

        _manager.setBorder(new javax.swing.border.TitledBorder("Plugin Manager"));
        _manager.setPreferredSize(new java.awt.Dimension(300, 300));
        display.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        _managerListPanel.setLayout(new javax.swing.BoxLayout(_managerListPanel, javax.swing.BoxLayout.Y_AXIS));

        _managerListSP.setViewportView(_managerListPanel);

        display.setLeftComponent(_managerListSP);

        _managerDescTxt.setEditable(false);
        _managerDescTxt.setTabSize(4);
        _managerDescSP.setViewportView(_managerDescTxt);

        display.setRightComponent(_managerDescSP);

        _manager.add(display, java.awt.BorderLayout.CENTER);

        getContentPane().add(_manager, java.awt.BorderLayout.CENTER);

        _load.setLayout(new javax.swing.BoxLayout(_load, javax.swing.BoxLayout.X_AXIS));

        _load.setBorder(new javax.swing.border.TitledBorder("Load Plugins"));
        _loadLabel.setText("URI  ");
        _load.add(_loadLabel);

        _loadURI.setText("http://");
        _load.add(_loadURI);

        _loadExec.setText("Load");
        _loadExec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _loadExecActionPerformed(evt);
            }
        });

        _load.add(_loadExec);

        getContentPane().add(_load, java.awt.BorderLayout.NORTH);

        _sec.setLayout(new java.awt.BorderLayout());

        _sec.setBorder(new javax.swing.border.TitledBorder("Security - Allowed Sites"));
        _sec.add(_secList, java.awt.BorderLayout.CENTER);

        _secAdd.setText("Add Location");
        _secAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _secAddActionPerformed(evt);
            }
        });

        _sec.add(_secAdd, java.awt.BorderLayout.NORTH);

        _secRem.setText("Remove Location");
        _secRem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _secRemActionPerformed(evt);
            }
        });

        _sec.add(_secRem, java.awt.BorderLayout.SOUTH);

        getContentPane().add(_sec, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents
	
    private void _loadExecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__loadExecActionPerformed
		String url = _loadURI.getText();
		_ctl.loadModules(url);
		
		// show new content
		initContent();
    }//GEN-LAST:event__loadExecActionPerformed
	
    private void _secRemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__secRemActionPerformed
		// update the plugin manager
		_ctl.getPluginManager().remSecureLocation((String) _secList.getSelectedValue());
		
		// update the UI
		java.util.Set vals = new java.util.HashSet(java.util.Arrays.asList(_ctl.getPluginManager().secureLocations()));
		vals.remove(_secList.getSelectedValue());
		_secList.setListData(vals.toArray());
    }//GEN-LAST:event__secRemActionPerformed
	
    private void _secAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__secAddActionPerformed
		// TODO add your handling code here:
		String s = JOptionPane.showInputDialog(
				null,
				"Add a trusted server for plugins.");
		if(s != null && (s.length() > 0)) {
			java.util.Set vals = new java.util.HashSet(java.util.Arrays.asList(_ctl.getPluginManager().secureLocations()));
			vals.add(s);
			_ctl.getPluginManager().addSecureLocation(s);
			_secList.setListData(vals.toArray());
		}
    }//GEN-LAST:event__secAddActionPerformed
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel _load;
    private javax.swing.JButton _loadExec;
    private javax.swing.JLabel _loadLabel;
    private javax.swing.JTextField _loadURI;
    private javax.swing.JPanel _manager;
    private javax.swing.JScrollPane _managerDescSP;
    private javax.swing.JTextArea _managerDescTxt;
    private javax.swing.JPanel _managerListPanel;
    private javax.swing.JScrollPane _managerListSP;
    private javax.swing.JPanel _sec;
    private javax.swing.JButton _secAdd;
    private javax.swing.JList _secList;
    private javax.swing.JButton _secRem;
    private javax.swing.JSplitPane display;
    // End of variables declaration//GEN-END:variables
	
}
