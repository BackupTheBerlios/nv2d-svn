package nv2d.plugins.standard.layout;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.animate.ColorAnimator;
import edu.berkeley.guir.prefuse.action.animate.PolarLocationAnimator;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.action.filter.TreeFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefusex.force.CircularWallForce;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.Force;
import edu.berkeley.guir.prefusex.force.WallForce;
import edu.berkeley.guir.prefusex.force.ForcePanel;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefusex.layout.CircleLayout;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.FruchtermanReingoldLayout;
import edu.berkeley.guir.prefusex.layout.GridLayout;
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

//import edu.berkeley.guir.prefusex.layout.WeightedForceDirectedLayout;

//import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;
import nv2d.plugins.NPluginManager;
import nv2d.plugins.IOInterface;
import nv2d.render.Colorizer;
import nv2d.render.PGraph;
import nv2d.render.PNode;
import nv2d.render.RenderBox;
import nv2d.render.SemiRandomLayout;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;

import nv2d.graph.directed.DEdge;
import nv2d.graph.directed.DGraph;
import nv2d.graph.directed.DVertex;
import nv2d.ui.NController;
import nv2d.ui.ViewInterface;

import nv2d.plugins.standard.SNA;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Math;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.PopupFactory;
import javax.swing.Popup;

import java.awt.FlowLayout;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JComponent;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.border.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import edu.berkeley.guir.prefusex.force.*;

import nv2d.graph.Graph;
import nv2d.graph.Vertex;

import nv2d.ui.NController;

import java.awt.Dimension;

/**
 * 
 * Handles Graph Layouts.  Adds a sidebar with access to layout
 * controls.
 * 
 * @author sam
 *
 * TODO
 *     - menulisteners on Layout Menu should update renderbox properties
 */
public class LayoutPlugin implements NV2DPlugin {

    static NController _control;
	Container _view;
	static RenderBox _renderbox;
	
	// --- GUI Components ---
	JMenu _layoutMenu;
	
	JPanel _sideBarPanel;
	LayoutChooserPanel _layoutChooserPanel;
	JPanel _layoutSettingsPanel;
	LayoutViewPanel _layoutViewPanel;
	JComponent _c;

	//static LayoutForceCtlSidePanel _forceCtlPanel;
	
	boolean hasBeenInitialized;
	private String _currentLayout;
	
	// --- FORCE SIMULATOR ---
	static private ForceSimulator _fsim;
	static private boolean _boundariesSet;
	static private boolean _enforceBoundaries;
	static final int BOUNDARY_FORCE_VALUE = -100;
	static private SmartWallForce _boundary_left;
	static private SmartWallForce _boundary_right;
	static private SmartWallForce _boundary_top;
	static private SmartWallForce _boundary_bottom;
    static NBodyForce _nBodyForce;
	static boolean forceDirSet;
	static boolean wForceDirSet;
	
	// --- ACTION LISTS ---
	static private SmartCircleLayout _smartCircleLayout;
	static private ActionList _fd_actions;
	static private ActionList _wd_actions;
	static private ActionList _fr_actions;
	static private ActionList _ci_actions;
	static private ActionList _gr_actions;
	static private ActionList _ra_actions;
	static private RandomLayout _randomLayout;
	static private SemiRandomLayout _semiRandomLayout;

	
	public static final String LAYOUT_ForceDir = "Force Directed";
	public static final String LAYOUT_WForceDir = "Weighted Force Directed";
	public static final String LAYOUT_FruchRein = "Fruchterman Reingold";
	public static final String LAYOUT_RadTree = "Radial Tree";
	public static final String LAYOUT_Circle = "Circular";
	public static final String LAYOUT_Grid = "Grid";	
	public static final String LAYOUT_SimAnneal = "Simulated Annealing";
	public static final String LAYOUT_Random = "Random";

	public static final String ACT_FORCE_DIRECTED = "layout_ForceDirectedLayout";
	public static final String ACT_WEIGHTED_FORCE_DIRECTED = "layout_WeightedForceDirectedLayout";
	public static final String ACT_FRUCHTERMAN_REINGOLD = "layout_FruchtermanReingoldLayout";
	public static final String ACT_CIRCLE = "layout_CircleLayout";

	public static final String VIEW_PAN_ZOOM = "Pan/Zoom";
	public static final String VIEW_ROTATE = "Rotate";
	
	public static final String STR_SORT_ALPHABETICAL = "Alphabetize";

	/**
	 * Constructor
	 */
	public LayoutPlugin() {
	    //System.out.println("Constructing Layout Plugin");
		
	    initForceSimulator();
		hasBeenInitialized = false;
		// TODO: FIX BOUNDARIES, for centering & etc.
		_enforceBoundaries = false;  // turn off boundaries until bugs fixed
	}
	
	/**
	 * ReloadAction
	 * 
	 * Called each time the graph is changed.
	 */
	public void reloadAction(Graph g) {
	    //System.out.println("** Reloading Layout Plugin");
	    
	    // set new initial settings
	    _layoutViewPanel.setSelected(VIEW_PAN_ZOOM);
	    setView(VIEW_PAN_ZOOM);

	    _layoutChooserPanel.setSelected(LAYOUT_ForceDir);
	    setLayout(LAYOUT_ForceDir, false);
	    //_renderbox.setInitialLayout(LAYOUT_ForceDir);
	}
	
