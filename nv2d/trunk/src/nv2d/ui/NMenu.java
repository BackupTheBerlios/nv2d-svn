package nv2d.ui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Iterator;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;

import edu.berkeley.guir.prefuse.VisualItem;

import nv2d.graph.filter.DegreeFilter;
import nv2d.render.RenderBox;
import nv2d.render.RenderSettings;

public class NMenu extends JMenuBar {
	RenderBox _renderbox;
	NController _topLevel;
	nmMods _menu_importers;
	nmPlugins _menu_plugins;

	// public NMenu(RenderBox r) {
	public NMenu(NController j, RenderBox r) {
		_topLevel = j;
		_renderbox = r;

		_menu_importers = new nmMods();
		_menu_plugins = new nmPlugins();

		add(_menu_importers);
		add(_menu_plugins);
		add(new nmOptimization());
		add(new nmView());
		add(new nmSettings());
	}

	public void addImporterMenu(JMenu m) {
		_menu_importers.add(m);
	}

	public void addPluginMenu(JMenu m) {
		_menu_plugins.add(m);
	}

	public class nmMods extends JMenu {
		private JMenuItem _clear = new JMenuItem("Clear Graph");
		public nmMods() {
			super("Import");

			_clear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_renderbox.clear();
				}
			});
			add(_clear);
			add(new JSeparator());
		}
	}

	public class nmPlugins extends JMenu {
		private JMenuItem _load = new JMenuItem("Load Plugin");
		public nmPlugins() {
			super("Plugins");
			add(_load);
			add(new JSeparator());
		}
	}

	public class nmOptimization extends JMenu {
		JMenuItem _start = new JMenuItem("Start");
		JMenuItem _stop = new JMenuItem("Stop");
		JMenuItem _center = new JMenuItem("Center");
		JMenuItem _reset= new JMenuItem("Reset");

		// status of the optimization
		boolean running;

		public nmOptimization() {
			super("Optimization");
			running = false;
			_start.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!running) {
						_renderbox.startForceDirectedLayout();
						running = true;
					}
				}
			});
			_stop.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					if(running) {
						_renderbox.stopForceDirectedLayout();
						running = false;
					}
				}
			});
			_center.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
				}
			});
			_reset.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					if(running) {
						_renderbox.stopForceDirectedLayout();
						running = false;
					}
					_renderbox.doRandomLayout();
				}
			});
			add(_start);
			add(_stop);
			add(_center);
			add(_reset);
		}
	}

	public class nmView extends JMenu implements ItemListener {
		JMenu _visualization;
		JCheckBoxMenuItem _nlabel = new JCheckBoxMenuItem("Labels",
				_renderbox.getRenderSettings().getBoolean(RenderSettings.SHOW_LABELS));
		JCheckBoxMenuItem _stress = new JCheckBoxMenuItem("Stress",
				_renderbox.getRenderSettings().getBoolean(RenderSettings.SHOW_STRESS));
		JCheckBoxMenuItem _length = new JCheckBoxMenuItem("Length",
				_renderbox.getRenderSettings().getBoolean(RenderSettings.SHOW_LENGTH));

		JMenu _filter;
		JMenuItem _degreeFilter = new JMenuItem("Degree");
		JMenuItem _measureFilter = new JMenuItem("Measure");

		// degree filter stuff
		DegreeFilter _degreeFilterObject = new DegreeFilter();

		JCheckBoxMenuItem _errTxt = new JCheckBoxMenuItem("Error Messages", true);
		JCheckBoxMenuItem _outTxt = new JCheckBoxMenuItem("Program Output", true);

		public nmView() {
			super("View");
			_visualization = new JMenu("Visualization");
			_filter = new JMenu("Graph Filters");

			// event listeners
			_nlabel.addItemListener(this);
			_stress.addItemListener(this);
			_length.addItemListener(this);
			_errTxt.addItemListener(this);
			_outTxt.addItemListener(this);
			_degreeFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_topLevel.setFilter(_degreeFilterObject);
					// dialog to get initialization arguments
					// TODO
					JDialog _degreeFilterDialog = DegreeFilterUI.getJDialog(_topLevel);
					if(_degreeFilterDialog != null) {
						_degreeFilterDialog.pack();
						_degreeFilterDialog.show();
					}
				}
			});

			// visualization submenu
			_visualization.add(_nlabel);
			_visualization.add(_stress);
			_visualization.add(_length);

			// filter submenu
			_filter.add(_degreeFilter);
			_filter.add(_measureFilter);

			add(_errTxt);
			add(_outTxt);
			add(new JSeparator());
			add(_visualization);
			add(_filter);
		}

		public void itemStateChanged(ItemEvent e) {
			if (e.getSource().equals(_nlabel)) {
				_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LABELS, _nlabel.getState());
			} else if (e.getSource().equals(_stress)) {
			} else if (e.getSource().equals(_length)) {
				_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LENGTH, _length.getState());
			} else if (e.getSource().equals(_errTxt)) {
				// System.out.print("err " + _errTxt.getState());
				_topLevel.displayErrTextBox(_errTxt.getState());
			} else if (e.getSource().equals(_outTxt)) {
				// System.out.print("out " + _outTxt.getState());
				_topLevel.displayOutTextBox(_outTxt.getState());
			}
		}
	}

	public class nmSettings extends JMenu implements ItemListener {
		JCheckBoxMenuItem _aa = new JCheckBoxMenuItem("Antialias",
				_renderbox.getRenderSettings().getBoolean(RenderSettings.ANTIALIAS));

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
