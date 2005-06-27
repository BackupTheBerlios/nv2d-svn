package nv2d.plugins.standard.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.lang.Math;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.border.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.metal.MetalLookAndFeel;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.plugins.NV2DPlugin;
import nv2d.plugins.NPluginManager;
import nv2d.plugins.standard.SNA;
import nv2d.render.PGraph;
import nv2d.render.PNode;
import nv2d.render.RenderBox;
import nv2d.ui.NController;
import nv2d.ui.ViewInterface;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.action.animate.ColorAnimator;
import edu.berkeley.guir.prefuse.action.animate.PolarLocationAnimator;
import edu.berkeley.guir.prefuse.action.animate.LocationAnimator;
import edu.berkeley.guir.prefuse.action.animate.SizeAnimator;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.action.filter.TreeFilter;
import edu.berkeley.guir.prefusex.force.CircularWallForce;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.Force;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.FruchtermanReingoldLayout;
import edu.berkeley.guir.prefusex.layout.GridLayout;
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * Graph Layout Plugin
 * 
 * Adds GUI and Backend functionality for layout out Graphs.  Adds
 * a side-bar panel to the GUI which enables selection and customization
 * of graph layout algorithms.
 * 
 * Repository:
 *   - HashMap of Layouts for the current graph
 *   - HashMap of LayoutActionLists for the current graphs
 * 
 * @author sam
 */
public class LayoutPlugin implements NV2DPlugin {

    // --- N2 Components ---
    private NController _control;
	private Container _view;
	private RenderBox _renderbox;
	
	// --- GUI Components ---
	private JMenu _layoutMenu;
	private JPanel _sideBarPanel;
	private LayoutChooserPanel _layoutChooserPanel;
	private JPanel _layoutSettingsPanel;
	private LayoutViewPanel _layoutViewPanel;
	private Color _backgroundColor;
	
	// --- Layout Components ---
	private HashMap _layouts;
	private HashMap _layoutLists;
	
	public static final int FRUCHREIN_NUM_ITERS = 100;
	public static final String MEASURES_PLUGIN = "SNA";

	public static final String LAYOUT_ForceDir = "Force Directed";
	public static final String LAYOUT_WForceDir = "Weighted Force Directed";
	public static final String LAYOUT_FruchRein = "Fruchterman Reingold";
	public static final String LAYOUT_RadTree = "Radial Tree";
	public static final String LAYOUT_Circle = "Circular";
	public static final String LAYOUT_Grid = "Grid";	
	public static final String LAYOUT_SimAnneal = "Simulated Annealing";
	public static final String LAYOUT_Random = "Random";
	public static final String LAYOUT_FRplusFD = "FR+FD Pipeline";
	public static final String LAYOUT_KamKawai = "Kamada Kawai";
	public static final String LAYOUT_ISOM = "ISOM";
	public static final String LAYOUT_TEST = "TEST";

	public static final String ACT_FORCE_DIRECTED = "layout_ForceDirectedLayout";
	public static final String ACT_WEIGHTED_FORCE_DIRECTED = "layout_WeightedForceDirectedLayout";
	public static final String ACT_FRUCHTERMAN_REINGOLD = "layout_FruchtermanReingoldLayout";
	public static final String ACT_CIRCLE = "layout_CircleLayout";
	public static final String ACT_SIMULATED_ANNEALING = "layout_SimulatedAnnealingLayout";	
	public static final String ACT_PIPELINE = "layout_Pipeline";
	public static final String ACT_RADIAL_TREE = "layout_RadialTree";
	public static final String ACT_GRID = "layout_Grid";
	public static final String ACT_KAMADA_KAWAI = "layout_KamadaKawai";
	public static final String ACT_ISOM = "layout_ISOM";
	public static final String ACT_TEST = "layout_TEST";
	
	public static final String STR_LABEL_VIEWPANEL = "View Settings";
	public static final String STR_LABEL_SETTINGSPANEL = "Layout Settings";
	public static final String STR_LABEL_FORCEDIR_SETTINGS = "Force-Directed Layout Settings";
	public static final String STR_LABEL_WFORCEDIR_SETTINGS = "Force-Directed Layout Settings";
	public static final String STR_LABEL_CIRCLE_SETTINGS = "Circle Layout Settings";
	public static final String STR_LABEL_FRUCHREIN_SETTINGS = "Fruchterman Layout Settings";
	public static final String STR_LABEL_CHOOSERPANEL = "Layout";
	public static final String STR_LABEL_PAN_ZOOM = "<html><p>Pan/Zoom graph:</p><p>&nbsp;&nbsp;- Left button pans</p><p>&nbsp;&nbsp;- Right button zooms</p>";
	public static final String STR_LABEL_ROTATE = "Rotate graph with Right button";
	public static final String STR_LABEL_FITVIEW = "Fit graph to window";
	public static final String STR_LABEL_RESIZE_NODES = "Reset node size";
	public static final String STR_LABEL_ANIMATE = "Animate graph transitions";
	public static final String STR_LABEL_KEEP_IN_BOUNDS = "Keep layout in visible window";
	public static final String STR_LABEL_ZOOM_ONE = "Zoom 1:1";
	
	public static final String IMG_NAME_ROTATE = "rotate";
	public static final String IMG_NAME_PAN_ZOOM = "panzoom";
	public static final String IMG_NAME_FITVIEW = "fitview";
	public static final String IMG_NAME_CIRCLE = "circle";
	public static final String IMG_NAME_FORCEDIR = "forcedir";
	public static final String IMG_NAME_WFORCEDIR = "wforcedir";
	public static final String IMG_NAME_FRUCHREIN = "fruchrein";
	public static final String IMG_NAME_RESIZE_NODES = "resizenodes";
	public static final String IMG_NAME_ANIMATE = "animate";
	public static final String IMG_NAME_KEEP_IN_BOUNDS = "bounds";
	public static final String IMG_NAME_ZOOM_ONE = "zoomOne";
	
	public static final String STR_SORT_ALPHABETICAL = "Alphabetize";

	private static boolean DEBUG = false;
	
	/**
	 * Constructor
	 */
	public LayoutPlugin() {
	    _layouts = new HashMap();
	    _layoutLists = new HashMap();
	} //
	

	/**
	 * Initialize - called once after plugin is loaded
	 */
	public void initialize(Graph g, Container view, NController control) {
	    if(DEBUG) {System.out.println("Layout INIT");}
	    _control = control;
		_view = view;
		_renderbox = control.getRenderBox();
		initLayouts();

		// --- init side panel and add to GUI ---
		_sideBarPanel = new JPanel();
        _sideBarPanel.setLayout(new BorderLayout());
        _sideBarPanel.setPreferredSize(new Dimension(190, 400));
        _sideBarPanel.setMinimumSize(new Dimension(120, 100));
        _sideBarPanel.setMaximumSize(new Dimension(190, 1500));

        // TODO - fix defaults here
		_layoutViewPanel = new LayoutViewPanel(this);
		_layoutViewPanel.setPreferredSize(new Dimension(190, 90));
		_layoutViewPanel.setMinimumSize(new Dimension(110, 30));
		_layoutViewPanel.setMaximumSize(new Dimension(190, 90));

		_layoutChooserPanel = new LayoutChooserPanel(this);
		_layoutChooserPanel.setPreferredSize(new Dimension(190, 30));
		_layoutChooserPanel.setMinimumSize(new Dimension(110, 20));
		_layoutChooserPanel.setMaximumSize(new Dimension(190, 30));

		_layoutSettingsPanel = new LayoutSettingsPanel(); //this, _fsim, true, true, true);
		_layoutSettingsPanel.setPreferredSize(new Dimension(190, 600));
		_layoutSettingsPanel.setMinimumSize(new Dimension(190, 300));
		_layoutSettingsPanel.setMaximumSize(new Dimension(190, 1000));
		
		_sideBarPanel.add(_layoutViewPanel, BorderLayout.NORTH);
		_sideBarPanel.add(_layoutChooserPanel, BorderLayout.CENTER);
		_sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);

