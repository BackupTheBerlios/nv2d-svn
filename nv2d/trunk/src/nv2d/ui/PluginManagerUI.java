/*
 * PluginManagerUI.java
 *
 * Created on February 5, 2005, 2:07 PM
 */

package nv2d.ui;

import java.lang.StringBuffer;
import java.util.Iterator;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import nv2d.plugins.IOInterface;
import nv2d.plugins.NV2DPlugin;
import nv2d.ui.NController;

/**
 *
 * @author  bshi
 */
public class PluginManagerUI extends javax.swing.JDialog {
    NController _ctl;
    
    /** Creates new form PluginManagerUI */
    public PluginManagerUI(java.awt.Frame parent, NController ctl) {
        super(parent, true);
        _ctl = ctl;
        initComponents();
        initContent();
    }
    
    /** This method is called from the constructor to fill in the components
     * from the {@link NPluginManager}.
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
        _manager.setLayout(new javax.swing.BoxLayout(_manager, javax.swing.BoxLayout.Y_AXIS));

        _manager.setBorder(new javax.swing.border.TitledBorder("Plugin Manager"));
        _manager.setPreferredSize(new java.awt.Dimension(300, 300));
        _managerListPanel.setLayout(new javax.swing.BoxLayout(_managerListPanel, javax.swing.BoxLayout.Y_AXIS));

        _managerListSP.setViewportView(_managerListPanel);

        _manager.add(_managerListSP);

        _managerDescTxt.setEditable(false);
        _managerDescTxt.setTabSize(4);
        _managerDescSP.setViewportView(_managerDescTxt);

        _manager.add(_managerDescSP);

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
        // TODO add your handling code here:
    }//GEN-LAST:event__loadExecActionPerformed

    private void _secRemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__secRemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event__secRemActionPerformed

    private void _secAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__secAddActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event__secAddActionPerformed
    
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PluginManagerUI(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    */
    
    
    
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
    // End of variables declaration//GEN-END:variables
    
}
