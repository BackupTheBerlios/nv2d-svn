package nv2d.ui;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import nv2d.render.RenderBox;
import nv2d.render.RenderSettings;

public class NMenu extends JMenuBar {
	RenderBox _renderbox;
	public NMenu(RenderBox r) {
		_renderbox = r;

		add(new nmFile());
		add(new nmOptimization());
		add(new nmShowHide());
		add(new nmSettings());
	}

	public class nmFile extends JMenu {
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

	public class nmOptimization extends JMenu {
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

	public class nmShowHide extends JMenu implements ItemListener {
		JCheckBoxMenuItem _vertex = new JCheckBoxMenuItem("Vertices", true);
		JCheckBoxMenuItem _edge = new JCheckBoxMenuItem("Edges", true);
		JCheckBoxMenuItem _nlabel = new JCheckBoxMenuItem("Labels", true);
		JCheckBoxMenuItem _stress = new JCheckBoxMenuItem("Stress", true);
		JCheckBoxMenuItem _length = new JCheckBoxMenuItem("Length", true);

		public nmShowHide() {
			super("Show/Hide");
			_vertex.addItemListener(this);
			_edge.addItemListener(this);
			_nlabel.addItemListener(this);
			_stress.addItemListener(this);
			_length.addItemListener(this);
			add(_vertex);
			add(_edge);
			add(_nlabel);
			add(_stress);
			add(_length);
			addItemListener(this);
		}

		public void itemStateChanged(ItemEvent e) {
			if(e.getSource().equals(_vertex)) {
				_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_VERTICES, _vertex.getState());
			} else if (e.getSource().equals(_edge)) {
				_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_EDGES, _edge.getState());
			} else if (e.getSource().equals(_nlabel)) {
				_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LABELS, _nlabel.getState());
			} else if (e.getSource().equals(_stress)) {
				System.out.print("stress changed");
			} else if (e.getSource().equals(_length)) {
				_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LENGTH, _length.getState());
			}
		}
	}

	public class nmSettings extends JMenu implements ItemListener {
		JCheckBoxMenuItem _aa = new JCheckBoxMenuItem("Antialias", false);

		public nmSettings() {
			super("Settings");
			add(_aa);
			_aa.addItemListener(this);
		}

		public void itemStateChanged(ItemEvent e) {
			if(e.getSource().equals(_aa)) {
				_renderbox.getRenderSettings().setBoolean(RenderSettings.ANTIALIAS, _aa.getState());
				_renderbox.setHighQuality(_aa.getState());
				// debug
				System.out.print("antialias changed");
			}
		}
	}
}