	/**
	 * Initialize
	 * 
	 * Called by main program to initialize plugin.
	 */
	// TODO: initialize is still called each time reloadAction is called.
	// Temporary fix is the hasBeenInitialized boolean.
	public void initialize(Graph g, Container view, NController control) {
	    //System.out.println("** Initializing LayoutPlugin");
	    
		if(!hasBeenInitialized) {
			//System.out.println("** FIRST LayoutPlugin Initialization");
			
		    _control = control;
			_view = view;
			_renderbox = control.getRenderBox();

			// --- init side panel ---
			_sideBarPanel = new JPanel();
	        _sideBarPanel.setLayout(new BorderLayout()); //new BoxLayout(_sideBarPanel, BoxLayout.Y_AXIS));
			_layoutChooserPanel = new LayoutChooserPanel(this);
			_layoutSettingsPanel = new LayoutSettingsPanel(this, _fsim, true, true, true);
			_layoutViewPanel = new LayoutViewPanel(this);
			
			_sideBarPanel.add(_layoutChooserPanel, BorderLayout.NORTH);
			_sideBarPanel.add(_layoutSettingsPanel, BorderLayout.CENTER); //_layoutSettingsPanel);
			_sideBarPanel.add(_layoutViewPanel, BorderLayout.SOUTH);
			
			// Add Force Panel to Side Bar
		    _control.getView().addComponent(_sideBarPanel, "Layout", ViewInterface.SIDE_PANEL);

			// first init done
			hasBeenInitialized = true;
		}
	}

	
	/**
	 * Get Registry
	 */
	public ItemRegistry getRegistry() {
	    return _renderbox.getRegistry();
	}
	
	
	// TODO: expand with Settings & Clustering
	/**
	 * Plugin Menu
	 */
	public JMenu menu() {
	    //System.out.println("Layout: Initializing JMenu");
		_layoutMenu = new JMenu("Layout");
		
		// Make the Layouts a group of RADIO BUTTON MENU ITEMS
//		ButtonGroup group = new ButtonGroup();
//		final JMenuItem l_random  = new JMenuItem(LAYOUT_Random); 
//		_layoutMenu.add(l_random);
//		_layoutMenu.addSeparator();		
		final JMenuItem l_forceDir = new JMenuItem(LAYOUT_ForceDir); 
		_layoutMenu.add(l_forceDir); //group.add(l_forceDir);
		l_forceDir.setSelected(true);
		final JMenuItem l_wForceDir = new JMenuItem(LAYOUT_WForceDir); 
		_layoutMenu.add(l_wForceDir); //group.add(l_wForceDir);
		final JMenuItem l_fruchRein = new JMenuItem(LAYOUT_FruchRein); 
		_layoutMenu.add(l_fruchRein); //group.add(l_fruchRein);
		final JMenuItem l_circle = new JMenuItem(LAYOUT_Circle); 
		_layoutMenu.add(l_circle); //group.add(l_circle);
		//final JMenuItem l_grid = new JMenuItem(LAYOUT_Grid); 
		//_layoutMenu.add(l_grid); //group.add(l_grid);
		
	    ActionListener layoutActions = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource().equals(l_forceDir)) {
				    _layoutChooserPanel.setSelected(LAYOUT_ForceDir);
				    setLayout(LAYOUT_ForceDir, true);
				} else if (e.getSource().equals(l_wForceDir)) {
				    _layoutChooserPanel.setSelected(LAYOUT_WForceDir);
				    setLayout(LAYOUT_WForceDir, true);
				} else if (e.getSource().equals(l_fruchRein)) {
				    _layoutChooserPanel.setSelected(LAYOUT_FruchRein);
				    setLayout(LAYOUT_FruchRein, true);
				} else if (e.getSource().equals(l_circle)) {
				    _layoutChooserPanel.setSelected(LAYOUT_Circle);
				    setLayout(LAYOUT_Circle, true);
				} 
				/*
				else if (e.getSource().equals(l_grid)) {
				} 
				else if (e.getSource().equals(l_random)) {
				}
				*/
				
			}
		};
		
		// add action listeners
		l_forceDir.addActionListener(layoutActions);
		l_wForceDir.addActionListener(layoutActions);
		l_fruchRein.addActionListener(layoutActions);
		l_circle.addActionListener(layoutActions);
		//l_grid.addActionListener(layoutActions);
		//l_random.addActionListener(layoutActions);
		
		return _layoutMenu;
	}
	

	/**
	 * Set Layout in RenderBox
	 */
	public void setLayout(String name, boolean start) {
	    if (name.equals(LAYOUT_ForceDir)) {
            _renderbox.addActivity(ACT_FORCE_DIRECTED, newActionList_ForceDir());
		    _renderbox.setActiveLayout(ACT_FORCE_DIRECTED);
		    if(start) {
		        _renderbox.startLayout();
		    }
		    
		    // if the layout just changed, update sidebar with new corresponding panels
		    if(_currentLayout != null && !_currentLayout.equals(LAYOUT_ForceDir)) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        _layoutSettingsPanel = new LayoutSettingsPanel(this, _fsim, true, true, true);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.CENTER);
		        _sideBarPanel.validate();
		    }
		    _currentLayout = LAYOUT_ForceDir;
	    }
	    else if (name.equals(LAYOUT_WForceDir)) {
            _renderbox.addActivity(ACT_WEIGHTED_FORCE_DIRECTED, newActionList_WForceDir());
		    _renderbox.setActiveLayout(ACT_WEIGHTED_FORCE_DIRECTED);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if(_currentLayout != null && !_currentLayout.equals(LAYOUT_WForceDir)) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        _layoutSettingsPanel = new LayoutSettingsPanel(this, _fsim, true, true, true);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.CENTER);
		        _sideBarPanel.validate();
		    }
		    _currentLayout = LAYOUT_WForceDir;
	    }
	    else if (name.equals(LAYOUT_FruchRein)) {
		    _renderbox.addActivity(ACT_FRUCHTERMAN_REINGOLD, newActionList_FruchRein());
		    _renderbox.setActiveLayout(ACT_FRUCHTERMAN_REINGOLD);
		    if(start) {
		        _renderbox.startLayout();
		    }

		    if(_currentLayout != null && !_currentLayout.equals(LAYOUT_FruchRein)) {
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        _layoutSettingsPanel = new FRSettingsPanel(this);
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.CENTER);
		        _sideBarPanel.validate();
		    }
		    _currentLayout = LAYOUT_FruchRein;
	    }
	    else if (name.equals(LAYOUT_Circle)) {

//		    // if SNA plugin is present, give Panel a reference
//			NPluginManager npm = _control.getPluginManager();
//			NV2DPlugin sna = npm.getNV2DPlugin("SNA");
//			if(sna != null) {
//			    _layoutSettingsPanel = new CircleSettingsPanel(this, sna);
//			}
//			else {
//			    _layoutSettingsPanel = new CircleSettingsPanel(this);			    
//			}

		    if(_currentLayout != null && !_currentLayout.equals(LAYOUT_Circle)) {
			    _renderbox.addActivity(ACT_CIRCLE, newActionList_Circle());
			    _renderbox.setActiveLayout(ACT_CIRCLE);
			    if(start) {
			        _renderbox.startLayout();
			    }
		        _sideBarPanel.remove(_layoutSettingsPanel);
		        SNA sna = (SNA)_control.getPluginManager().getNV2DPlugin("SNA");
				if(sna != null) {
				    _layoutSettingsPanel = new CircleSettingsPanel(this, sna);
				}
				else {
				    _layoutSettingsPanel = new CircleSettingsPanel(this);			    
				}		        
		        _sideBarPanel.add(_layoutSettingsPanel, BorderLayout.CENTER);
		        _sideBarPanel.validate();

		    }
		    else {

			    if(start) {
			        _renderbox.startLayout();
			    }
		    }
		    _currentLayout = LAYOUT_Circle;
	    }
	}
	
	/**
	 * SetView
	 */
	public void setView(String view) {
	    if(view.equals(VIEW_PAN_ZOOM)) {
	        _renderbox.setRotateMode(false);
	    }
	    else if(view.equals(VIEW_ROTATE)) {
	        _renderbox.setRotateMode(true);
	    }
	}
	
	/**
	 * InitializeForceSimulator
	 *
	 * Initializes the global force simulator.
	 */
	public void initForceSimulator() {
	    //System.out.println("Initializing Force Simulator");
	    _fsim = new ForceSimulator();
	    _nBodyForce = new NBodyForce(-0.4f, -1f, 0.9f);
		_fsim.addForce(_nBodyForce);
		_fsim.addForce(new SpringForce(4E-5f, 75f));
		_fsim.addForce(new DragForce(-0.005f));
		_fsim.addForce(new CircularWallForce(0.1f, 0.1f, 0.1f));
		
		if(_enforceBoundaries) {
		if(_renderbox != null) {
		    Rectangle r = _renderbox.getBounds();
			_boundary_left = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, r.height);
			_boundary_top = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, r.width, 0);
			_boundary_bottom = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, r.height, r.width, r.height);
			_boundary_right = new SmartWallForce(BOUNDARY_FORCE_VALUE, r.width, 0, r.width, r.height);	    
		}
		else { 
			_boundary_left = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, 0);
			_boundary_top = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, 0);
			_boundary_bottom = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, 0);
			_boundary_right = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, 0);	    
		}
		
		_fsim.addForce(_boundary_left);
		_fsim.addForce(_boundary_right);
		_fsim.addForce(_boundary_bottom);
		_fsim.addForce(_boundary_top);
		_boundariesSet = true;
		}
	}
	
	/**
	 * UpdateBoundaryForces
	 * 
	 * Updates the boundary wall forces in the global force 
	 * simulator to the contours of the specified Rectangle.
	 * 
	 * Use this when the RenderBox is resized. 
	 */
	static public void updateBoundaryForces(Rectangle r) {
		if(_enforceBoundaries && _boundariesSet) {
		    //System.out.println("Updating Boundary Forces");	    
		    _boundary_left.setY2(r.height);
		    _boundary_top.setX2(r.width);
		    _boundary_bottom.setY1(r.height);
		    _boundary_bottom.setX2(r.width);
		    _boundary_bottom.setY2(r.height);
		    _boundary_right.setX1(r.width);
		    _boundary_right.setX2(r.width);
		    _boundary_right.setY2(r.height); 
		}
		
		/*
		System.out.println("Left: (" + _boundary_left.getX1() + ", " + _boundary_left.getY1() + ") - (" + _boundary_left.getX2() + ", " + _boundary_left.getY2() + ")");
		System.out.println("Right: (" + _boundary_right.getX1() + ", " + _boundary_right.getY1() + ") - (" + _boundary_right.getX2() + ", " + _boundary_right.getY2() + ")");
		System.out.println("Top: (" + _boundary_top.getX1() + ", " + _boundary_top.getY1() + ") - (" + _boundary_top.getX2() + ", " + _boundary_top.getY2() + ")");
		System.out.println("Bottom: (" + _boundary_bottom.getX1() + ", " + _boundary_bottom.getY1() + ") - (" + _boundary_bottom.getX2() + ", " + _boundary_bottom.getY2() + ")");
		*/
	}
	
	
	/**
	 * Force Directed Action List
	 */
	static public ActionList newActionList_ForceDir() {
		_fd_actions = new ActionList(_renderbox.getItemRegistry(), -1, 20);
		_fd_actions.add(new GraphFilter());
		_fd_actions.add(new RepaintAction());

		
		Layout l;
		if(_enforceBoundaries) {
		// Enforce bounds and add a force to repel away from walls
		Rectangle r = _renderbox.getBounds();
		r.grow(-20, -20);
		updateBoundaryForces(r);
		_fsim.clear();
		
		l = new ForceDirectedLayout(_fsim, true, false);
		l.setLayoutBounds(r);
		}
		else {
		    l = new ForceDirectedLayout(_fsim, false, false);
		}
		// System.out.println("Setting Layout Bounds to: " + r);
		_fd_actions.add(l);

		return _fd_actions;
	}

	
	/**
	 * Weighted Force Directed Action List
	 * @return
	 */
	static public ActionList newActionList_WForceDir() {
		_wd_actions = new ActionList(_renderbox.getItemRegistry(), -1, 20);
		_wd_actions.add(new GraphFilter());
		_wd_actions.add(new RepaintAction());
		
		Layout l;
		if(_enforceBoundaries) {
		// handle staying within bounds
		// Enforce bounds and add a force to repel away from walls
		Rectangle r = _renderbox.getBounds();
		r.grow(-20, -20);

		updateBoundaryForces(r);
		_fsim.clear();
		//updateForceSimulator();
		
		l = new WeightedForceDirectedLayout(_fsim, true, 10, false);
		l.setLayoutBounds(r);
		}
		else {
		    l = new WeightedForceDirectedLayout(_fsim, false, 10, false);
		}
		// System.out.println("Setting Layout Bounds to: " + r);
		_wd_actions.add(l);

		return _wd_actions;
	}
	
	/**
	 * Fruchterman Reingold Layout
	 * @return
	 */
	static public ActionList newActionList_FruchRein() {
	    ActionList a;
		a = new ActionList(_renderbox.getItemRegistry(), 500, 20);
		a.add(new GraphFilter());
		//a.add(new Colorizer()); 		// colors nodes & edges
		a.add(new RepaintAction());
		a.add(new FruchtermanReingoldLayout(100));
		return a;
	}

	/**
	 * Random Action List
	 * @return
	 */
	public ActionList newActionList_Random() {
	    ActionList a;
		a = new ActionList(_renderbox.getItemRegistry());
		a.add(new GraphFilter());
		//a.add(new Colorizer()); 		// colors nodes & edges
		a.add(new RepaintAction());
		a.add(new RandomLayout());	    
	    return a;
	}

	/**
	 * Circle Graph ActionList
	 * @return
	 */
	static public ActionList newActionList_Circle() {
	    ActionList a;
		// init circle layout
		a = new ActionList(_renderbox.getItemRegistry()); //-1, 20);
		a.add(new GraphFilter());
		//a.add(new Colorizer()); 		// colors nodes & edges
		a.add(new RepaintAction());
		// if SNA plugin is present, inform SmartCircleLayout of the available
		// measures
		NPluginManager npm = _control.getPluginManager();
		// TODO, it would be better not give reference to the plugin but
		// to give some reference to the measures available
		NV2DPlugin sna = npm.getNV2DPlugin("SNA");
		if(sna != null) {
		    _smartCircleLayout = new SmartCircleLayout(sna);
		}
		else {
		    _smartCircleLayout = new SmartCircleLayout();
		}
		
	    a.add(_smartCircleLayout);

		return a;
	}
	
	public SmartCircleLayout getSmartCircleLayout() {
	    return _smartCircleLayout;
	}

	/**
	 * Grid Layout ActionList
	 * @return
	 */
	public ActionList newActionList_Grid() {
	    ActionList a;
		a = new ActionList(_renderbox.getItemRegistry(), -1, 20);
		a.add(new GraphFilter());
		//a.add(new Colorizer()); 		// colors nodes & edges
		a.add(new RepaintAction());
		a.add(new GridLayout());
	    return a;
	}	

	public void heartbeat() {
	}

	/**
	 * Radial Tree Graph ActionList
	 * @return
	 */
	public ActionList newActionList_RadTree() {
	    ActionList a;
		// Radial Graph Layout
		a = new ActionList(_renderbox.getItemRegistry());
		a.add(new TreeFilter(true, false, true));
		a.add(new RadialTreeLayout());
        ActionList animate = new ActionList(_renderbox.getItemRegistry(), 1500, 20);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new PolarLocationAnimator());
        animate.add(new ColorAnimator());
        animate.add(new RepaintAction());
        animate.alwaysRunAfter(a);
	    return a;
	}

	/**
	 * Simulated Annealing ActionList
	 * @return
	 */
	public ActionList newActionList_SimAnneal() {
	    ActionList a = new ActionList(_renderbox.getItemRegistry());

	    return a;
	}
	
	
	
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
	
	
	
	
	
	/**
	 * LayoutChooserPanel
	 */
	public class LayoutChooserPanel extends JPanel {
	    LayoutPlugin _pluginRef;
	    JToggleButton _fd_button;
	    JToggleButton _wd_button;
	    JToggleButton _fr_button;
	    JToggleButton _ci_button;
	    
	    public LayoutChooserPanel(LayoutPlugin lp) {
	        _pluginRef = lp;
	        
	        // TODO: may want to handle some of these externally
	        this.setLayout(new FlowLayout(FlowLayout.LEFT));
	        this.setMinimumSize(new Dimension(200, 30));
	        this.setPreferredSize(new Dimension(300, 100));
	        this.setBorder(BorderFactory.createTitledBorder("Layout"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.setMaximumSize(new Dimension(300, 100));
			
	        ButtonGroup group = new ButtonGroup();
	        Border border = BorderFactory.createEmptyBorder(3, 3, 3, 3);

	        _fd_button = createLayoutButton("forceDirected",
	                LayoutPlugin.LAYOUT_ForceDir, border);
	        group.add(_fd_button);
	        this.add(_fd_button);

	        _wd_button = createLayoutButton("wForceDirected",
	                LayoutPlugin.LAYOUT_WForceDir, border);
	        group.add(_wd_button);
	        this.add(_wd_button);

	        _fr_button = createLayoutButton("fruchRein",
	                LayoutPlugin.LAYOUT_FruchRein, border);
	        group.add(_fr_button);
	        this.add(_fr_button);

	        _ci_button = createLayoutButton("circle", LayoutPlugin.LAYOUT_Circle,
	                border);
	        group.add(_ci_button);
	        this.add(_ci_button);

	        // Action Listener
	        ActionListener layoutActions = new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                String command = ((JToggleButton) e.getSource())
	                        .getActionCommand();
	                if (command.equals(LayoutPlugin.LAYOUT_ForceDir)) {
	                    //System.out.println("Toggle FD");
	                    _pluginRef.setLayout(LayoutPlugin.LAYOUT_ForceDir, true);
	                } else if (command.equals(LayoutPlugin.LAYOUT_WForceDir)) {
	                    //System.out.println("Toggle WFD");
	                    _pluginRef.setLayout(LayoutPlugin.LAYOUT_WForceDir, true);
	                } else if (command.equals(LayoutPlugin.LAYOUT_FruchRein)) {
	                    //System.out.println("Toggle FR");
	                    _pluginRef.setLayout(LayoutPlugin.LAYOUT_FruchRein, true);
	                } else if (command.equals(LayoutPlugin.LAYOUT_Circle)) {
	                    //System.out.println("Toggle Circle");
	                    _pluginRef.setLayout(LayoutPlugin.LAYOUT_Circle, true);
	                }
	            }
	        };

	        // add action listeners
	        _fd_button.addActionListener(layoutActions);
	        _wd_button.addActionListener(layoutActions);
	        _fr_button.addActionListener(layoutActions);
	        _ci_button.addActionListener(layoutActions);
	    }
	    
	    public void setSelected(String type) {
	        if(type.equals(LayoutPlugin.LAYOUT_ForceDir)) {
	            _fd_button.setSelected(true);
	        }
	        else if(type.equals(LayoutPlugin.LAYOUT_WForceDir)) {
	            _wd_button.setSelected(true);
	        }
	        else if(type.equals(LayoutPlugin.LAYOUT_FruchRein)) {
	            _fr_button.setSelected(true);
	        }
	        else if(type.equals(LayoutPlugin.LAYOUT_Circle)) {
	            _ci_button.setSelected(true);
	        }

	    }

	    private JToggleButton createLayoutButton(String name, String label,
	            Border normalBorder) {
	        JToggleButton button = new JToggleButton();
	        button.setActionCommand(label);

	        // Set the image or, if that's invalid, equivalent text.
	        ImageIcon icon = createImageIcon("images/" + name + ".gif");
	        ImageIcon selectedIcon = createImageIcon("images/sel_" + name + ".gif");
	        if (icon != null) {
	            button.setIcon(icon);
	            button.setSelectedIcon(selectedIcon);
	            button.setBorder(normalBorder);
	        } else {
	            button.setText(label);
	            button.setFont(button.getFont().deriveFont(Font.ITALIC));
	            button.setHorizontalAlignment(JButton.HORIZONTAL);
	            button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        }
	        button.setToolTipText(label + " Layout");

	        return button;
	    }

		/* Returns an ImageIcon, or null if the path was invalid. */
		protected ImageIcon createImageIcon(String path) {
		    java.net.URL imgURL = null;
		    try {
		        // TODO: fix image path issue!!!!!!!!!, store locally
		        imgURL = new java.net.URL("http://web.mit.edu/prentice/www/" + path); //LayoutForceCtlSidePanel.class.getResource(path);
		        //imgURL = LayoutPlugin.class.getResource(path);
		    }
		    catch (Exception e) { System.out.println(e); }
		    //System.out.println("Looking in: " + imgURL);
		    if (imgURL != null) {
		        return new ImageIcon(imgURL);
		    } else {
		        System.err.println("Couldn't find file: " + path);
		        return null;
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
	    
	    static final int CONST_GRAV_MIN = 0;
	    static final int CONST_GRAV_MAX = 12;
	    static final int CONST_GRAV_DEF = 3;
	    static final int CONST_CLUSTER_MIN = 0;
	    static final int CONST_CLUSTER_MAX = 100;
	    static final int CONST_CLUSTER_DEF = 1;
	    static final int CONST_BOUNCE_MIN = 4;
	    static final int CONST_BOUNCE_MAX = 8;
	    static final int CONST_BOUNCE_DEF = 6;
	    
	    
	    
	    public LayoutSettingsPanel(LayoutPlugin lp, ForceSimulator fs, boolean showG, boolean showC, boolean showB) {
	        _pluginRef = lp;
	        showGravity = showG;
	        showCluster = showC;
	        showSpring = showB;
	        fsim = fs;
	        
	        this.setPreferredSize(new Dimension(300, 200));
	        this.setBorder(BorderFactory.createTitledBorder("Force-Directed Layout Settings"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
	        
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	        float param_val;
	        int slider_val;
	        
	        Force[] forces = fsim.getForces();
	        for ( int i=0; i<forces.length; i++ ) {
	            Force f = forces[i];
	            String name = f.getClass().getName();
	            name = name.substring(name.lastIndexOf(".")+1);
	            if ((showGravity || showCluster) && name.equals("NBodyForce")) {
	                for ( int j=0; j<f.getParameterCount(); j++ ) {
	                    if(showGravity && f.getParameterName(j).equals("GravitationalConstant")) {
	                        // set slider equivalent to current value
	                        param_val = f.getParameter(j);
	                        slider_val = 6 + (int) (3 * (Math.log10(-1 * param_val)));
	                        if(slider_val < CONST_GRAV_MIN || slider_val > CONST_GRAV_MAX) {
	                            slider_val = CONST_GRAV_DEF;
	                        }
	                        Container c = createForceSlider(_gSlider, "GravitationalConstant", "Inter-Gravity", CONST_GRAV_MIN, CONST_GRAV_MAX, slider_val, 3, f, j, "High", "Low", true);
	                        this.add(c);
	                    }
	                    else if(showCluster && f.getParameterName(j).equals("BarnesHutTheta")) {
	                        // set slider equivalent to current value
	                        slider_val = (int)f.getParameter(j);
	                        if(slider_val < CONST_CLUSTER_MIN || slider_val > CONST_CLUSTER_MAX) {
	                            slider_val = CONST_CLUSTER_DEF;
	                        }
	                        Container c = createForceSlider(_cSlider, "BarnesHutTheta", "Clustering", CONST_CLUSTER_MIN, CONST_CLUSTER_MAX, slider_val, 10, f, j, "Low", "High", false);
	                        this.add(c);
	                    }
	                }
	                
	            }
	            else if (showSpring && name.equals("SpringForce")) {
	                for ( int j=0; j<f.getParameterCount(); j++ ) {
	                    //System.out.println("  - param name:" + f.getParameterName(j));
	                    if(f.getParameterName(j).equals("SpringCoefficient")) {
	                        // set slider equivalent to current value
	                        param_val = f.getParameter(j);
	                        slider_val = -1 * (int) (Math.log10(param_val/4));
	                        if(slider_val < CONST_BOUNCE_MIN || slider_val > CONST_BOUNCE_MAX) {
	                            slider_val = CONST_BOUNCE_DEF;
	                        }	                        
	                        Container c = createForceSlider(_bSlider, "SpringCoefficient", "Bounce", CONST_BOUNCE_MIN, CONST_BOUNCE_MAX, slider_val, 1, f, j, "More", "Less", true);
	                        this.add(c);                       
	                    }
	                }
	            }
	            
	        }
	        this.add(Box.createVerticalGlue());
	    }

	    /**
	     * createForceSlider
	     */
	    public Container createForceSlider(JSlider slider, String name, String label, int min, int max, int pref, int tickspace, Force f, int param, String minLabel, String maxLabel, boolean inverted) {
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
	                //System.out.println("Slider state changed: " + source.getName() + " to: " + val);                
	                if (source.getName().equals("GravitationalConstant")) {
	                    double power = ((double)val / 3) - 2;
	                    //System.out.println("Power: " + power);
	                    Double gval = new Double(-1 * Math.pow(10, power));
	                    Force f = (Force)source.getClientProperty("force");
	                    Integer param = (Integer)source.getClientProperty("param");
	                    f.setParameter(param.intValue(), gval.floatValue());
	                    //System.out.println("  - gSlider: " + gval.floatValue());
	                }
	                else if (source.getName().equals("BarnesHutTheta")) {
//	                    double power = ((double)val / 3) - 2;
//	                    Double cval = new Double(Math.pow(10, power));
//	                    System.out.println("cval: " + cval);
//	                    Force f = (Force)source.getClientProperty("force");
//	                    Integer param = (Integer)source.getClientProperty("param");
//	                    f.setParameter(param.intValue(), cval.floatValue());
	                    float cval = (float)val;
	                    //System.out.println("cval: " + cval);
	                    Force f = (Force)source.getClientProperty("force");
	                    Integer param = (Integer)source.getClientProperty("param");
	                    f.setParameter(param.intValue(), cval);
	                }
	                else if (source.getName().equals("SpringCoefficient")) {
	                    Double bval = new Double(4 * Math.pow(10, (-1 * val)));
	                    //System.out.println("bval: " + bval);
	                    Force f = (Force)source.getClientProperty("force");
	                    Integer param = (Integer)source.getClientProperty("param");
	                    f.setParameter(param.intValue(), bval.floatValue());
	                    //System.out.println("  - bSlider: " + val + " bval: " + bval.floatValue());
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
	        //System.out.println("Constructing CircleSettingsPanel with SNA");
	        _pluginRef = lp;
	        _snaPluginRef = null;
	        
	        setupPanel();
	    }

	    public CircleSettingsPanel(LayoutPlugin lp, SNA np) {
	        //System.out.println("Constructing CircleSettingsPanel with SNA");
	        _pluginRef = lp;
	        _snaPluginRef = np;
	        
	        setupPanel();
	    }

	    private void setupPanel() {
	        this.setPreferredSize(new Dimension(300, 200));
	        this.setBorder(BorderFactory.createTitledBorder("Circle Layout Settings"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
	        
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	        
	        final SmartCircleLayout myLayout = _pluginRef.getSmartCircleLayout();
	        String[] options = myLayout.measuresOffered(_pluginRef.getRegistry());
	        // display a pulldown with all the measures we can sort by
	        optionList = new JComboBox(options);
	        //optionList.setSelectedIndex(4);
	        optionList.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                String chosen = (String)((JComboBox)e.getSource()).getSelectedItem();
	                //System.out.println("OPTIONBOX: " + chosen);
	                if(resizeBox.isSelected() && !chosen.equals(LayoutPlugin.STR_SORT_ALPHABETICAL)) {
	                    _snaPluginRef.resizeNodes(chosen);
	                }
	                myLayout.setActiveMeasure(chosen);
	                _pluginRef.setLayout(LayoutPlugin.LAYOUT_Circle, true);
	            }
	        });
	        
	        snaButton = new JButton("Add SNA Measures");
	        snaButton.setPreferredSize(new Dimension(30, 10));
	        snaButton.setMaximumSize(new Dimension(150, 30));
	        snaButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                    // Run SNA Calculations
	                	_snaPluginRef.indecize();
	                    _snaPluginRef.calculate();
	                    
	                    // Update ComboBox
	        	        String[] ops = myLayout.measuresOffered(_pluginRef.getRegistry());
	        	        optionList.removeAllItems();
	        	        for(int k=0; k<ops.length; k++) {
	        	            optionList.addItem(ops[k]);
	        	        }
	        	        
	        	        // need to repaint?/revalidate?
	            }
	        });
	        
	        resizeBox = new JCheckBox("Scale Node Size");
	        
	        Container container = new Container();
	        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
	        JLabel sortLabel = new JLabel("Sort By: ", JLabel.CENTER);
	        sortLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(sortLabel);
	        container.add(optionList);
	        
	        this.add(container); //, BorderLayout.NORTH);
	        this.add(new Box.Filler(new Dimension(200, 15), new Dimension(200, 15), new Dimension(800, 30)));
            if(_snaPluginRef != null) {
                this.add(snaButton); //, BorderLayout.CENTER);            
    	        this.add(new Box.Filler(new Dimension(200, 15), new Dimension(200, 15), new Dimension(800, 30)));
                this.add(resizeBox); //, BorderLayout.SOUTH);
            }
            
            
	    }
	    
	    // TODO: add Radius size slider
	    // TODO: add Sort-By pulldown
	    //     - alphabetize
	    //     - measures
	    //        - in order, or spread
	}

//	-----------------------------------------
	
	/**
	 * FRSettingsPanel
	 */
	public class FRSettingsPanel extends JPanel {
	    LayoutPlugin _pluginRef;
	    
	    
	    public FRSettingsPanel(LayoutPlugin lp) {
	        _pluginRef = lp;
	        
	        this.setPreferredSize(new Dimension(300, 200));
	        this.setBorder(BorderFactory.createTitledBorder("Fruchterman Layout Settings"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
	        
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    }

	}

	
//	-----------------------------------------
	
	/**
	 * LayoutViewPanel
	 */
	public class LayoutViewPanel extends JPanel {
	    // TODO: ***********************
	    // Taken out for Jonathons Test Run
	    //
	    LayoutPlugin _pluginRef;
	    JToggleButton _panzoom_button;
	    JToggleButton _rotate_button;
	    
	    public LayoutViewPanel(LayoutPlugin lp) {
	        _pluginRef = lp;
	        
	        // TODO: may want to handle some of these externally
	        this.setLayout(new FlowLayout(FlowLayout.LEFT));
	        this.setMinimumSize(new Dimension(200, 30));
	        this.setPreferredSize(new Dimension(300, 100));
	        //this.setBorder(BorderFactory.createTitledBorder("View"));
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.setMaximumSize(new Dimension(300, 100));
			
	        ButtonGroup group = new ButtonGroup();
	        Border border = BorderFactory.createEmptyBorder(3, 3, 3, 3);
	       
//
//	        _panzoom_button = createLayoutButton("Pan/Zoom",
//	                LayoutPlugin.VIEW_PAN_ZOOM, border);
//	        group.add(_panzoom_button);
//	        this.add(_panzoom_button);
//
//	        /* TODO fix Rotate Origin */
//	        _rotate_button = createLayoutButton("Rotate",
//	                LayoutPlugin.VIEW_ROTATE, border);
//	        group.add(_rotate_button);
//	        this.add(_rotate_button);
//	        /**/
	    }
	    
	    public void setSelected(String type) {
	    }
	    
	}
//	-----------------------------------------
	
//    public LayoutViewPanel(LayoutPlugin lp) {
//        _pluginRef = lp;
//        
//        // TODO: may want to handle some of these externally
//        this.setLayout(new FlowLayout(FlowLayout.LEFT));
//        this.setMinimumSize(new Dimension(200, 30));
//        this.setPreferredSize(new Dimension(300, 100));
//        this.setBorder(BorderFactory.createTitledBorder("View"));
//		this.setAlignmentX(Component.LEFT_ALIGNMENT);
//		this.setMaximumSize(new Dimension(300, 100));
//		
//        ButtonGroup group = new ButtonGroup();
//        Border border = BorderFactory.createEmptyBorder(3, 3, 3, 3);
//
//        _panzoom_button = createLayoutButton("Pan/Zoom",
//                LayoutPlugin.VIEW_PAN_ZOOM, border);
//        group.add(_panzoom_button);
//        this.add(_panzoom_button);
//
//        /* TODO fix Rotate Origin */
//        _rotate_button = createLayoutButton("Rotate",
//                LayoutPlugin.VIEW_ROTATE, border);
//        group.add(_rotate_button);
//        this.add(_rotate_button);
//        /**/
//        
//        // Action Listener
//        ActionListener viewActions = new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                String command = ((JToggleButton) e.getSource()).getActionCommand();
//                if (command.equals(LayoutPlugin.VIEW_PAN_ZOOM)) {
//                    _pluginRef.setView(LayoutPlugin.VIEW_PAN_ZOOM);
//                } else if (command.equals(LayoutPlugin.VIEW_ROTATE)) {
//                    _pluginRef.setView(LayoutPlugin.VIEW_ROTATE);
//                }
//            }
//        };
//
//        // add action listeners
//        _panzoom_button.addActionListener(viewActions);
//        _rotate_button.addActionListener(viewActions);
//    }
//
//    public void setSelected(String type) {
//        if(type.equals(LayoutPlugin.VIEW_PAN_ZOOM)) {
//            _panzoom_button.setSelected(true);
//        }
//        
//        else if(type.equals(LayoutPlugin.VIEW_ROTATE)) {
//            _rotate_button.setSelected(true);
//        }
//                
//    }
//
//    // TODO: clean up this method and put in utils
//    private JToggleButton createLayoutButton(String name, String label,
//            Border normalBorder) {
//        JToggleButton button = new JToggleButton();
//        button.setActionCommand(label);
//
//        // Set the image or, if that's invalid, equivalent text.
//        /*ImageIcon icon = createImageIcon("images/" + name + ".gif");
//        ImageIcon selectedIcon = createImageIcon("images/sel_" + name + ".gif");
//        if (icon != null) {
//            System.out.println("Icon is not null" + name);
//            button.setIcon(icon);
//            button.setSelectedIcon(selectedIcon);
//            button.setBorder(normalBorder);
//        } else {
//            System.out.println("Icon IS NULL" + name + " " + label);
//            */
//            button.setText(label);
//            button.setFont(button.getFont().deriveFont(Font.ITALIC));
//            button.setHorizontalAlignment(JButton.HORIZONTAL);
//            button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
////        }
//        button.setToolTipText(label + " View");
//
//        return button;
//    }
//
//	/* Returns an ImageIcon, or null if the path was invalid. */
//	protected ImageIcon createImageIcon(String path) {
//	    java.net.URL imgURL = null;
//	    try {
//	        // TODO: fix image path issue!!!!!!!!!, store locally
//	        imgURL = new java.net.URL("http://web.mit.edu/prentice/www/" + path); //LayoutForceCtlSidePanel.class.getResource(path);
//	        //imgURL = LayoutPlugin.class.getResource(path);
//	    }
//	    catch (Exception e) { System.out.println(e); }
//	    //System.out.println("Looking in: " + imgURL);
//	    if (imgURL != null) {
//	        return new ImageIcon(imgURL);
//	    } else {
//	        System.err.println("Couldn't find file: " + path);
//	        return null;
//	    }
//	}
//}
////-----------------------------------------


}