		_control.getView().addComponent(_sideBarPanel, "Layout", ViewInterface.SIDE_PANEL);
			
	} // -- end initialize
	
	
	/**
	 * ReloadAction - called each time a new graph is loaded.
	 */
	public void reloadAction(Graph g) {
	    if(DEBUG) {System.out.println("LAYOUT RELOAD");}
	    // --- reset panel, view, and layout settings --
	    _layoutViewPanel.setSelected(RenderBox.VIEW_MODE_PAN_ZOOM);
	    setView(RenderBox.VIEW_MODE_PAN_ZOOM);

        _sideBarPanel.remove(_layoutSettingsPanel);
	    _layoutSettingsPanel = new LayoutSettingsPanel(this, ((ForceDirectedLayout)_layouts.get(LAYOUT_ForceDir)).getForceSimulator(), true, true, true);
        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
        _sideBarPanel.validate();

	    _layoutChooserPanel.setSelected(LAYOUT_ForceDir);
	    setLayout(ACT_FORCE_DIRECTED, false); //TODO - changed LAYOUT_ForceDir, false);

	    if(DEBUG) {System.out.println("Layout Panel Size:" + _sideBarPanel.getSize());}
	} //
	
	public RenderBox getRenderBox() {
	    return _renderbox;
	}
	
	
	/**
	 * AddSideBarToController - adds the side bar to the GUI and
	 * makes it visible.
	 */
	public void addSideBarToController() {
	    // -- add Component to SideBar --
	    // NOTE: ViewInterface makes sure that it is only added once
	    // don't need to check .contains()
	    _control.getView().addComponent(_sideBarPanel, "Layout", ViewInterface.SIDE_PANEL);
		_control.getView().setComponentVisible(_sideBarPanel);
	}
	
	/**
	 * GetRegistry - returns the ItemRegistry correpsonding to the
	 * current graph in renderbox.
	 */
	public ItemRegistry getRegistry() {
	    return _renderbox.getRegistry();
	} //
		

	/**
	 * Menu - returns the Menu for this plugin.
	 */
	public JMenu menu() {
		_layoutMenu = new JMenu("Layout");
		
		// --- set menuitems for layout types ---
		final JMenuItem view_panel = new JMenuItem("Show Layout Panel");
		view_panel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    addSideBarToController();
			}
		});
		_layoutMenu.add(view_panel);
		_layoutMenu.addSeparator();
		final JMenuItem l_forceDir = new JMenuItem(LAYOUT_ForceDir);
		l_forceDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_ForceDir);
			    // TODO changed: setLayout(LAYOUT_ForceDir, true);
			    setLayout(ACT_FORCE_DIRECTED, true);
			}
		});
		_layoutMenu.add(l_forceDir);
		l_forceDir.setSelected(true);
		final JMenuItem l_wForceDir = new JMenuItem(LAYOUT_WForceDir); 
		l_wForceDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_WForceDir);
			    setLayout(LAYOUT_WForceDir, true);
			}
		});
		_layoutMenu.add(l_wForceDir);
		final JMenuItem l_fruchRein = new JMenuItem(LAYOUT_FruchRein);
		l_fruchRein.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_FruchRein);
			    setLayout(LAYOUT_FruchRein, true);
			}
		});
		_layoutMenu.add(l_fruchRein);
		final JMenuItem l_circle = new JMenuItem(LAYOUT_Circle);
		l_circle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_Circle);
			    setLayout(LAYOUT_Circle, true);
			}
		});
		_layoutMenu.add(l_circle);
		
		// TODO
		final JMenuItem l_sa = new JMenuItem(LAYOUT_SimAnneal);
		l_sa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_SimAnneal);
			    setLayout(LAYOUT_SimAnneal, true);
			}
		});
		_layoutMenu.add(l_sa);
		
		final JMenuItem l_rt = new JMenuItem(LAYOUT_RadTree);
		l_rt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_RadTree);
			    setLayout(LAYOUT_RadTree, true);
			}
		});
		_layoutMenu.add(l_rt);
		
		final JMenuItem l_grid = new JMenuItem(LAYOUT_Grid);
		l_grid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_Grid);
			    setLayout(LAYOUT_Grid, true);
			}
		});
		_layoutMenu.add(l_grid);
		
		final JMenuItem l_pi = new JMenuItem(LAYOUT_FRplusFD);
		l_pi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_FRplusFD);
			    setLayout(LAYOUT_FRplusFD, true);
			}
		});
		_layoutMenu.add(l_pi);
		// TODO
		
		final JMenuItem l_test = new JMenuItem(LAYOUT_TEST);
		l_test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _layoutChooserPanel.setSelected(LAYOUT_TEST);
			    setLayout(LAYOUT_TEST, true);
			}
		});
		_layoutMenu.add(l_test);
		
		return _layoutMenu;
	} // -- end menu

	   
	public void setLayout(String name, boolean start) {
	    setLayout(name, start, false);
	}
	
	/**
	 * SetLayout - sets the current layout in renderbox.  If
	 * 
	 * start - set true if the layout should run immediately 
	 */
	public void setLayout(String name, boolean start, boolean requirePanelUpdate) {
	    String currentLayout = _renderbox.getCurrentLayoutName();
//	    if(DEBUG) {System.out.println("Setting Layout: " + name + " current is: " + currentLayout);}
	    if(DEBUG) {System.out.println("SET LAYOUT: " + name + " " + start + " " + requirePanelUpdate);}
	    
	    // if the current layout is Radial Tree, we must remove the 
	    // Focus Control before setting a new layout
	    if(currentLayout != null && currentLayout.equals(ACT_RADIAL_TREE)) {
	        _renderbox.removeFocusControl();
	    }
	    
	    // --- Force-Directed Layout ---
	    // TODO, clean these OR's up
	    if (name.equals(ACT_FORCE_DIRECTED) || name.equals(LAYOUT_ForceDir)) {
            _renderbox.addActivity(ACT_FORCE_DIRECTED, (LayoutActionList)_layoutLists.get(LAYOUT_ForceDir));
		    _renderbox.setActiveLayout(ACT_FORCE_DIRECTED);
		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    // if the layout just changed, update sidebar with new corresponding panels
		    if((currentLayout != null && !currentLayout.equals(ACT_FORCE_DIRECTED)) || requirePanelUpdate) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        _layoutSettingsPanel = new LayoutSettingsPanel(this, ((ForceDirectedLayout)_layouts.get(LAYOUT_ForceDir)).getForceSimulator(), true, true, true);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
		        _sideBarPanel.validate();
		    }
	    }
	    // --- Weighted Force-Directed Layout ---
	    else if (name.equals(ACT_WEIGHTED_FORCE_DIRECTED) || name.equals(LAYOUT_WForceDir)) {
            _renderbox.addActivity(ACT_WEIGHTED_FORCE_DIRECTED, (LayoutActionList)_layoutLists.get(LAYOUT_WForceDir));
		    _renderbox.setActiveLayout(ACT_WEIGHTED_FORCE_DIRECTED);
		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if((currentLayout != null && !currentLayout.equals(ACT_WEIGHTED_FORCE_DIRECTED)) || requirePanelUpdate) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        _layoutSettingsPanel = new LayoutSettingsPanel(this, ((WeightedForceDirectedLayout)_layouts.get(LAYOUT_WForceDir)).getForceSimulator(), true, true, true);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
		        _sideBarPanel.validate();
		    }
	    }
	    // --- Fruchterman Reingold Layout ---
	    else if (name.equals(ACT_FRUCHTERMAN_REINGOLD) || name.equals(LAYOUT_FruchRein)) {
		    _renderbox.addActivity(ACT_FRUCHTERMAN_REINGOLD, (LayoutActionList)_layoutLists.get(LAYOUT_FruchRein));
		    _renderbox.setActiveLayout(ACT_FRUCHTERMAN_REINGOLD);
		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if((currentLayout != null && !currentLayout.equals(ACT_FRUCHTERMAN_REINGOLD)) || requirePanelUpdate) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        _layoutSettingsPanel = new FRSettingsPanel(this);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
		        _sideBarPanel.validate();
		    }
	    }
   	    // --- Circle Layout ---
	    else if (name.equals(ACT_CIRCLE) || name.equals(LAYOUT_Circle)) {
		    _renderbox.addActivity(ACT_CIRCLE, (LayoutActionList)_layoutLists.get(LAYOUT_Circle));
		    _renderbox.setActiveLayout(ACT_CIRCLE);
		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if((currentLayout != null && !currentLayout.equals(ACT_CIRCLE)) || requirePanelUpdate) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        SNA sna = (SNA)_control.getPluginManager().getNV2DPlugin("SNA");
			    _layoutSettingsPanel = new CircleSettingsPanel(this, sna);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
		        _sideBarPanel.validate();

		    }
	    }
	    // --- Simulated Layout ---
	    else if (name.equals(ACT_SIMULATED_ANNEALING) || name.equals(LAYOUT_SimAnneal)) {
	        if(DEBUG) {System.out.println("Choosing Simulated Annealing");}
		    _renderbox.addActivity(ACT_SIMULATED_ANNEALING, (LayoutActionList)_layoutLists.get(LAYOUT_SimAnneal));
		    _renderbox.setActiveLayout(ACT_SIMULATED_ANNEALING);
		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if((currentLayout != null && !currentLayout.equals(ACT_SIMULATED_ANNEALING)) || requirePanelUpdate) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        _layoutSettingsPanel = new FRSettingsPanel(this);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
		        _sideBarPanel.validate();
		    }
	    }
	    // --- Radial Tree Layout ---
	    else if (name.equals(ACT_RADIAL_TREE) || name.equals(LAYOUT_RadTree)) {
	        LayoutActionList rt_actions = (LayoutActionList)_layoutLists.get(LAYOUT_RadTree);
		    _renderbox.addActivity(ACT_RADIAL_TREE, rt_actions);
		    _renderbox.addFocusControl(rt_actions);
		    _renderbox.setActiveLayout(ACT_RADIAL_TREE);
		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if((currentLayout != null && !currentLayout.equals(ACT_RADIAL_TREE)) || requirePanelUpdate) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        // TODO - make sidepanel
		        _layoutSettingsPanel = new FRSettingsPanel(this);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
		        _sideBarPanel.validate();
		    }
	    }
	    // --- Grid Layout ---
	    else if (name.equals(ACT_GRID) || name.equals(LAYOUT_Grid)) {
		    _renderbox.addActivity(ACT_GRID, (LayoutActionList)_layoutLists.get(LAYOUT_Grid));
		    _renderbox.setActiveLayout(ACT_GRID);
		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if((currentLayout != null && !currentLayout.equals(ACT_GRID)) || requirePanelUpdate) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        // TODO - make sidepanel
		        _layoutSettingsPanel = new FRSettingsPanel(this);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
		        _sideBarPanel.validate();
		    }
	    }
	    
	    // --- Pipeline FR+FD Layout ---
	    else if (name.equals(ACT_PIPELINE) || name.equals(LAYOUT_FRplusFD)) {
		    _renderbox.addActivity(ACT_PIPELINE, (LayoutActionList)_layoutLists.get(LAYOUT_FRplusFD));
		    _renderbox.setActiveLayout(ACT_PIPELINE);
		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if((currentLayout != null && !currentLayout.equals(ACT_PIPELINE)) || requirePanelUpdate) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        // TODO - make FRplusFD Settings Panel
		        _layoutSettingsPanel = new FRSettingsPanel(this);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
		        _sideBarPanel.validate();
		    }
	    }
