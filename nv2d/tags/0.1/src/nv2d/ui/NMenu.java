package nv2d.ui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Iterator;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import edu.berkeley.guir.prefuse.VisualItem;

import nv2d.render.RenderBox;
import nv2d.render.RenderSettings;

public class NMenu extends JMenuBar {
	RenderBox _renderbox;
	nmMods _menu_mods;

	public NMenu(RenderBox r) {
		_renderbox = r;
		_menu_mods = new nmMods();

		add(_menu_mods);
		add(new nmOptimization());
		add(new nmShowHide());
		add(new nmSettings());
	}

	public void addModuleMenu(JMenu m) {
		_menu_mods.add(m);
	}

	public class nmMods extends JMenu {
		private JMenuItem _clear = new JMenuItem("Clear Graph");
		public nmMods() {
			super("Modules");

			_clear.addActionListener(new ClearAction());
			add(_clear);
		}

		private class ClearAction implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				_renderbox.clear();
			}
		}
	}

	public class nmOptimization extends JMenu implements ActionListener {
		JMenuItem _start = new JMenuItem("Start");
		JMenuItem _stop = new JMenuItem("Stop");
		JMenuItem _center = new JMenuItem("Center");
		JMenuItem _reset= new JMenuItem("Reset");

		// status of the optimization
		boolean running;

		public nmOptimization() {
			super("Optimization");
			running = false;
			_start.addActionListener(this);
			_stop.addActionListener(this);
			_center.addActionListener(this);
			_reset.addActionListener(this);
			add(_start);
			add(_stop);
			add(_center);
			add(_reset);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(_start)) {
				if(!running) {
					_renderbox.startForceDirectedLayout();
					running = true;
				}
			} else if (e.getSource().equals(_stop)) {
				if(running) {
					_renderbox.stopForceDirectedLayout();
					running = false;
				}
			} else if (e.getSource().equals(_reset)) {
				if(running) {
					_renderbox.stopForceDirectedLayout();
					running = false;
				}
				_renderbox.doRandomLayout();
			}
		}
	}

	public class nmShowHide extends JMenu implements ItemListener {
		JCheckBoxMenuItem _nlabel = new JCheckBoxMenuItem("Labels", true);
		JCheckBoxMenuItem _stress = new JCheckBoxMenuItem("Stress", true);
		JCheckBoxMenuItem _length = new JCheckBoxMenuItem("Length", true);

		public nmShowHide() {
			super("Show/Hide");
			_nlabel.addItemListener(this);
			_stress.addItemListener(this);
			_length.addItemListener(this);
			add(_nlabel);
			add(_stress);
			add(_length);
			addItemListener(this);
		}

		public void itemStateChanged(ItemEvent e) {
			if (e.getSource().equals(_nlabel)) {
				_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LABELS, _nlabel.getState());
			} else if (e.getSource().equals(_stress)) {
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
			}
		}
	}
}
