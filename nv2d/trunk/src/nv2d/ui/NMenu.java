/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
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

/* TODO: complete rewrite using the java forms manager */
public class NMenu extends JMenuBar {
	private RenderBox _renderbox;
	private NController _ctl;
	private PluginManagerUI _pluginManagerUI;
	
	private DegreeFilter _degreeFilter;

	// public NMenu(RenderBox r) {
	public NMenu(NController j, RenderBox r) {
		_ctl = j;
		_renderbox = r;
		
		_pluginManagerUI = new PluginManagerUI(null, _ctl);
		_degreeFilter = new DegreeFilter();
		_separator = new JSeparator();
		
		initComponents();
		
		add(_mods);
		add(_plugin);
		add(_optimize);
		add(_view);
		add(_settings);
	}
	
	public void addImporterMenu(JMenu m) {
		_mods.add(m);
	}

	public void addPluginMenu(JMenu m) {
		_plugin.add(m);
	}
	
	
	private JSeparator _separator;
	
	private JMenu _mods;
	private JMenuItem _modsClear;
	
	private JMenu _plugin;
	private JMenuItem _pluginLoad;
	
	private JMenu _optimize;
	private JMenuItem _optimizeStart;
	private JMenuItem _optimizeStop;
	private JMenuItem _optimizeCenter;
	private JMenuItem _optimizeReset;
	
	private JMenu _view;
	private JCheckBoxMenuItem _viewVLabel;
	private JCheckBoxMenuItem _viewStress;
	private JCheckBoxMenuItem _viewLength;
	private JMenu _viewVis;
	private JMenu _viewFilter;
	private JMenuItem _viewFilterDegree;
	private JCheckBoxMenuItem _viewSouthPanel;
	private JCheckBoxMenuItem _viewHistoryPanel;
	private JCheckBoxMenuItem _viewErrTxt;
	private JCheckBoxMenuItem _viewOutTxt;
	
	private JMenu _settings;
	private JCheckBoxMenuItem _settingsAntialias;
	