//	    // --- TEST Layout ---
//	    else if (name.equals(ACT_TEST) || name.equals(LAYOUT_TEST)) {
//		    _renderbox.addActivity(ACT_TEST, (LayoutActionList)_layoutLists.get(LAYOUT_TEST));
//		    _renderbox.setActiveLayout(ACT_TEST);
//		    _renderbox.updateCurrentLayoutBounds(_renderbox.getBounds(), false);
//		    if(start) {
//		        _renderbox.startLayout();
//		    }
//
//		    if((currentLayout != null && !currentLayout.equals(ACT_TEST)) || requirePanelUpdate) {
//		        _sideBarPanel.remove(_layoutSettingsPanel);
//		        // TODO - make sidepanel
//		        _layoutSettingsPanel = new FRSettingsPanel(this);
//		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.SOUTH);
//		        _sideBarPanel.validate();
//		    }
//	    }
	    
	    
	} // -- end setLayout
	
	
	public void jiggleLayout(Force nBodyForce, int gravIndex, Force springForce, int springIndex) {
	    if(DEBUG) {System.out.println("Jiggling Layout");}
	    // make action list that
	    // sets nbody very low
	    // then sets springiness very high
	    
		// -- create LayoutActionList --
	    JiggleAction jiggle = new JiggleAction(nBodyForce, gravIndex, springForce, springIndex);
		ActionList actions = new ActionList(_renderbox.getItemRegistry(), 1000, 20);
		actions.add(jiggle);
//		actions.add(new GraphFilter());
//		actions.add(new RepaintAction());
		actions.runNow();
	}
	
	
	/**
	 * SetView - sets the view mode in renderbox.
	 */
	public void setView(String view) {
	    _renderbox.setViewMode(view);
	} //
	
	
	public void fitGraphToWindow() {
	    _renderbox.fitGraphToWindow();
	}
	
	public void setAnimateLayout(boolean b) {
	    // if the animation setting is changing, then all
	    // LayoutActionLists must be regenerated and
	    // resent to the RenderBox
	    if(b != _renderbox.getAnimateLayout()) {
		    _renderbox.setAnimateLayout(b);
	        initLayouts();
	        setLayout(_renderbox.getCurrentLayoutName(), false, true);
	    }
	}
	
	public void setEnforceBounds(boolean b) {
	    // if enforceBounds changes, then all
	    // LayoutActionLists must be regenerated and
	    // resent to the RenderBox
	    
	    if(DEBUG) {System.out.println("Set EnforceBounds:" + b);}
	    if(b != _renderbox.getEnforceBounds()) {
	        if(DEBUG) {System.out.println(" - setting in renderbox");}
	        _renderbox.setEnforceBounds(b);
	        if(DEBUG) {System.out.println(" - initing layouts");}
	        initLayouts();
	        
	        if(DEBUG) {System.out.println(" - determining restart");}
	        boolean restartLayout = false;
	        if(_renderbox.isRunningActivity()) {
	            _renderbox.stopLayout();
	            restartLayout = true;
	        }
	        setLayout(_renderbox.getCurrentLayoutName(), restartLayout, true);
	    }
	}

	
	
	
	
	/**
	 * InitLayouts
	 */
	public void initLayouts() {
	    // Create Layouts and LayoutActionLists for each layout
	    // and populate the HashMaps
	    
	    boolean enforceBounds = _renderbox.getEnforceBounds();
	    boolean animateLayout = _renderbox.getAnimateLayout();

	    _layoutLists.put(LAYOUT_ForceDir, newForceDirLayoutList(enforceBounds));
	    _layoutLists.put(LAYOUT_WForceDir, newWForceDirLayoutList(enforceBounds));
	    _layoutLists.put(LAYOUT_FruchRein, newFruchReinLayoutList(enforceBounds, animateLayout));
	    _layoutLists.put(LAYOUT_Circle, newCircleLayoutList(enforceBounds, animateLayout));
	    _layoutLists.put(LAYOUT_SimAnneal, newSimAnnealLayoutList(enforceBounds, animateLayout));
	    _layoutLists.put(LAYOUT_RadTree, newRadialTreeLayoutList(enforceBounds, animateLayout));
	    _layoutLists.put(LAYOUT_Grid, newGridLayoutList(enforceBounds, animateLayout));
	    // TODO
//	    _layoutLists.put(LAYOUT_FRplusFD, newFRplusFDLayoutList(enforceBounds, animateLayout));
	    // TODO
	    
//	    _layouts.put(LAYOUT_ForceDir, new ForceDirectedLayout(_fsim, _enforceBounds, false));
//	    _layouts.put(LAYOUT_WForceDir, new WeightedForceDirectedLayout(_fsim, _enforceBounds, false));
//	    _layouts.put(LAYOUT_FruchRein, new FruchtermanReingoldLayout(FRUCHREIN_NUM_ITERS));
//	    _layouts.put(LAYOUT_Circle, new SmartCircleLayout(_control.getPluginManager().getNV2DPlugin(MEASURES_PLUGIN)));   
	}

	
	/**
	 * GetLayout - returns a Layout object, or null if not found.
	 */
	public Layout getLayout(String name) {
	    return (Layout)_layouts.get(name);
	}
	
	

	/**
	 * Force Directed Action List - returns a new LayoutActionList Object
	 * for the Force-Directed Layout.
	 * 
	 * Creates a ForceDirectedLayout and places it in _layouts.
	 * 
	 * NOTE: FD Layout ignores the animateLayout flag, as it animates by default.
	 */
	public LayoutActionList newForceDirLayoutList(boolean enforceBounds) {//, boolean animateLayout) {
	    // -- create a force simulator --
	    ForceSimulator fsim = new ForceSimulator();
		fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
		fsim.addForce(new SpringForce(4E-5f, 75f));
		fsim.addForce(new DragForce(-0.005f));
		fsim.addForce(new CircularWallForce(0.1f, 0.1f, 0.1f));
		if(enforceBounds) {
			fsim.addForce(_renderbox.getLeftBoundaryForce());
			fsim.addForce(_renderbox.getRightBoundaryForce());
			fsim.addForce(_renderbox.getBottomBoundaryForce());
			fsim.addForce(_renderbox.getTopBoundaryForce());
		}
		
		// -- create layout and set bounds if enabled --
		Layout layout = new ForceDirectedLayout(fsim, enforceBounds, false);
		if(enforceBounds) {
			// --- enforce bounds and add a force to repel away from walls ---
		    Rectangle r = _renderbox.getAbsRectangle(_renderbox.getBounds());
			r.grow(-1*RenderBox.DISPLAY_BOUNDARY_CUSHION, -1*RenderBox.DISPLAY_BOUNDARY_CUSHION);
//			updateBoundaryForces(r);
			layout.setLayoutBounds(r);
		}
		_layouts.put(LAYOUT_ForceDir, layout);
		
		// -- create LayoutActionList --
		LayoutActionList actions = new LayoutActionList(_renderbox.getItemRegistry(), -1, 20);
		actions.add(new GraphFilter());
		actions.add(new RepaintAction());
		actions.setLayout(layout);

		return actions;
	} // -- end newActionList_ForceDir

	
