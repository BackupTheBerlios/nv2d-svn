/*
 * PluginManagerUI.java
 *
 * Created on February 5, 2005, 3:38 AM
 */

package nv2d.ui;

import java.net.URI;
import nv2d.plugins.NPluginManager;
import nv2d.ui.NController;

/**
 *
 * @author  bshi
 */
public class PluginManagerUI extends javax.swing.JDialog {
    private NController _ctl;
    private NPluginManager _pm;
    
    /** Creates new form PluginManagerUI */
    public PluginManagerUI(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        _load = new javax.swing.JPanel();
        _loadLabel = new javax.swing.JLabel();
        _loadURI = new javax.swing.JTextField();
        _loadExec = new javax.swing.JButton();
        _manager = new javax.swing.JPanel();
        _managerList = new javax.swing.JScrollPane();
        _managerListPanel = new javax.swing.JPanel();
        exampleCheckBox = new javax.swing.JCheckBox();
        _managerInfo = new javax.swing.JScrollPane();
        _managerInfoTxt = new javax.swing.JTextPane();
        _sec = new javax.swing.JPanel();
        _secAdd = new javax.swing.JButton();
        _secRem = new javax.swing.JButton();
        _secList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Plugin Manager");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("AR PL KaitiM Big5", 0, 10));
        setLocationByPlatform(true);
        setModal(true);
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

        _manager.setLayout(new javax.swing.BoxLayout(_manager, javax.swing.BoxLayout.Y_AXIS));

        _manager.setBorder(new javax.swing.border.TitledBorder("Plugin Manager"));
        _managerListPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        _managerListPanel.setAutoscrolls(true);
        _managerListPanel.setMinimumSize(new java.awt.Dimension(1, 1));
        exampleCheckBox.setText("NFileIO");
        _managerListPanel.add(exampleCheckBox);

        _managerList.setViewportView(_managerListPanel);

        _manager.add(_managerList);

        _managerInfo.setViewportView(_managerInfoTxt);

        _manager.add(_managerInfo);

        getContentPane().add(_manager, java.awt.BorderLayout.CENTER);

        _sec.setLayout(new java.awt.BorderLayout());

        _sec.setBorder(new javax.swing.border.TitledBorder("Security Settings"));
        _sec.setMinimumSize(new java.awt.Dimension(182, 200));
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

        _sec.add(_secList, java.awt.BorderLayout.CENTER);

        getContentPane().add(_sec, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

    private void _loadExecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__loadExecActionPerformed
        // TODO add your handling code here:
        URI uri = null;
        try {
            uri = new URI(_loadURI.getText());
        } catch(java.net.URISyntaxException e) {
            // launch an error dialog
        }
        
        // TODO code for loading
    }//GEN-LAST:event__loadExecActionPerformed

    private void _secRemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__secRemActionPerformed
        // TODO add your handling code here:
        String remove = (String) _secList.getSelectedValue();
        // _pm.removeLocation(remove);
    }//GEN-LAST:event__secRemActionPerformed

    private void _secAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__secAddActionPerformed
        // TODO add your handling code here:
        
        // open up a dialog asking for one argument
	String s = javax.swing.JOptionPane.showInputDialog(
            null,
            "Add a trusted site for plugins.");

        // _pm.addLocation(s);
    }//GEN-LAST:event__secAddActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PluginManagerUI(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel _load;
    private javax.swing.JButton _loadExec;
    private javax.swing.JLabel _loadLabel;
    private javax.swing.JTextField _loadURI;
    private javax.swing.JPanel _manager;
    private javax.swing.JScrollPane _managerInfo;
    private javax.swing.JTextPane _managerInfoTxt;
    private javax.swing.JScrollPane _managerList;
    private javax.swing.JPanel _managerListPanel;
    private javax.swing.JPanel _sec;
    private javax.swing.JButton _secAdd;
    private javax.swing.JList _secList;
    private javax.swing.JButton _secRem;
    private javax.swing.JCheckBox exampleCheckBox;
    // End of variables declaration//GEN-END:variables
    
}
