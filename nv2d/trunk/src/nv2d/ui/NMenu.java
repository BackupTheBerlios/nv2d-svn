package nv2d.gui;

import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class NMenu extends JMenuBar {

	public NMenu() {
		add(new nmFile());
		add(new nmOptimization());
		add(new nmShowHide());
		add(new nmSettings());
	}

}

class nmFile extends JMenu {
	JMenuItem _open = new JMenuItem("Import Data");
	JMenuItem _editmode = new JMenuItem("Edit Mode");
	JMenuItem _save = new JMenuItem("Save");
	JMenuItem _close = new JMenuItem("Close");

	public nmFile() {
		super("File");
		add(_open);
		add(_editmode);
		add(_save);
		add(_close);
	}
}

class nmOptimization extends JMenu {
	JMenuItem _start = new JMenuItem("Start");
	JMenuItem _stop = new JMenuItem("Stop");
	JMenuItem _center = new JMenuItem("Center");
	JMenuItem _reset= new JMenuItem("Reset");
	public nmOptimization() {
		super("Optimization");
		add(_start);
		add(_stop);
		add(_center);
		add(_reset);
	}
}

class nmShowHide extends JMenu {
	JCheckBoxMenuItem _node = new JCheckBoxMenuItem("Nodes", true);
	JCheckBoxMenuItem _edge = new JCheckBoxMenuItem("Edges", true);
	JCheckBoxMenuItem _nlabel = new JCheckBoxMenuItem("Labels", true);
	JCheckBoxMenuItem _stress = new JCheckBoxMenuItem("Stress", false);
	JCheckBoxMenuItem _length = new JCheckBoxMenuItem("Length", false);

	public nmShowHide() {
		super("Show/Hide");
		add(_node);
		add(_edge);
		add(_nlabel);
		add(_stress);
		add(_length);
	}
}

class nmSettings extends JMenu {
	JMenuItem _placeholder = new JMenuItem("Does Nothing");

	public nmSettings() {
		super("Settings");
		add(_placeholder);
	}
}