//	/**
//	 * Force Directed Action List - returns a new LayoutActionList Object
//	 * for the Force-Directed Layout.
//	 * 
//	 * Creates a ForceDirectedLayout and places it in _layouts.
//	 */
//	public LayoutActionList newWForceDirLayoutList(boolean enforceBounds) {
//	    // -- create a force simulator --
//	    ForceSimulator fsim = new ForceSimulator();
//		fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
//		fsim.addForce(new SpringForce(4E-5f, 75f));
//		fsim.addForce(new DragForce(-0.005f));
//		fsim.addForce(new CircularWallForce(0.1f, 0.1f, 0.1f));
//		if(enforceBounds) {
//			fsim.addForce(_renderbox.getLeftBoundaryForce());
//			fsim.addForce(_renderbox.getRightBoundaryForce());
//			fsim.addForce(_renderbox.getBottomBoundaryForce());
//			fsim.addForce(_renderbox.getTopBoundaryForce());
//		}
//		
//		// -- create layout and set bounds if enabled --
//		Layout layout = new WeightedForceDirectedLayout(fsim, enforceBounds, false);
//		if(enforceBounds) {
//			// --- enforce bounds and add a force to repel away from walls ---
//		    Rectangle r = _renderbox.getAbsRectangle(_renderbox.getBounds());
//			r.grow(-1*RenderBox.DISPLAY_BOUNDARY_CUSHION, -1*RenderBox.DISPLAY_BOUNDARY_CUSHION);
////			updateBoundaryForces(r);
//			layout.setLayoutBounds(r);
//		}
//		_layouts.put(LAYOUT_WForceDir, layout);
//		
//		// -- create LayoutActionList --
//		LayoutActionList actions = new LayoutActionList(_renderbox.getItemRegistry(), -1, 20);
//		actions.add(new GraphFilter());
//		actions.add(new RepaintAction());
//		actions.setLayout(layout);
//
//		return actions;
//	} // -- end newActionList_WForceDir
//	

	/**
	 * Force Directed Action List - returns a new LayoutActionList Object
	 * for the Force-Directed Layout.
	 * 
	 * Creates a ForceDirectedLayout and places it in _layouts.
	 */
	public LayoutActionList newWForceDirLayoutList(boolean enforceBounds) {
	    // -- create a force simulator --
	    ForceSimulator fsim = new ForceSimulator();
		fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
		fsim.addForce(new SpringForce(4E-5f, 75f));
		fsim.addForce(new DragForce(-0.005f));
		fsim.addForce(new CircularWallForce(0.1f, 0.1f, 0.1f));
		//fsim.addForce(new WallForce());
		
		fsim.setSpeedLimit(0.1f);
		if(enforceBounds) {
			fsim.addForce(_renderbox.getLeftBoundaryForce());
			fsim.addForce(_renderbox.getRightBoundaryForce());
			fsim.addForce(_renderbox.getBottomBoundaryForce());
			fsim.addForce(_renderbox.getTopBoundaryForce());
		}
		
		// -- create layout and set bounds if enabled --
		Layout layout = new WeightedForceDirectedLayout(fsim, enforceBounds, false);
		if(enforceBounds) {
			// --- enforce bounds and add a force to repel away from walls ---
		    Rectangle r = _renderbox.getAbsRectangle(_renderbox.getBounds());
			r.grow(-1*RenderBox.DISPLAY_BOUNDARY_CUSHION, -1*RenderBox.DISPLAY_BOUNDARY_CUSHION);
//			updateBoundaryForces(r);
			layout.setLayoutBounds(r);
		}
		_layouts.put(LAYOUT_WForceDir, layout);
		
		// -- create LayoutActionList --
		LayoutActionList actions = new LayoutActionList(_renderbox.getItemRegistry(), -1, 20);
		actions.add(new GraphFilter());
		actions.add(new RepaintAction());
		actions.setLayout(layout);

		return actions;
	} // -- end newActionList_WForceDir
	
	
	/**
	 * Fruchterman Reingold ActionList - returns a new LayoutActionList
	 * Object for the Fruchterman Reingold Layout.
	 */
	public LayoutActionList newFruchReinLayoutList(boolean enforceBounds, boolean animateLayout) {
	    // -- create Layout --
	    Layout layout = new FruchtermanReingoldLayout(FRUCHREIN_NUM_ITERS);
		if(enforceBounds) {
		    Rectangle r = _renderbox.getAbsRectangle(_renderbox.getBounds());
			r.grow(-1*RenderBox.DISPLAY_BOUNDARY_CUSHION, -1*RenderBox.DISPLAY_BOUNDARY_CUSHION);
			layout.setLayoutBounds(r);
		}
	    _layouts.put(LAYOUT_FruchRein, layout);
	    
	    // -- create LayoutActionList --
	    LayoutActionList a;
		if(animateLayout) {
		    if(DEBUG) {System.out.println("Adding Animated FR");}
		    a = new LayoutActionList(_renderbox.getItemRegistry(), 1000, 20);
			a.add(new GraphFilter());
			a.add(new RepaintAction());
			a.setLayout(layout);
		    a.add(new LocationAnimator());
		    a.setPacingFunction(new SlowInSlowOutPacer());
		}
		else {
		    if(DEBUG) {System.out.println("DOING NOTHING");}
		    a = new LayoutActionList(_renderbox.getItemRegistry());
			a.add(new GraphFilter());
			a.add(new RepaintAction());
			a.setLayout(layout);
		}

		return a;
		
		//TODO - temp test
		//LayoutActionList b = (LayoutActionList)_layoutLists.get(LAYOUT_ForceDir);
		//b.alwaysRunAfter(a);
	} //
	
	
	/**
	 * Circle Graph ActionList - returns a new LayoutActionList Object
	 * for the Circle Layout.
	 */
	public LayoutActionList newCircleLayoutList(boolean enforceBounds, boolean animateLayout) {
	    // -- create Layout --
	    Layout layout = new SmartCircleLayout(animateLayout); //_control.getPluginManager().getNV2DPlugin(MEASURES_PLUGIN));
		if(enforceBounds) {
		    Rectangle r = _renderbox.getAbsRectangle(_renderbox.getBounds());
			r.grow(-1*RenderBox.DISPLAY_BOUNDARY_CUSHION, -1*RenderBox.DISPLAY_BOUNDARY_CUSHION);
			layout.setLayoutBounds(r);
		}
	    _layouts.put(LAYOUT_Circle, layout);
	    
	    // -- create LayoutActionList
	    LayoutActionList a;
	    if(animateLayout) {
	        a = new LayoutActionList(_renderbox.getItemRegistry(), 1000, 20);
			a.add(new GraphFilter());
		    a.setLayout(layout);
			a.add(new RepaintAction());
			a.add(new LocationAnimator());
			a.add(new SizeAnimator());
			a.setPacingFunction(new SlowInSlowOutPacer());	        
	    }
	    else {
	        a = new LayoutActionList(_renderbox.getItemRegistry());
			a.add(new GraphFilter());
		    a.setLayout(layout);
			a.add(new RepaintAction());
	    }

	    return a;
	} //
	
	
	/**
	 * SimAnneal ActionList - returns a new LayoutActionList Object
	 * for the Sim Anneal Layout.
	 */
	// TODO - handle enforce bounds
	public LayoutActionList newSimAnnealLayoutList(boolean enforceBounds, boolean animateLayout) {
	    // TODO - reinstate sim anneal and move center
	    // -- create Layout --
	    Layout layout = new SimulatedAnnealingLayout(false); //_control.getPluginManager().getNV2DPlugin(MEASURES_PLUGIN));
	    _layouts.put(LAYOUT_SimAnneal, layout);
	    
	    // -- create LayoutActionList
	    LayoutActionList a;
	    if(animateLayout) {
	        a = new LayoutActionList(_renderbox.getItemRegistry(), 3000, 100);
			a.add(new GraphFilter());
		    a.setLayout(layout);
			a.add(new RepaintAction());
			a.add(new LocationAnimator());
			a.setPacingFunction(new SlowInSlowOutPacer());
	    }
	    else {
	        a = new LayoutActionList(_renderbox.getItemRegistry());
			a.add(new GraphFilter());
		    a.setLayout(layout);
			a.add(new RepaintAction());
	    }
		return a;
	} //
	
	
	
	/**
	 * Radial Tree Layout List.
	 */
	public LayoutActionList newRadialTreeLayoutList(boolean enforceBounds, boolean animateLayout) {
	    // -- create Layout --
	    Layout layout = new RadialTreeLayout();
		if(enforceBounds) {
		    Rectangle r = _renderbox.getAbsRectangle(_renderbox.getBounds());
			r.grow(-1*RenderBox.DISPLAY_BOUNDARY_CUSHION, -1*RenderBox.DISPLAY_BOUNDARY_CUSHION);
			layout.setLayoutBounds(r);
		}
	    _layouts.put(LAYOUT_RadTree, layout);
	    
	    // -- create LayoutActionList
	    LayoutActionList a;
        a = new LayoutActionList(_renderbox.getItemRegistry());
		a.add(new TreeFilter(true));
	    a.setLayout(layout);
		a.add(new RepaintAction());

	    if(animateLayout) {
	        ActionList animate = new ActionList(_renderbox.getItemRegistry(), 1500, 20);
	        animate.setPacingFunction(new SlowInSlowOutPacer());
	        animate.add(new PolarLocationAnimator());
	        animate.add(new ColorAnimator());
	        animate.add(new RepaintAction());
	        animate.alwaysRunAfter(a);
	    }
        
	    return a;
	} //
	
	
//	/**
//	 * Grid Layout ActionList
//	 * @return
//	 */
//	public ActionList newActionList_Grid() {
//	    ActionList a;
//		a = new ActionList(_renderbox.getItemRegistry(), -1, 20);
//		a.add(new GraphFilter());
//		a.add(new RepaintAction());
//		a.add(new GridLayout());
//	    return a;
//	}	

	/**
	 * Grid Layout List.
	 */
	public LayoutActionList newGridLayoutList(boolean enforceBounds, boolean animateLayout) {
	    // -- create Layout --
	    Layout layout = new GridLayout();
	    _layouts.put(LAYOUT_Grid, layout);
	    
	    // -- create LayoutActionList
	    LayoutActionList a;
	    if(animateLayout) {
	        a = new LayoutActionList(_renderbox.getItemRegistry(), 3000, 100);
			a.add(new GraphFilter());
		    a.setLayout(layout);
			a.add(new RepaintAction());
			a.add(new LocationAnimator());
			a.setPacingFunction(new SlowInSlowOutPacer());
	    }
	    else {
	        a = new LayoutActionList(_renderbox.getItemRegistry());
			a.add(new GraphFilter());
		    a.setLayout(layout);
			a.add(new RepaintAction());
	    }
		return a;
	} //

	
//	/**
//	 * FR+FD Pipeline ActionList - returns a new LayoutActionList
//	 * Object for the FR+FD Pipeline Layout.
//	 */
//	public LayoutActionList newFRplusFDLayoutList(boolean enforceBounds, boolean animateLayout) {
////	    LayoutActionList FR = (LayoutActionList)_layoutLists.get(LAYOUT_FruchRein);
////	    LayoutActionList FD = (LayoutActionList)_layoutLists.get(LAYOUT_ForceDir);
////	    
////	    if(FR == null) {
////	        FR = newFruchReinLayoutList(enforceBounds, animateLayout);
////	    }
////	    if(FD == null) {
////	        FD = newForceDirLayoutList(enforceBounds);
////	    }
////	    //LayoutActionList a = new LayoutActionList(_renderbox.getItemRegistry(), -1, 20);
////	    //a.add(FR);
////	    //a.add(FD);
////	    FD.alwaysRunAfter(FR);
////	    //return a;
////	    return FR;
//	    
//	    
//	    LayoutActionList FR = newFruchReinLayoutList(enforceBounds, animateLayout);
//	    LayoutActionList FD = newForceDirLayoutList(enforceBounds);
//
//	    FR.setStartTime(0);
//	    FR.setDuration(2000);
//	    
//	    FD.setStartTime(2000);
//	    FD.setDuration(5000);
//	    
//	    LayoutActionList a = new LayoutActionList(_renderbox.getItemRegistry(), 7000, 20);
//	    a.add(FR);
//	    a.add(FD);
//	    //FD.alwaysRunAfter(FR);
//	    a.setLayout(FD.getLayout());
//	    return a;
//	    //return FR;
//	    
//	} //
	
	
//	/**
//	 * Force Directed Action List - returns a new LayoutActionList Object
//	 * for the Force-Directed Layout.
//	 */
//	public LayoutActionList newActionList_ForceDir() {
//		LayoutActionList actions = new LayoutActionList(_renderbox.getItemRegistry(), -1, 20);
//		actions.add(new GraphFilter());
//		actions.add(new RepaintAction());
//
//		// --- reinit the force simulator ---
//		_fsim.clear();
//
//		Layout l = (Layout)_layouts.get(LAYOUT_ForceDir);
//
//		if(_enforceBounds) {
//		    if(DEBUG) {System.out.println("setting layout bounds");}
//			// --- enforce bounds and add a force to repel away from walls ---
//		    Rectangle r = getAbsRectangle(_renderbox.getBounds());
//			r.grow(-1*DISPLAY_BOUNDARY_CUSHION, -1*DISPLAY_BOUNDARY_CUSHION);
//			updateBoundaryForces(r);
//			l.setLayoutBounds(r);
//		}
//		else {
//		    // turn off enforce bounds boolean
//		    l = new ForceDirectedLayout(_fsim, false, false);
//			_layouts.put(LAYOUT_ForceDir, l);
//		}
//
//		actions.setLayout(l);
//
//		return actions;
//	} // -- end newActionList_ForceDir
	
	
	
