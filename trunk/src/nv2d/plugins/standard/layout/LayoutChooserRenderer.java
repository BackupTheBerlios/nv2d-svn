package nv2d.plugins.standard.layout;

import java.awt.Font;
import java.awt.Component;
import javax.swing.ListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class LayoutChooserRenderer extends JLabel implements ListCellRenderer {
    private Font font;

    public LayoutChooserRenderer(String[] layouts, ImageIcon[] icons) {
        this.setOpaque(true);
        this.setHorizontalAlignment(CENTER);
        this.setVerticalAlignment(CENTER);
    }

    /**
     * For ListCellRenderer:
     * 
     * Finds the image and text for selected value
     * and returns the JLabel.
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Get the selected index. (The index param isn't always valid, so just use the value.)
        int selectedIndex = ((Integer)value).intValue();

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        // Set the icon and text
        // If icon is null, just use text
        ImageIcon icon = icons[selectedIndex];
        String layout = layouts[selectedIndex];
        setIcon(icon);
        setFont(list.getFont());
        setText(layout);

        return this;
    }

}