	private void initComponents() {
		// initialize the modules menu
		_mods = new JMenu("Import");
		_modsClear = new JMenuItem("Clear Graph");

		_mods.add(_modsClear);
		_mods.add(_separator);
		_modsClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_modsClearActionPerformed(e);
			}
		});

		// initialize the plugins menu
		_plugin = new JMenu("Plugins");
		_pluginLoad = new JMenuItem("Plugin Manager");
		
		_plugin.add(_pluginLoad);
		_plugin.add(_separator);
		_pluginLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_pluginLoadActionPerformed(e);
			}
		});
		
		// initialize the optimization menu
		_optimize = new JMenu("Optimization");
		_optimizeStart = new JMenuItem("Start");
		_optimizeStop = new JMenuItem("Stop");
		_optimizeCenter = new JMenuItem("Center");
		_optimizeReset= new JMenuItem("Reset");
		
		_optimize.add(_optimizeStart);
		_optimize.add(_optimizeStop);
		_optimize.add(_optimizeCenter);
		_optimize.add(_optimizeReset);
		
		_optimizeStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_optimizeStartActionPerformed(e);
			}
		});
		_optimizeStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_optimizeStopActionPerformed(e);
			}
		});
		_optimizeCenter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_optimizeCenterActionPerformed(e);
			}
		});
		_optimizeReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_optimizeResetActionPerformed(e);
			}
		});
		
		_view = new JMenu("View");
		_viewVLabel = new JCheckBoxMenuItem("Labels", _renderbox.getRenderSettings().getBoolean(RenderSettings.SHOW_LABELS));
		_viewStress = new JCheckBoxMenuItem("Stress", _renderbox.getRenderSettings().getBoolean(RenderSettings.SHOW_STRESS));
		_viewLength = new JCheckBoxMenuItem("Length", _renderbox.getRenderSettings().getBoolean(RenderSettings.SHOW_LENGTH));
		_viewVis = new JMenu("Visualization");
		_viewFilter = new JMenu("Graph Filters");
		_viewFilterDegree = new JMenuItem("Degree Filter");
		_viewHistoryPanel = new JCheckBoxMenuItem("History", false);
		_viewSouthPanel = new JCheckBoxMenuItem("Bottom Control Panel", true);
		_viewErrTxt = new JCheckBoxMenuItem("Error Messages", true);
		_viewOutTxt = new JCheckBoxMenuItem("Program Output", true);

		_view.add(_viewSouthPanel);
		_view.add(_viewHistoryPanel);
		_view.add(_viewErrTxt);
		_view.add(_viewOutTxt);
		_view.add(_separator);
		_view.add(_viewVis);
		_view.add(_viewFilter);
		// visualization submenu
		_viewVis.add(_viewVLabel);
		_viewVis.add(_viewStress);
		_viewVis.add(_viewLength);
		// filter submenu
		_viewFilter.add(_viewFilterDegree);
		_viewFilterDegree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewFilterDegreeActionPerformed(e);
			}
		});
		_viewSouthPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewSouthPanelActionPerformed(e);
			}
		});
		_viewHistoryPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewHistoryPanelActionPerformed(e);
			}
		});
		_viewVLabel.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				_viewVLabelItemStateChanged(e);
			}
		});
		_viewStress.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				_viewStressItemStateChanged(e);
			}
		});
		_viewLength.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				_viewLengthItemStateChanged(e);
			}
		});
		_viewErrTxt.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				_viewErrTxtItemStateChanged(e);
			}
		});
		_viewOutTxt.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				_viewOutTxtItemStateChanged(e);
			}
		});
		
		// initialize settings menu
		_settings = new JMenu("Settings");
		_settingsAntialias = new JCheckBoxMenuItem("Antialias", _renderbox.getRenderSettings().getBoolean(RenderSettings.ANTIALIAS));
		
		_settings.add(_settingsAntialias);
		_settingsAntialias.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				_settingsAntialiasItemStateChanged(e);
			}
		});
	}

	private void _modsClearActionPerformed(ActionEvent e) {
		_renderbox.clear();
	}

	private void _pluginLoadActionPerformed(ActionEvent e) {
		_pluginManagerUI.initContent();
		_pluginManagerUI.setVisible(true);
	}

	private void _optimizeStartActionPerformed(ActionEvent e) {
		_renderbox.startForceDirectedLayout();
	}

	private void _optimizeStopActionPerformed(ActionEvent e) {
		_renderbox.stopForceDirectedLayout();
	}

	private void _optimizeCenterActionPerformed(ActionEvent e) {
		_renderbox.stopForceDirectedLayout();
		_renderbox.doCenterLayout();
	}

	private void _optimizeResetActionPerformed(ActionEvent e) {
		_renderbox.stopForceDirectedLayout();
		_renderbox.doRandomLayout();
	}

	private void _viewFilterDegreeActionPerformed(ActionEvent e) {
		_ctl.setFilter(_degreeFilter);
		// dialog to get initialization arguments
		// TODO
		JDialog _degreeFilterDialog = DegreeFilterUI.getJDialog(_ctl);
		if(_degreeFilterDialog != null) {
			_degreeFilterDialog.pack();
			_degreeFilterDialog.show();
		}
	}

	private void _viewSouthPanelActionPerformed(ActionEvent e) {
		_ctl.displayBottomPane(_viewSouthPanel.getState());
	}

	private void _viewVLabelItemStateChanged(ItemEvent e) {
		_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LABELS, _viewVLabel.getState());
	}

	private void _viewOutTxtItemStateChanged(ItemEvent e) {
		_ctl.displayOutTextBox(_viewOutTxt.getState());
	}

	private void _viewErrTxtItemStateChanged(ItemEvent e) {
		_ctl.displayErrTextBox(_viewErrTxt.getState());
	}

	private void _viewLengthItemStateChanged(ItemEvent e) {
		_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LENGTH, _viewLength.getState());
	}

	private void _viewStressItemStateChanged(ItemEvent e) {
	}

	private void _viewHistoryPanelActionPerformed(ActionEvent e) {
		_ctl.getHistoryPane().setVisible(_viewHistoryPanel.getState());
		_ctl.getWindow().validate();
	}
		
	private void _settingsAntialiasItemStateChanged(ItemEvent e) {
		_renderbox.getRenderSettings().setBoolean(RenderSettings.ANTIALIAS, _settingsAntialias.getState());
		_renderbox.setHighQuality(_settingsAntialias.getState());	
	}
}