//	
//	/**
//	 * Weighted Force Directed ActionList - returns a new LayoutActionList
//	 * Object for the Weighted-Force-Directed Layout.
//	 */
//	public LayoutActionList newActionList_WForceDir() {
//		LayoutActionList actions = new LayoutActionList(_renderbox.getItemRegistry(), -1, 20);
//		actions.add(new GraphFilter());
//		actions.add(new RepaintAction());
//		
//		_fsim.clear();
//		
//		Layout l = (Layout)_layouts.get(LAYOUT_WForceDir);
//		
//		if(_enforceBounds) {
//			// --- enforce bounds and add a force to repel away from walls ---
//		    Rectangle r = getAbsRectangle(_renderbox.getBounds());
//			r.grow(-1*DISPLAY_BOUNDARY_CUSHION, -1*DISPLAY_BOUNDARY_CUSHION);
//			updateBoundaryForces(r);
//			l.setLayoutBounds(r);
//		}
//		else {
//		    l = new WeightedForceDirectedLayout(_fsim, false, false);
//		    _layouts.put(LAYOUT_WForceDir, l);
//		}
//
//		actions.setLayout(l);
//
//		return actions;
//	} // -- end newActionList_WForceDir
//
//	
//	/**
//	 * Fruchterman Reingold ActionList - returns a new LayoutActionList
//	 * Object for the Fruchterman Reingold Layout.
//	 */
//	public LayoutActionList newActionList_FruchRein() {
//	    LayoutActionList a = new LayoutActionList(_renderbox.getItemRegistry(), 500, 20);
//		a.add(new GraphFilter());
//		a.add(new RepaintAction());
//		a.setLayout((Layout)_layouts.get(LAYOUT_FruchRein));
//		return a;
//	} //
//
//
//	/**
//	 * Circle Graph ActionList - returns a new LayoutActionList Object
//	 * for the Circle Layout.
//	 */
//	public LayoutActionList newActionList_Circle() {
//	    LayoutActionList a = new LayoutActionList(_renderbox.getItemRegistry());
//		a.add(new GraphFilter());
//		a.add(new RepaintAction());
//	    a.setLayout((Layout)_layouts.get(LAYOUT_Circle));
//		return a;
//	} //

	
	
	
//	
//	
//	/**
//	 * Grid Layout ActionList
//	 * @return
//	 */
//	public ActionList newActionList_Grid() {
//	    ActionList a;
//		a = new ActionList(_renderbox.getItemRegistry(), -1, 20);
//		a.add(new GraphFilter());
//		//a.add(new Colorizer()); 		// colors nodes & edges
//		a.add(new RepaintAction());
//		a.add(new GridLayout());
//	    return a;
//	}	
//
//	
//	/**
//	 * Radial Tree Graph ActionList
//	 * @return
//	 */
//	public ActionList newActionList_RadTree() {
//	    ActionList a;
//		// Radial Graph Layout
//		a = new ActionList(_renderbox.getItemRegistry());
//		a.add(new TreeFilter(true, false, true));
//		a.add(new RadialTreeLayout());
//        ActionList animate = new ActionList(_renderbox.getItemRegistry(), 1500, 20);
//        animate.setPacingFunction(new SlowInSlowOutPacer());
//        animate.add(new PolarLocationAnimator());
//        animate.add(new ColorAnimator());
//        animate.add(new RepaintAction());
//        animate.alwaysRunAfter(a);
//	    return a;
//	}
		
	public String author() {
	    return "Sam";
	}
	
	public String description() {
		return "Graph Layout Algorithms.";
	}

	public String name() {
		return "LayoutPlugin";
	}
	
	public String require() {
		return "";
	}

	public void cleanup() {
	}
	
	public void heartbeat() {
	}
	
	
	
//	public void resizeNodesByDatum(String measure) {
//	    Graph g = _renderbox.getGraph();
//	    
//	    // if the datum has been calculated
//	    if ( DATUM HAS BEEN CALCULATED ) {
//	        // find the maximum measure
//	        double min = Double.MAX_VALUE;
//	        double max = Double.MIN_VALUE;
//	        Iterator i = _graph.getVertices().iterator();
//	        while(i.hasNext()) {
//				Vertex v = (Vertex) i.next();
//				// TODO: could ensure the datum is a NUMBER
//				double value = ((Double) v.getDatum(measure).get()).doubleValue();
//				max = (value > max ? value : max);
//				min = (value < min ? value : min);
//			}
//	
//			i = g.getVertices().iterator();
//			while(i.hasNext()) {
//				Vertex v = (Vertex) i.next();
//				PNode pn;
//				try {
//					pn = (PNode) v.getDatum(PGraph.DATUM_POBJ).get();
//				} catch(NullPointerException e) {
//					// return
//					System.err.println("Error: cannot resize nodes until the renderer completes initialization.");
//					return;
//				}
//				double value = ((Double) v.getDatum(measure).get()).doubleValue();
//				double size;
//	
//				if(max == min) {
//					size = MIN_VERTEX_RADIUS;
//				} else {
//					size = MIN_VERTEX_RADIUS + (MAX_VERTEX_RADIUS - MIN_VERTEX_RADIUS) * (value - min) / (max - min);
//				}
//	
//				r.getRegistry().getNodeItem(pn).setSize(size);
//			}
//		}
//	}
//	
//	/**
//	 * Resize all visible (filtered) nodes to the given size.
//	 * @param new_size
//	 */
//	public void resizeNodes(int new_size) {
//	    Iterator i = _renderbox.getFilteredGraph().
//	    //.iterator();
//	}

	
	
	
	
	/**
	 * LayoutChooserPanel
	 */
	public class LayoutChooserPanel extends JPanel {
	    LayoutPlugin _pluginRef;
	    JToggleButton _fd_button;
	    JToggleButton _wd_button;
	    JToggleButton _fr_button;
	    JToggleButton _ci_button;
	    
	    JToggleButton _sa_button;
	    JToggleButton _pi_button;
	    
	    JToolBar toolbar;
	    
	    JComboBox layoutList;
	    
//	    String[] layout_names = {LayoutPlugin.LAYOUT_ForceDir, LayoutPlugin.LAYOUT_WForceDir, LayoutPlugin.LAYOUT_FruchRein, LayoutPlugin.LAYOUT_Circle, LayoutPlugin.LAYOUT_SimAnneal};
//	    ImageIcon[] icons = new ImageIcon[5];

//	    String[] layout_actions = {LayoutPlugin.ACT_FORCE_DIRECTED, LayoutPlugin.ACT_FRUCH_REIN, LayoutPlugin.LAYOUT_Circle};
	    String[] layout_names = {LayoutPlugin.LAYOUT_ForceDir, LayoutPlugin.LAYOUT_WForceDir, LayoutPlugin.LAYOUT_FruchRein, LayoutPlugin.LAYOUT_Circle, LayoutPlugin.LAYOUT_SimAnneal, LayoutPlugin.LAYOUT_RadTree, LayoutPlugin.LAYOUT_Grid, LayoutPlugin.LAYOUT_FRplusFD, LayoutPlugin.LAYOUT_KamKawai, LayoutPlugin.LAYOUT_ISOM};
	    ImageIcon[] icons = new ImageIcon[10];

	    boolean USE_BUTTONS = false;
	    
	    public LayoutChooserPanel(LayoutPlugin lp) {
	        _pluginRef = lp;
	        toolbar = new JToolBar("Layout");
//	        toolbar.setBorder(BorderFactory.createTitledBorder(STR_LABEL_CHOOSERPANEL));
	        
	        // TODO: may want to handle some of these externally
	        this.setLayout(new FlowLayout(FlowLayout.LEFT));
	        this.setBorder(BorderFactory.createTitledBorder(STR_LABEL_CHOOSERPANEL));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			if(USE_BUTTONS) {
			    // code attached in bottom
			}
			// if not USE_BUTTONS --> do a chooser
			else {
		        icons[0] = LayoutUtil.createImageIcon("images/" + IMG_NAME_FORCEDIR + ".gif");
		        icons[1] = LayoutUtil.createImageIcon("images/" + IMG_NAME_WFORCEDIR + ".gif");
		        icons[2] = LayoutUtil.createImageIcon("images/" + IMG_NAME_FRUCHREIN + ".gif");
		        icons[3] = LayoutUtil.createImageIcon("images/" + IMG_NAME_CIRCLE + ".gif");
		        icons[4] = LayoutUtil.createImageIcon("images/" + IMG_NAME_FORCEDIR + ".gif");
		        icons[5] = LayoutUtil.createImageIcon("images/" + IMG_NAME_FORCEDIR + ".gif");
		        icons[6] = LayoutUtil.createImageIcon("images/" + IMG_NAME_FORCEDIR + ".gif");
		        icons[7] = LayoutUtil.createImageIcon("images/" + IMG_NAME_FORCEDIR + ".gif");
		        icons[8] = LayoutUtil.createImageIcon("images/" + IMG_NAME_FORCEDIR + ".gif");
		        icons[9] = LayoutUtil.createImageIcon("images/" + IMG_NAME_FORCEDIR + ".gif");
		        
		        // Create the combo box.
		        Integer[] intArray = new Integer[layout_names.length];
		        for (int i = 0; i < layout_names.length; i++) {
		            intArray[i] = new Integer(i);
		        }
		        
		        // Action Listener
		        ActionListener layoutActions = new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                Integer chosen = (Integer)((JComboBox)e.getSource()).getSelectedItem();
		                int chosen_val = chosen.intValue();
		                
		                String layout_type;
		                if(chosen_val < layout_names.length) {
		                    layout_type = layout_names[chosen_val];
			                if(layout_type != null) {
			                    _pluginRef.setLayout(layout_type, true);
			                }
		                }
		            }
		        };
		        
		        layoutList = new JComboBox(intArray);
		        LayoutChooserRenderer renderer= new LayoutChooserRenderer();
		        renderer.setPreferredSize(new Dimension(140, 20));
		        renderer.setMaximumSize(new Dimension(170, 30));
		        layoutList.setRenderer(renderer);
		        layoutList.setMaximumRowCount(5);
		        layoutList.addActionListener(layoutActions);

		        this.add(layoutList);
			}
	    }
	    
	    public void setSelected(String type) {
	        if(type.equals(LayoutPlugin.LAYOUT_ForceDir)) {
	            layoutList.setSelectedIndex(0);
	        }
	        else if(type.equals(LayoutPlugin.LAYOUT_WForceDir)) {
	            layoutList.setSelectedIndex(1);
	        }
	        else if(type.equals(LayoutPlugin.LAYOUT_FruchRein)) {
	            layoutList.setSelectedIndex(2);
	        }
	        else if(type.equals(LayoutPlugin.LAYOUT_Circle)) {
	            layoutList.setSelectedIndex(3);
	        }
	        else if(type.equals(LayoutPlugin.LAYOUT_SimAnneal)) {
	            layoutList.setSelectedIndex(4);
	        }
	        else if(type.equals(LayoutPlugin.LAYOUT_FRplusFD)) {
	            layoutList.setSelectedIndex(5);
	        }
	        // TODO - complete these
	    }

	    private AbstractButton createLayoutButton(AbstractButton button, String name, String label, Border normalBorder) {
//	        JToggleButton button = new JToggleButton();
	        button.setActionCommand(label);

	        // Set the image or, if that's invalid, equivalent text.
	        ImageIcon icon = LayoutUtil.createImageIcon("images/" + name + ".gif");
	        ImageIcon selectedIcon = LayoutUtil.createImageIcon("images/sel_" + name + ".gif");
	        if (icon != null) {
	            button.setIcon(icon);
	            //button.setSelectedIcon(selectedIcon);
	            //button.setBorder(normalBorder);
	        } else {
	            button.setText(label);
	            button.setFont(button.getFont().deriveFont(Font.ITALIC));
	            button.setHorizontalAlignment(JButton.HORIZONTAL);
	            button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        }
	        button.setToolTipText(label + " Layout");

	        return button;
	    }
	    
	    // Internal-internal class (inside of LayoutChooser)
	    class LayoutChooserRenderer extends JLabel implements ListCellRenderer {
	        private Font font;

	        public LayoutChooserRenderer() {
	            this.setOpaque(true);
	            this.setHorizontalAlignment(LEFT);
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

	            if(selectedIndex > 6) {
	                this.disable();
	            }
	            else
	                this.enable();
	            
	            // Set the icon and text
	            // If icon is null, just use text
	            ImageIcon icon = icons[selectedIndex];
	            String layout = layout_names[selectedIndex];
	            setIcon(icon);
	            setFont(list.getFont());
	            setText(layout);

	            return this;
	        }

	    }

	}
	
	
	
	
