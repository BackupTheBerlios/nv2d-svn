package nv2d.render;

import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;

public class MouseMenu extends JMenu {
	private JCheckBoxMenuItem _collapse = new JCheckBoxMenuItem("Collapse Node");
	private JCheckBoxMenuItem _info = new JCheckBoxMenuItem("Show Info");
	private JCheckBoxMenuItem _delete = new JCheckBoxMenuItem("Delete");

	public MouseMenu() {
		add(_collapse);
		add(_info);
		add(_delete);
	}
}
