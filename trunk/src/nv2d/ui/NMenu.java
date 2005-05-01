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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;

import nv2d.graph.filter.DegreeFilter;
import nv2d.render.RenderBox;
import nv2d.render.RenderSettings;

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
		_separatorImporter = new JSeparator();
		_separatorPlugin = new JSeparator();
		
		initComponents();
		
		add(_mods);
		add(_plugin);
		add(_optimize);
		add(_view);
		add(_legend);
		add(_settings);
	}
	
	public void addImporterMenu(JMenu m) {
		_mods.add(m);
	}

	public void addPluginMenu(JMenu m) {
		_plugin.add(m);
	}

	public void resetImporterMenu() {
		_mods.removeAll();
		_mods.add(_modsClear);
		_mods.add(_separatorImporter);
	}

	public void resetPluginMenu() {
		_plugin.removeAll();
		_plugin.add(_pluginLoad);
		_plugin.add(_separatorPlugin);
	}
	
	public void setLegendMenu(final LegendMap map) {
		// grab set of names/attributes
		// alphabetize
		// make menuitem for each name/attribute
		// add listener which grabs legend
		_legend.removeAll();
		_legend.add(_legendDefaultColoring);
		_legend.add(new JSeparator());

		Object [] items = map.datumSet().toArray();
		java.util.Arrays.sort(items);
		for(int j = 0; j < items.length; j++) {
			final String name = ((nv2d.graph.Datum) items[j]).name();
			JMenuItem item = new JMenuItem(name);
			item.setToolTipText("Color vertices according to " + name);
			_legend.add(item);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ColorLegendUI colorLegendUI = new ColorLegendUI(_ctl, map.getLegend(name));
					map.getLegend(name).assignColors();
					// _ctl.getView().addComponent(new ColorLegendUI(map.getLegend(name)), "Legend", ViewInterface.SIDE_PANEL);
					_viewLegendMenuActionPerformed(e, colorLegendUI);
				}
			});
		}
	}
	
	
	private JSeparator _separatorImporter;
	private JSeparator _separatorPlugin;
	
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
	private JCheckBoxMenuItem _viewSidePanel;
	private JMenuItem _viewRenderBox;
	private JMenuItem _viewErrTxt;
	private JMenuItem _viewOutTxt;
	private JMenuItem _viewSaveImage;
	
	private JMenu _legend;
	private JMenuItem _legendDefaultColoring;
	
	private JMenu _settings;
	private JCheckBoxMenuItem _settingsAntialias;
	
	private void initComponents() {
		_legend = new JMenu("Legend");
		_legendDefaultColoring = new JMenuItem("Normal Color Scheme");
		_legendDefaultColoring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_legendDefaultColoringActionPerformed(e);
			}
		});
		_legend.add(_legendDefaultColoring);
		_legend.add(new JSeparator());
		
		// initialize the modules menu
		_mods = new JMenu("Import");
		_modsClear = new JMenuItem("Clear Graph");

		// NOTE: done in resetImporterMenu() now -bs
		// _mods.add(_modsClear);
		// _mods.add(_separatorImporter);
		_modsClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_modsClearActionPerformed(e);
			}
		});

		// initialize the plugins menu
		_plugin = new JMenu("Plugins");
		_pluginLoad = new JMenuItem("Plugin Manager");
	
		// NOTE: done in resetPluginMenu() now -bs
		// _plugin.add(_pluginLoad);
		// _plugin.add(_separatorPlugin);
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
		_viewSidePanel = new JCheckBoxMenuItem("Side Panel", false);
		_viewSouthPanel = new JCheckBoxMenuItem("Bottom Panel", true);
		_viewErrTxt = new JMenuItem("Error Messages");
		_viewOutTxt = new JMenuItem("Program Output");
		_viewRenderBox = new JMenuItem("Graph Visualization");
		_viewSaveImage = new JMenuItem("Save Image...");

		_view.add(_viewSouthPanel);
		_view.add(_viewSidePanel);
		_view.add(_viewRenderBox);
		_view.add(_viewOutTxt);
		_view.add(_viewErrTxt);
		_view.add(new JSeparator());
		_view.add(_viewVis);
		_view.add(_viewFilter);
		// visualization submenu
		_viewVis.add(_viewVLabel);
		_viewVis.add(_viewStress);
		_viewVis.add(_viewLength);
		// filter submenu
		_viewFilter.add(_viewFilterDegree);
		// save image option
		_view.add(new JSeparator());
		_view.add(_viewSaveImage);
		_viewFilterDegree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewFilterDegreeActionPerformed(e);
			}
		});
		_viewSouthPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewBottomPanelActionPerformed(e);
			}
		});
		_viewSidePanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewSidePanelActionPerformed(e);
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
		_viewErrTxt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewErrTxtActionPerformed(e);
			}
		});
		_viewOutTxt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewOutTxtActionPerformed(e);
			}
		});
		_viewRenderBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewRenderBoxActionPerformed(e);
			}
		});
		_viewSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_viewSaveImageActionPerformed(e);
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
		_renderbox.startLayout();
	}

	private void _optimizeStopActionPerformed(ActionEvent e) {
		_renderbox.stopLayout();
	}

	private void _optimizeCenterActionPerformed(ActionEvent e) {
		_renderbox.stopLayout();
		_renderbox.doCenterLayout();
	}

	private void _optimizeResetActionPerformed(ActionEvent e) {
		_renderbox.stopLayout();
		_renderbox.doRandomLayout();
	}

	private void _viewFilterDegreeActionPerformed(ActionEvent e) {
		_ctl.setFilter(_degreeFilter);
		// dialog to get initialization arguments
		// TODO
		JDialog _degreeFilterDialog = DegreeFilterUI.getJDialog(_ctl);
		if(_degreeFilterDialog != null) {
			_degreeFilterDialog.pack();
			_degreeFilterDialog.setVisible(true);
		}
	}

	private void _viewSidePanelActionPerformed(ActionEvent e) {
		_ctl.getView().toggleSidePane(_viewSidePanel.getState());
	}

	private void _viewBottomPanelActionPerformed(ActionEvent e) {
		_ctl.getView().toggleBottomPane(_viewSouthPanel.getState());
	}

	private void _viewVLabelItemStateChanged(ItemEvent e) {
		_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LABELS, _viewVLabel.getState());
	}

	private void _viewOutTxtActionPerformed(ActionEvent e) {
		if(!_ctl.getView().contains(_ctl.getViewFactory().getConsolePane())) {
			_ctl.getView().addComponent(_ctl.getViewFactory().getConsolePane(), "Console", ViewInterface.MAIN_PANEL);
		}
	}

	private void _viewErrTxtActionPerformed(ActionEvent e) {
		if(!_ctl.getView().contains(_ctl.getViewFactory().getErrorPane())) {
			_ctl.getView().addComponent(_ctl.getViewFactory().getErrorPane(), "Errors", ViewInterface.MAIN_PANEL);
		}
	}
	
	private void _viewRenderBoxActionPerformed(ActionEvent e) {
		if(!_ctl.getView().contains(_ctl.getRenderBox())) {
			_ctl.getView().addComponent(_ctl.getRenderBox(), "Main", ViewInterface.MAIN_PANEL);
		}
	}

	private void _viewSaveImageActionPerformed(ActionEvent e) {
	    _renderbox.handleSaveImage();
	}
	
	private void _viewLengthItemStateChanged(ItemEvent e) {
		_renderbox.getRenderSettings().setBoolean(RenderSettings.SHOW_LENGTH, _viewLength.getState());
	}

	private void _viewStressItemStateChanged(ItemEvent e) {
	}
		
	private void _settingsAntialiasItemStateChanged(ItemEvent e) {
		_renderbox.getRenderSettings().setBoolean(RenderSettings.ANTIALIAS, _settingsAntialias.getState());
		_renderbox.setHighQuality(_settingsAntialias.getState());	
	}

	private ColorLegendUI _oldColorLegendUI;
	/**
	 * Use the <code>_oldColorLegendUI</code> to make sure that only one legend
	 * panel is open at any given time.
	 */
	private void _viewLegendMenuActionPerformed(ActionEvent e, ColorLegendUI clu) {
		if(_oldColorLegendUI != null) {
			_ctl.getView().removeComponentNoUpdate(_oldColorLegendUI);
		}
		_ctl.getView().addComponentNoUpdate(clu, "Legend", ViewInterface.SIDE_PANEL);
		_ctl.getView().setComponentVisible(clu);
		_oldColorLegendUI = clu;
		_renderbox.useLegendColoring();
	}
	
	private void _legendDefaultColoringActionPerformed(ActionEvent e) {
		_oldColorLegendUI = null;
		_renderbox.useDefaultColoring();
	}
}