//	-----------------------------------------
	
	/**
	 * LayoutSettingsPanel
	 */
	public class LayoutSettingsPanel extends JPanel {
	    LayoutPlugin _pluginRef;
	    private ForceConstantAction action = new ForceConstantAction();
	    private ForceSimulator fsim;
	    
	    JSlider _gSlider;
	    JSlider _cSlider;
	    JSlider _bSlider;
	    boolean showGravity;
	    boolean showCluster;
	    boolean showSpring;
	    Container gravityContainer;
	    Container springContainer;
	    Container clusterContainer;
	    Force nBodyForce;
	    Force springForce;
	    float gravitationalConstant;
	    float springCoefficient;
	    int grav_index;
	    int spring_index;
	    
	    static final int CONST_GRAV_MIN = 0;
	    static final int CONST_GRAV_MAX = 12;
	    static final int CONST_GRAV_DEF = 3;
	    static final int CONST_CLUSTER_MIN = 0;
	    static final int CONST_CLUSTER_MAX = 100;
	    static final int CONST_CLUSTER_DEF = 1;
	    static final int CONST_BOUNCE_MIN = 4;
	    static final int CONST_BOUNCE_MAX = 8;
	    static final int CONST_BOUNCE_DEF = 6;
	    

	    JToolBar toolbar;
	    
	    public LayoutSettingsPanel() {
	        this.setBorder(BorderFactory.createTitledBorder("Layout Settings"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    }
	    
	    public LayoutSettingsPanel(LayoutPlugin lp, ForceSimulator fs, boolean showG, boolean showC, boolean showB) {
	        this.setBorder(BorderFactory.createTitledBorder("Layout Settings"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
//	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	        toolbar = new JToolBar(JToolBar.VERTICAL);
//	        toolbar.setBorder(BorderFactory.createTitledBorder("Layout Settings"));
			
	        _pluginRef = lp;
	        showGravity = showG;
	        showCluster = showC;
	        showSpring = showB;
	        fsim = fs;

	        float param_val;
	        int slider_val;
	        
	        Force[] forces = fsim.getForces();
	        for ( int i=0; i<forces.length; i++ ) {
	            Force f = forces[i];
	            String name = f.getClass().getName();
	            name = name.substring(name.lastIndexOf(".")+1);
	            if ((showGravity || showCluster) && name.equals("NBodyForce")) {
	                nBodyForce = f;
	                for ( int j=0; j<f.getParameterCount(); j++ ) {
	                    if(showGravity && f.getParameterName(j).equals("GravitationalConstant")) {
	                        // set slider equivalent to current value
	                        param_val = f.getParameter(j);
	                        gravitationalConstant = param_val;
	                        grav_index = j;
	                        slider_val = 6 + (int) (3 * (Math.log10(-1 * param_val)));
	                        if(slider_val < CONST_GRAV_MIN || slider_val > CONST_GRAV_MAX) {
	                            slider_val = CONST_GRAV_DEF;
	                        }
	                        gravityContainer = createForceSlider(_gSlider, "GravitationalConstant", "Node Attraction", CONST_GRAV_MIN, CONST_GRAV_MAX, slider_val, 3, f, j, "High", "Low", true);
	                    }
	                    else if(showCluster && f.getParameterName(j).equals("BarnesHutTheta")) {
	                        // set slider equivalent to current value
	                        slider_val = (int)f.getParameter(j);
	                        if(slider_val < CONST_CLUSTER_MIN || slider_val > CONST_CLUSTER_MAX) {
	                            slider_val = CONST_CLUSTER_DEF;
	                        }
	                        clusterContainer = createForceSlider(_cSlider, "BarnesHutTheta", "Clustering Bias", CONST_CLUSTER_MIN, CONST_CLUSTER_MAX, slider_val, 10, f, j, "Low", "High", false);
	                    }
	                }
	                
	            }
	            else if (showSpring && name.equals("SpringForce")) {
	                springForce = f;
	                for ( int j=0; j<f.getParameterCount(); j++ ) {
	                    if(DEBUG) {System.out.println("  - param name:" + f.getParameterName(j));}
	                    if(f.getParameterName(j).equals("SpringCoefficient")) {
	                        // set slider equivalent to current value
	                        param_val = f.getParameter(j);
	                        springCoefficient = param_val;
	                        spring_index = j;
	                        slider_val = -1 * (int) (Math.log10(param_val/4));
	                        if(slider_val < CONST_BOUNCE_MIN || slider_val > CONST_BOUNCE_MAX) {
	                            slider_val = CONST_BOUNCE_DEF;
	                        }	                        
	                        springContainer = createForceSlider(_bSlider, "SpringCoefficient", "Edge Spring Force", CONST_BOUNCE_MIN, CONST_BOUNCE_MAX, slider_val, 1, f, j, "More", "Less", true);
	                    }
	                }
	            }
	        }

	        if(gravityContainer != null) {
	            toolbar.add(gravityContainer);
	        }
	        if(springContainer != null) {
	            toolbar.add(springContainer);
	        }
	        if(clusterContainer != null) {
	            toolbar.add(clusterContainer);
	        }

	        JButton jiggleButton = new JButton("Jiggle");
	        jiggleButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
//	                _pluginRef.jiggleLayout(nBodyForce, gravitationalConstant, grav_index, springForce, springCoefficient, spring_index);
	                _pluginRef.jiggleLayout(nBodyForce, grav_index, springForce, spring_index);
	            }
	        });
	        jiggleButton.setToolTipText("<html><body><p>Jiggle nodes during layout</p><p>to escape local minima</p></body></html>");
	        jiggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	        toolbar.add(jiggleButton);
	        // this.add(Box.createVerticalGlue());
	        
	        toolbar.setRollover(true);
	        toolbar.setFloatable(false);
	        this.add(toolbar);
	    }

	    /**
	     * createForceSlider
	     */
	    private Container createForceSlider(JSlider slider, String name, String label, int min, int max, int pref, int tickspace, Force f, int param, String minLabel, String maxLabel, boolean inverted) {
	        Container container = new Container();
	        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

	        JLabel barLabel = new JLabel(label, JLabel.CENTER);
	        barLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	        
	        slider = new JSlider(JSlider.HORIZONTAL, min, max, pref);
	        slider.setName(name);
	        slider.addChangeListener(new SliderListener());
	        slider.putClientProperty("force", f);
	        slider.putClientProperty("param", new Integer(param));

	        //Turn on labels at major tick marks.
	        slider.setMajorTickSpacing(tickspace);
	        slider.setPaintTicks(true);
	        
	        //Create the label table
	        java.util.Hashtable labelTable = new java.util.Hashtable();
	        labelTable.put( new Integer( min ), new JLabel(minLabel) );
	        labelTable.put( new Integer( max ), new JLabel(maxLabel) );
	        slider.setLabelTable( labelTable );
	        slider.setPaintLabels(true);

	        slider.setInverted(inverted);
	        
	        //slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
	        slider.setAlignmentX(Component.CENTER_ALIGNMENT);
	        slider.setPreferredSize(new Dimension(120, 40));
	        //slider.setMaximumSize(new Dimension(180, 40));
	        
	        //container.setMaximumSize(new Dimension(180, 40));
	        container.add(barLabel);
	        container.add(slider);
	        
	        return container;
	    }
	    
//	  ...where initialization occurs:
	    class SliderListener implements ChangeListener {
	        public void stateChanged(ChangeEvent e) {
	            JSlider source = (JSlider)e.getSource();

	            if (!source.getValueIsAdjusting()) {
	                int val = (int)source.getValue();
	                if (source.getName().equals("GravitationalConstant")) {
	                    double power = ((double)val / 3) - 2;
	                    Double gval = new Double(-1 * Math.pow(10, power));
	                    Force f = (Force)source.getClientProperty("force");
	                    Integer param = (Integer)source.getClientProperty("param");
	                    f.setParameter(param.intValue(), gval.floatValue());
	                    if(DEBUG) {System.out.println("Grav Const:" + gval.floatValue());}
	                }
	                else if (source.getName().equals("BarnesHutTheta")) {
	                    float cval = (float)val;
	                    Force f = (Force)source.getClientProperty("force");
	                    Integer param = (Integer)source.getClientProperty("param");
	                    f.setParameter(param.intValue(), cval);
	                }
	                else if (source.getName().equals("SpringCoefficient")) {
	                    Double bval = new Double(4 * Math.pow(10, (-1 * val)));
	                    Force f = (Force)source.getClientProperty("force");
	                    Integer param = (Integer)source.getClientProperty("param");
	                    f.setParameter(param.intValue(), bval.floatValue());
	                    if(DEBUG) {System.out.println("Spring Coef:" + bval.floatValue());}
	                }
	                
	            }
	        }
	    }
	   
	    
	    
	    private class ForceConstantAction extends AbstractAction {
	        public void actionPerformed(ActionEvent arg0) {
	            JTextField text = (JTextField)arg0.getSource();
	            float val = Float.parseFloat(text.getText());
	            Force f = (Force)text.getClientProperty("force");
	            Integer param = (Integer)text.getClientProperty("param");
	            f.setParameter(param.intValue(), val);
	        }
	    } // end of inner class ForceAction
	}
	
	
//	-----------------------------------------
	
	/**
	 * CircleSettingsPanel
	 */
	public class CircleSettingsPanel extends JPanel {
	    LayoutPlugin _pluginRef;
	    SNA _snaPluginRef;
	    
	    JComboBox optionList;
	    JButton snaButton;
	    JCheckBox resizeBox;
	    
	    public CircleSettingsPanel(LayoutPlugin lp) {
	        _pluginRef = lp;
	        _snaPluginRef = null;
	        setupPanel();
	    }

	    public CircleSettingsPanel(LayoutPlugin lp, SNA np) {
	        _pluginRef = lp;
	        _snaPluginRef = np;
	        setupPanel();
	    }

	        
        
	    private void setupPanel() {
	        this.setBorder(BorderFactory.createTitledBorder("Circle Layout Settings"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	        
	        Layout myLayout = _pluginRef.getLayout(LayoutPlugin.LAYOUT_Circle);
	        if(myLayout == null || !(myLayout instanceof SmartCircleLayout)) {
	            System.err.println("[LayoutPlugin] Circle Panel tried to use a null or invalid Layout");
	        }
	        if(DEBUG) {System.out.println("[panel] gettign smartcirclelayout");}
	        final SmartCircleLayout layout = (SmartCircleLayout)myLayout; 

//	        String[] options = layout.measuresOffered(_pluginRef.getRegistry());
	        String[] layout_measures = layout.measuresOffered();
	        String[] sna_measures = _snaPluginRef.nodeMeasuresOffered();
	        String[] options = LayoutUtil.concat(layout_measures, sna_measures);
	        
	        // display a pulldown with all the measures we can sort by
	        optionList = new JComboBox(options);
	        optionList.setPreferredSize(new Dimension(100, 20));
	        optionList.setMaximumSize(new Dimension(100, 20));	        
	        optionList.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                String chosen = (String)((JComboBox)e.getSource()).getSelectedItem();
	                if(chosen != null) {
                        if( _snaPluginRef != null) {
    		                // -- calculate the chosen measure --
                            _snaPluginRef.indecize();
                            _snaPluginRef.calculate(chosen);

                            // -- if resize is checked, handle --
                            //if(resizeBox.isSelected()) {
                                if(!chosen.equals(LayoutPlugin.STR_SORT_ALPHABETICAL)) {
                                    _snaPluginRef.resizeNodes(chosen);
                                }
                                else {
                                    _pluginRef.getRenderBox().resizeNodes(1);
                                }
                            //}

                            // -- tell the layout which datum to order by --
                            layout.setActiveMeasure(chosen);

                            _pluginRef.setLayout(LayoutPlugin.LAYOUT_Circle, true);
                            //optionList.setSelectedItem(chosen);
                        }
	                }
	            }
	        });
        
	        
	        Container container = new Container();
	        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
	        JLabel sortLabel = new JLabel("Sort By: ", JLabel.CENTER);
	        sortLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(sortLabel);
	        container.add(optionList);
	        
	        this.add(container); //, BorderLayout.NORTH);
	        this.add(new Box.Filler(new Dimension(5, 5), new Dimension(5, 200), new Dimension(5, 300)));
	    }
	    
	} // end of Circle Settings Panel

//	-----------------------------------------
	
	
	/**
	 * FRSettingsPanel
	 */
	public class FRSettingsPanel extends JPanel {
	    LayoutPlugin _pluginRef;
	    
	   
	    public FRSettingsPanel(LayoutPlugin lp) {
	        _pluginRef = lp;
	        
	        this.setBorder(BorderFactory.createEmptyBorder());// createTitledBorder());// "Fruchterman Layout Settings"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
	        
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    }

	}


//	-----------------------------------------
	
	/**
	 * LayoutViewPanel
	 */
	public class LayoutViewPanel extends JPanel {
	    LayoutPlugin pluginRef;
	    JToggleButton panzoom_button;
	    JToggleButton rotate_button;
	    JToggleButton animate_button;
	    JToggleButton keepInBounds_button;
	    JButton fitview_button;
	    JButton resizeNodes_button;
	    JButton unfixNodes_button;
	    JButton zoomOne_button;
	    JToolBar toolbar;
	    JToolBar tb1;
	    JToolBar tb2;

	    public LayoutViewPanel(LayoutPlugin lp) {
	        pluginRef = lp;
	        toolbar = new JToolBar(JToolBar.VERTICAL);
	        tb1 = new JToolBar();
	        tb2 = new JToolBar();
	        
	        // TODO: may want to handle some of these externally
//	        this.setLayout(new FlowLayout(FlowLayout.LEFT));
	        this.setLayout(new BorderLayout());
	        this.setBorder(BorderFactory.createTitledBorder(STR_LABEL_VIEWPANEL));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
			
	        ButtonGroup group = new ButtonGroup();
	        
	        Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
	
	        
	        panzoom_button = (JToggleButton)createLayoutButton(new JToggleButton(), RenderBox.VIEW_MODE_PAN_ZOOM, STR_LABEL_PAN_ZOOM, IMG_NAME_PAN_ZOOM, border);
	        group.add(panzoom_button);
	        //this.add(_panzoom_button);
//	        toolbar.add(panzoom_button);
	        tb1.add(panzoom_button);
	        
	        rotate_button = (JToggleButton)createLayoutButton(new JToggleButton(), RenderBox.VIEW_MODE_ROTATE, STR_LABEL_ROTATE, IMG_NAME_ROTATE, border);
	        group.add(rotate_button);
	        //this.add(_rotate_button);
//	        toolbar.add(rotate_button);
//	        toolbar.addSeparator();
	        tb1.add(rotate_button);
	        tb1.addSeparator();
	        
	        // TODO
	        animate_button = (JToggleButton)createLayoutButton(new JToggleButton(), STR_LABEL_ANIMATE, STR_LABEL_ANIMATE, IMG_NAME_ANIMATE, border);
	        animate_button.setSelected(_renderbox.getAnimateLayout());
	        animate_button.addActionListener( new ActionListener() {
	           public void actionPerformed(ActionEvent e) {
	               if(DEBUG) {System.out.println("Setting ANIMATE:" + animate_button.isSelected());}
	               pluginRef.setAnimateLayout(animate_button.isSelected());
	           }
	        });
//	        toolbar.add(animate_button);
	        tb1.add(animate_button);
	        
	        keepInBounds_button = (JToggleButton)createLayoutButton(new JToggleButton(), STR_LABEL_KEEP_IN_BOUNDS, STR_LABEL_KEEP_IN_BOUNDS, IMG_NAME_KEEP_IN_BOUNDS, border);
	        keepInBounds_button.setSelected(_renderbox.getEnforceBounds());
	        keepInBounds_button.addActionListener( new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                if(DEBUG) {System.out.println("Setting ENFORCEBOUNDS:" + ((JToggleButton)e.getSource()).isSelected());}
	                pluginRef.setEnforceBounds(((JToggleButton)e.getSource()).isSelected());
	            }
	        });
	        
//	        toolbar.add(keepInBounds_button);
	        tb1.add(keepInBounds_button);

	        // TODO
	        // toolbar.addSeparator();
//	        tb1.addSeparator();
	        
	        fitview_button = (JButton)createLayoutButton(new JButton(), STR_LABEL_FITVIEW, STR_LABEL_FITVIEW, IMG_NAME_FITVIEW, border);
	        fitview_button.addActionListener( new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                pluginRef.fitGraphToWindow();
	            }
	        });
	        //this.add(fitview_button);
//	        toolbar.add(fitview_button);
	        tb2.add(fitview_button);

	        zoomOne_button = (JButton)createLayoutButton(new JButton(), "Zoom 1:1", "Zoom 1:1", IMG_NAME_ZOOM_ONE, border);
	        zoomOne_button.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                if(DEBUG) {System.out.println("Zooming 1:1");}
	                _renderbox.zoomGraph(1.0);
	            }
	        });
	        tb2.add(zoomOne_button);
	        
	        tb2.addSeparator();
	        
	        resizeNodes_button = (JButton)createLayoutButton(new JButton(), STR_LABEL_RESIZE_NODES, STR_LABEL_RESIZE_NODES, IMG_NAME_RESIZE_NODES, border); 
	        resizeNodes_button.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) {
	               if(DEBUG) {System.out.println("resize box clicked");}
                   pluginRef.getRenderBox().resizeNodes(1);
	           }
	        });
//	        toolbar.add(resizeNodes_button);
	        tb2.add(resizeNodes_button);

	        // TODO - get rid of this button if not needed
	        unfixNodes_button = (JButton)createLayoutButton(new JButton(), "Unfix Nodes", "Unfix Nodes", IMG_NAME_RESIZE_NODES, border);
	        unfixNodes_button.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) {
	               if(DEBUG) {System.out.println("UNFIX NODES clicked");}
                   pluginRef.getRenderBox().unfixNodes();
	           }
	        });
	        //tb2.add(unfixNodes_button);
	        

	        
//	        toolbar.setFloatable(false);
//	        toolbar.setRollover(true);
//	        this.add(toolbar);
	        
//	        tb1.setFloatable(true);
//	        tb2.setFloatable(true);
	        tb1.setFloatable(false);
	        tb2.setFloatable(false);
	        tb1.setRollover(true);
	        tb2.setRollover(true);
	        // TODO - get color from UIManager/LookAndFeel instead of hard coding
	        tb1.setBackground(new Color(238, 238, 238));
	        //tb1.setBorder(BorderFactory.createEmptyBorder());
	        //tb1.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.GRAY));
	        tb2.setBackground(new Color(238, 238, 238));
	        //tb2.setBorder(BorderFactory.createEmptyBorder());
	                
//	        if(DEBUG) {System.out.println("LookNFeel: " + javax.swing.UIManager.getLookAndFeel());}
	        this.add(tb1, BorderLayout.NORTH);
	        this.add(tb2, BorderLayout.SOUTH);
	        this.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 0)), BorderLayout.CENTER);
	        
//	        toolbar.setFloatable(false);
//	        toolbar.setRollover(false);
//	        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
//	        toolbar.add(tb1);
//	        toolbar.add(tb2);
//	        this.add(toolbar);
//	        
//	        this.add(tb1);
//	        this.add(tb2);
	        
	        
	        // Action Listener
	        // TODO - make individual action listeners
	        ActionListener viewActions = new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                String command = ((JToggleButton) e.getSource()).getActionCommand();
	                if (command.equals(RenderBox.VIEW_MODE_PAN_ZOOM)) {
	                    pluginRef.setView(RenderBox.VIEW_MODE_PAN_ZOOM);
	                } else if (command.equals(RenderBox.VIEW_MODE_ROTATE)) {
	                    pluginRef.setView(RenderBox.VIEW_MODE_ROTATE);
	                }
	            }
	        };
	
	        // add action listeners
	        panzoom_button.addActionListener(viewActions);
	        rotate_button.addActionListener(viewActions);
	    }
	
	    public void setSelected(String type) {
	        if(type.equals(RenderBox.VIEW_MODE_PAN_ZOOM)) {
	            panzoom_button.setSelected(true);
	        }
	        
	        else if(type.equals(RenderBox.VIEW_MODE_ROTATE)) {
	            rotate_button.setSelected(true);
	        }
	                
	    }
	
	    // TODO: clean up this method and put in utils
	    private AbstractButton createLayoutButton(AbstractButton button, String command, String label, String imgname, Border normalBorder) {
	        //JToggleButton button = new JToggleButton();
	        button.setActionCommand(command);
	
	        // Set the image or, if that's invalid, equivalent text.
	        ImageIcon icon = LayoutUtil.createImageIcon("images/" + imgname + ".gif");
	        ImageIcon selectedIcon = LayoutUtil.createImageIcon("images/sel_" + imgname + ".gif");
	        if (icon != null) {
	            button.setIcon(icon);
	            //button.setSelectedIcon(selectedIcon);
	            //button.setBorder(normalBorder);
	        } 
	        else {
	            button.setText(label);
	            button.setFont(button.getFont().deriveFont(Font.ITALIC));
	            button.setHorizontalAlignment(JButton.HORIZONTAL);
	            //button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        }
	        button.setToolTipText(label);
	
	        return button;
	    }
	
	} // end of LayoutViewPanel


} // end of LayoutPlugin





//// if USE BUTTONS
//    ButtonGroup group = new ButtonGroup();
//    Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
//
//    _fd_button = (JToggleButton)createLayoutButton(new JToggleButton(), "forceDirected",
//            LayoutPlugin.LAYOUT_ForceDir, border);
//    group.add(_fd_button);
////	        this.add(_fd_button);
//    toolbar.add(_fd_button);
//
//    _wd_button = (JToggleButton)createLayoutButton(new JToggleButton(), "wForceDirected",
//            LayoutPlugin.LAYOUT_WForceDir, border);
////    group.add(_wd_button);
//////	        this.add(_wd_button);
////    toolbar.add(_wd_button);
//
//    _fr_button = (JToggleButton)createLayoutButton(new JToggleButton(), "fruchRein",
//            LayoutPlugin.LAYOUT_FruchRein, border);
//    group.add(_fr_button);
////	        this.add(_fr_button);
//    toolbar.add(_fr_button);
//
//    _ci_button = (JToggleButton)createLayoutButton(new JToggleButton(), "circle", LayoutPlugin.LAYOUT_Circle,
//            border);
//    group.add(_ci_button);
////	        this.add(_ci_button);
//    toolbar.add(_ci_button);
//    
//    toolbar.setFloatable(false);
//    toolbar.setRollover(true);
//    this.add(toolbar);
//
//    // TODO
//    _sa_button = (JToggleButton)createLayoutButton(new JToggleButton(), "simAnneal", LayoutPlugin.LAYOUT_SimAnneal,
//            border);
////    group.add(_sa_button);
////    this.add(_sa_button);
//////	        _pi_button = createLayoutButton("pipeline", LayoutPlugin.LAYOUT_FRplusFD,
//////	                border);
//////	        group.add(_pi_button);
//////	        this.add(_pi_button);
//    // TODO
//    
//    
//    // Action Listener
//    ActionListener layoutActions = new ActionListener() {
//        public void actionPerformed(ActionEvent e) {
//            String command = ((JToggleButton) e.getSource())
//                    .getActionCommand();
//            if (command.equals(LayoutPlugin.LAYOUT_ForceDir)) {
//                _pluginRef.setLayout(LayoutPlugin.ACT_FORCE_DIRECTED, true);
//            } else if (command.equals(LayoutPlugin.LAYOUT_WForceDir)) {
//                _pluginRef.setLayout(LayoutPlugin.LAYOUT_WForceDir, true);
//            } else if (command.equals(LayoutPlugin.LAYOUT_FruchRein)) {
//                _pluginRef.setLayout(LayoutPlugin.LAYOUT_FruchRein, true);
//            } else if (command.equals(LayoutPlugin.LAYOUT_Circle)) {
//                _pluginRef.setLayout(LayoutPlugin.LAYOUT_Circle, true);
//            }
//            
////	                // TODO
//            else if (command.equals(LayoutPlugin.LAYOUT_SimAnneal)) {
//                _pluginRef.setLayout(LayoutPlugin.LAYOUT_SimAnneal, true);
//            }
////	                else if (command.equals(LayoutPlugin.LAYOUT_FRplusFD)) {
////	                    _pluginRef.setLayout(LayoutPlugin.LAYOUT_FRplusFD, true);
////	                }
////	                // TODO
//        
//    }
//    };
//
//    // add action listeners
//    _fd_button.addActionListener(layoutActions);
//    _wd_button.addActionListener(layoutActions);
//    _fr_button.addActionListener(layoutActions);
//    _ci_button.addActionListener(layoutActions);
//    
////	        // TODO
//    _sa_button.addActionListener(layoutActions);
////	        _pi_button.addActionListener(layoutActions);
////	       // TODO
//}







///**
//*/
//public void initBoundaryForces() {
//	if(_enforceBounds && _renderbox != null) {
//	    Rectangle r = _renderbox.getBounds();
//		_boundary_left = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, r.height);
//		_boundary_top = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, r.width, 0);
//		_boundary_bottom = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, r.height, r.width, r.height);
//		_boundary_right = new SmartWallForce(BOUNDARY_FORCE_VALUE, r.width, 0, r.width, r.height);
//	}
//	else { 
//		_boundary_left = new SmartWallForce(0, 0, 0, 0, 0);
//		_boundary_top = new SmartWallForce(0, 0, 0, 0, 0);
//		_boundary_bottom = new SmartWallForce(0, 0, 0, 0, 0);
//		_boundary_right = new SmartWallForce(0, 0, 0, 0, 0);
//	}
//	_boundariesSet = true;
//}

//public void initForceSimulator() {
//   _fsim = new ForceSimulator();
//	_fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
//	_fsim.addForce(new SpringForce(4E-5f, 75f));
//	_fsim.addForce(new DragForce(-0.005f));
//	_fsim.addForce(new CircularWallForce(0.1f, 0.1f, 0.1f));
//	
//	if(_enforceBounds) {
//		if(_renderbox != null) {
//		    Rectangle r = _renderbox.getBounds();
//			_boundary_left = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, r.height);
//			_boundary_top = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, r.width, 0);
//			_boundary_bottom = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, r.height, r.width, r.height);
//			_boundary_right = new SmartWallForce(BOUNDARY_FORCE_VALUE, r.width, 0, r.width, r.height);	    
//		}
//		else { 
//			_boundary_left = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, 0);
//			_boundary_top = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, 0);
//			_boundary_bottom = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, 0);
//			_boundary_right = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, 0);	    
//		}
//		
//		_fsim.addForce(_boundary_left);
//		_fsim.addForce(_boundary_right);
//		_fsim.addForce(_boundary_bottom);
//		_fsim.addForce(_boundary_top);
//		_boundariesSet = true;
//	}
//} // -- end initForceSimulator

//
///**
//* UpdateBoundaryForces - updates the boundary wall forces in 
//* the global force simulator to the contours of the specified 
//* Rectangle.
//* 
//* Use this when the RenderBox is resized. 
//*/
//public void updateBoundaryForces(Rectangle r) {
//	if(_enforceBounds && _boundariesSet) {
//	    System.out.println("Updating Boundaries");
//	    _boundary_left.setY2(r.height);
//	    _boundary_top.setX2(r.width);
//	    _boundary_bottom.setY1(r.height);
//	    _boundary_bottom.setX2(r.width);
//	    _boundary_bottom.setY2(r.height);
//	    _boundary_right.setX1(r.width);
//	    _boundary_right.setX2(r.width);
//	    _boundary_right.setY2(r.height); 
//	}
//} // -- end updateBoundaryForces
//

///**
//* GetAbsRectangle - takes a Rectangle in display coordinates and
//* returns a Rectangle in absolute coordinates.
//*/
//public Rectangle getAbsRectangle(Rectangle r) {
//   System.out.println("Getting Abs Rect");
//	Point p1 = new Point((int)r.getX(), (int)r.getY());
//	Point p2 = new Point((int)(r.getX()+r.getWidth()), (int)(r.getY()+r.getHeight()));
//   Point2D p1abs = _renderbox.getAbsoluteCoordinate(p1, new Point());
//   Point2D p2abs = _renderbox.getAbsoluteCoordinate(p2, new Point());
//   r = new Rectangle((int)p1abs.getX(), (int)p1abs.getY(), (int)(p2abs.getX()-p1abs.getX()), (int)(p2abs.getY()-p1abs.getY()));
//   return r;
//} // -- end getAbsRectangle

//
///**
//* updatesCurrentLayoutBounds - updates the layout bounds of the 
//* current Layout in the RenderBox. If the RenderBox is running the 
//* Layout, if first stops the Activity, then manipulates the Layout,
//* then restarts (to avoid concurrent manipulations by threads).
//*/
//public void updateCurrentLayoutBounds(Rectangle r) {
//   boolean needToRestart = false;
//   // --- stop layout to prevent thread conflicts --- 
//   if(_renderbox.isRunningActivity()) {
//       _renderbox.stopLayout();
//       needToRestart = true;
//   }
//
//   // --- update layout bounds --- 
//   Activity a = _renderbox.getCurrentActivity();
//   if (a instanceof LayoutActionList) {
//       Layout l = ((LayoutActionList)a).getLayout();
//       r = getAbsRectangle(_renderbox.getBounds());
//       r.grow(-1 * DISPLAY_BOUNDARY_CUSHION, -1* DISPLAY_BOUNDARY_CUSHION);
//       updateBoundaryForces(r);
//       l.setLayoutBounds(r);
//   }
//       
//   // --- restart --- 
//   if(needToRestart) {
//       _renderbox.startLayout();
//   }
//} // end updateCurrentLayoutBounds
//



