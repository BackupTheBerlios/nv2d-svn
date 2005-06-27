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

package nv2d.render;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Math;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Point;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.PopupFactory;
import javax.swing.Popup;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.util.display.DisplayLib;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.animate.SizeAnimator;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.focus.DefaultFocusSet;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;
import edu.berkeley.guir.prefusex.controls.RotationControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.Path;
import nv2d.graph.Vertex;
import nv2d.graph.filter.DegreeFilter;
import nv2d.ui.NController;
import nv2d.utils.filefilter.*;
import nv2d.plugins.standard.layout.LayoutActionList;
import nv2d.plugins.standard.layout.SmartWallForce;

/**
 * Creates a new graph and draws it on the screen.
 */

// TODO - clean this up
//public class RenderBox extends Display {
public class RenderBox extends ActiveDisplay {

	public static final float TRANSPARENCY = 0.7f;
	public static final String DATUM_LASTLOCATION = "__renderbox:lastloc";
	
	private NController _ctl;
	private ItemRegistry _registry;
	private RenderSettings _settings;
	private Graph _g;
	private ActivityDirector _director;
	private ControlManager _controls;
	
	private boolean _empty;
	private String _viewMode;
	private boolean _animateLayout;
	
	/**
	 * Used to handle enforcing layouts to window bounds
	 */
	private boolean _boundariesSet;
	private boolean _enforceBounds;
	private SmartWallForce _boundary_left;
	private SmartWallForce _boundary_right;
	private SmartWallForce _boundary_top;
	private SmartWallForce _boundary_bottom;
	static final int BOUNDARY_FORCE_VALUE = -100;

	// --- Cushion between Layout & Display Borders (in Pixels) ---
	public static final int DISPLAY_BOUNDARY_CUSHION = 20;


	
	/**
	 * Used to keep track of the source for shortest paths calculations and
	 * visualization
	 */
	private Vertex _apspSource;
	
	// various colorizers
	ColorFunction _colorizer;
	ColorFunction _legendColorizer;
	
	private PopupMenu _vertexMenu;
	
	// for the mouse interface
	private static VisualItem _lastItemClicked;
	
	// Activity and Layout String Names
	public static final String ACT_COLORIZER = "standard_ColorizerActivity";
	public static final String ACT_SEMIRANDOM = "standard_SemiRandomLayout";
	public static final String ACT_RANDOM = "standard_RandomLayout";
	public static final String ACT_FORCEDIRECTED = "standard_ForceDirectedLayout";
	
	// Control Listener String Names
	public static final String CTL_MOUSE_ADAPTOR = "standard_MouseAdaptor";
	public static final String CTL_DRAG_CONTROL = "standard_DragControl";
	public static final String CTL_PAN_CONTROL = "standard_PanControl";
	public static final String CTL_ZOOM_CONTROL = "standard_ZoomControl";
	public static final String CTL_ROTATION_CONTROL = "standard_RotationControl";
	public static final String CTL_FOCUS_ACTIONS_CONTROL = "focusActionsControl";
	public static final String CTL_FOCUS_HOVER_CONTROL = "focusHoverControl";
	
	// View Mode Names
	public static final String VIEW_MODE_ROTATE = "standard_ViewModeRotation";
	public static final String VIEW_MODE_PAN = "standard_ViewModePan";
	public static final String VIEW_MODE_ZOOM = "standard_ViewModeZoom";
	public static final String VIEW_MODE_PAN_ZOOM = "standard_ViewModePanZoom";
	
	private static boolean DEBUG = false;
	
	private ActionList updateDisplay;
	
	/**
	 * Constructor
	 */
	public RenderBox(NController ctl) {
		// (1) convert NV2D graph to a data structure usable by Prefuse
		// (2) create a new item registry
		//  the item registry stores all the visual
		//  representations of different graph elements
		super(new ItemRegistry(new DefaultGraph(true)));
		
		// establish settings controller
		_settings = new RenderSettings();
		// _pFactory = PopupFactory.getSharedInstance();
		
		_ctl = ctl;
		
		// setup the popup menu for vertices
		_vertexMenu = new PopupMenu();
		_lastItemClicked = null;
		
		_director = new ActivityDirector();
		_controls = new ControlManager(this);
		
		// create a new display component to show the data
		// setSize(400,400);
		// pan(350, 350);

		// TODO - handle REPAINT problem by placing repaint activity between layouts
		// Set Mouse Adaptor and Drag ALWAYS ON
		_controls.addControl(CTL_MOUSE_ADAPTOR, new MouseAdapter(this));
		_controls.addControl(CTL_DRAG_CONTROL, new NoFixDragControl(true));
//		_controls.addControl(CTL_DRAG_CONTROL, new DragControl(true, false));
//		_controls.addControl(CTL_DRAG_CONTROL, new MouseDragControl(this));
		// TODO - add selection control for filtering Nodes by selected mouse region
		//_controls.addControl("select", new nv2d.plugins.standard.layout.SelectionControl());

		setViewMode(VIEW_MODE_PAN_ZOOM);
		
		// initialize boundary forces
	    initBoundaryForces();
	    setEnforceBounds(true);
	 
	    setAnimateLayout(true);
	         
	    // --- update bounds and layout on renderbox resize ---
		this.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		        // if enforcing bounds, update
		        if(_enforceBounds) {
		            updateCurrentLayoutBounds(getBounds(), false);
		        }
		        
		        // TODO - perhaps center the current layout
		        // within the newly updated window
		    }
		});

		/*
		// TODO - EXAMPLE Listeners for Bo
		this.addDisplayTransformListener(new DisplayTransformListener() {
		   public void displayTransformed(DisplayTransformEvent e) {
		       if(DEBUG) {System.out.println("RENDERBOX: DisplayTransformed");}
		       if(e.getEventType() == DisplayTransformEvent.PAN_TRANSFORM_EVENT) {
		           if(DEBUG) {System.out.println("RENDERBOX: Pan Occured");}	           
		       }
		       else if(e.getEventType() == DisplayTransformEvent.ZOOM_TRANSFORM_EVENT) {
		           if(DEBUG) {System.out.println("RENDERBOX: Zoom Occured");}		           
		       }
		       else if(e.getEventType() == DisplayTransformEvent.PAN_ZOOM_TRANSFORM_EVENT) {
		           if(DEBUG) {System.out.println("RENDERBOX: Pan Zoom Occured");}
		       }
		       
		        if(_enforceBounds) {
		            updateCurrentLayoutBounds(getBounds(), true);
		        }

		   }
		});
		this.addDisplayTransformListener(new DisplayTransformListener() {
		    public void displayTransformed(DisplayTransformEvent e) {
		        if(DEBUG) {System.out.println("Display Event: " + e.getEventType());}
		    }
		});
		*/
	    
		_empty = true;
		initialize(null);
	}
	
	
	public void useLegendColoring() {
		_colorizer.setEnabled(false);
		_legendColorizer.setEnabled(true);
		//_legendColorizer.run(_registry, 0.0);
		_director.runNowInBackground(_legendColorizer, _registry, 0.0);
	}
	
	
	public void useDefaultColoring() {
		_colorizer.setEnabled(true);
		_legendColorizer.setEnabled(false);
//		_colorizer.run(_registry, 0.0);
		_director.runNowInBackground(_colorizer, _registry, 0.0);
		repaint();
	}
	
	
	public void clear() {
		if(_empty) {
			return;
		}
		
		doSaveVertexLocations();
		_registry.clear();
		_director.clear();
		_empty = true;
		repaint();
	}
	
	
	public BufferedImage screenShot() {
		BufferedImage bi = new BufferedImage(
				(int) getWidth(),
				(int) getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		
		// draw to image
		update(g);
		
		return bi;
	}

	
	/**
	 * Initialize
	 */
	public void initialize(Graph g) {
		_g = g;
		_registry = getRegistry();
				
		if(g != null) {
			_registry.setGraph(new PGraph(g));
		}

        _empty = false;

        // antialias?
        setHighQuality(_settings.getBoolean(RenderSettings.ANTIALIAS));
        		
		// init colorizers
        _colorizer = new Colorizer();
        _legendColorizer = new LegendColorizer(_ctl);
        _colorizer.setEnabled(true);
        _legendColorizer.setEnabled(false);

        initStandardLayouts();
        
        // check if an active layout has already been set, if not set default
        if (!_director.isActiveSet()) {
            _director.setActive(ACT_FORCEDIRECTED);
        }

        // Run Colorizers WITH ALL LAYOUTS
        _director.setRunWithLayout(ACT_COLORIZER);

        // Begin by randomly setting all the items
        doSemiRandomLayout();
        
//        // TODO - Add RepaintAction to the Activity Director
//        ActionList al = new ActionList(_registry, -1, 50);
//        al.add(new RepaintAction());
//        al.runNow();
        
	    // update ActionList
        updateDisplay = new ActionList(_registry);
        updateDisplay.add(new Colorizer());
        updateDisplay.add(new LegendColorizer(_ctl));
        updateDisplay.add(new RepaintAction());

        
	}
	
	
	/**
	 * Standard Layouts
	 */
	private void initStandardLayouts() {
		ForceSimulator fsim;
		ForceDirectedLayout flayout;
		RandomLayout randomLayout;
		SemiRandomLayout semiRandomLayout;
		ActionList s, r, f;
		
		GraphFilter graphFilter = new GraphFilter();
		RepaintAction repaintAction = new RepaintAction();
		
		// Colorizer Activity
		ActionList colors = new ActionList(_registry, -1, 20);
		colors.add(_colorizer);
		colors.add(_legendColorizer);
		_director.add(ACT_COLORIZER, colors);
		
		// Semi-Random Layout
		semiRandomLayout = new SemiRandomLayout(_ctl);
		s = new ActionList(_registry);
		s.add(graphFilter);
		s.add(repaintAction);
		s.add(semiRandomLayout);
		_director.add(ACT_SEMIRANDOM, s);
		
		// init random layout
		randomLayout = new RandomLayout();
		r = new ActionList(_registry);
		r.add(graphFilter);
		r.add(repaintAction);
		r.add(randomLayout);
		_director.add(ACT_RANDOM, r);
		
		// init FD layout
		fsim = new ForceSimulator();
		fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
		fsim.addForce(new SpringForce(4E-5f, 75f));
		fsim.addForce(new DragForce(-0.005f));
		flayout = new ForceDirectedLayout(fsim, false, false);
		f = new ActionList(_registry, -1, 20);
		f.add(graphFilter);
		f.add(repaintAction);
		f.add(flayout);
		_director.add(ACT_FORCEDIRECTED, f);
	}
	
	
	// ----- Radial Tree - Focus ------
	
	public void addFocusControl(ActionList actions) {
	    //        addControlListener(new FocusControl(actions));
	    //        addControlListener(new FocusControl(0,FocusManager.HOVER_KEY));
        _controls.addControl(CTL_FOCUS_ACTIONS_CONTROL, new FocusControl(actions));
        _controls.addControl(CTL_FOCUS_HOVER_CONTROL, new FocusControl(0,FocusManager.HOVER_KEY));
        _registry.getFocusManager().putFocusSet(FocusManager.HOVER_KEY, new DefaultFocusSet());
	}
	
	public void removeFocusControl() {
	    _controls.removeControl(CTL_FOCUS_ACTIONS_CONTROL);
	    _controls.removeControl(CTL_FOCUS_HOVER_CONTROL);
	}
	
	// ------- Boundaries --------
	
	/**
	 */
	public void initBoundaryForces() {
		if(_enforceBounds) {
		    Rectangle r = getBounds();
			_boundary_left = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, 0, r.height);
			_boundary_top = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, 0, r.width, 0);
			_boundary_bottom = new SmartWallForce(BOUNDARY_FORCE_VALUE, 0, r.height, r.width, r.height);
			_boundary_right = new SmartWallForce(BOUNDARY_FORCE_VALUE, r.width, 0, r.width, r.height);
		}
		else { 
			_boundary_left = new SmartWallForce(0, 0, 0, 0, 0);
			_boundary_top = new SmartWallForce(0, 0, 0, 0, 0);
			_boundary_bottom = new SmartWallForce(0, 0, 0, 0, 0);
			_boundary_right = new SmartWallForce(0, 0, 0, 0, 0);
		}
		_boundariesSet = true;
	}
	
	
	/**
	 * UpdateBoundaryForces - updates the boundary wall forces in 
	 * the global force simulator to the contours of the specified 
	 * Rectangle.
	 * 
	 * Use this when the RenderBox is resized. 
	 */
	public void updateBoundaryForces(Rectangle r) {
		if(_enforceBounds && _boundariesSet) {
		    if(DEBUG) {System.out.println("Updating Boundaries");}
		    _boundary_left.setY2(r.height);
		    _boundary_top.setX2(r.width);
		    _boundary_bottom.setY1(r.height);
		    _boundary_bottom.setX2(r.width);
		    _boundary_bottom.setY2(r.height);
		    _boundary_right.setX1(r.width);
		    _boundary_right.setX2(r.width);
		    _boundary_right.setY2(r.height); 
		}
	} // -- end updateBoundaryForces
	
	
	/**
	 * updatesCurrentLayoutBounds - updates the layout bounds of the 
	 * current Layout in the RenderBox. If the RenderBox is running the 
	 * Layout, if first stops the Activity, then manipulates the Layout,
	 * then restarts (to avoid concurrent manipulations by threads).
	 */
	public void updateCurrentLayoutBounds(Rectangle new_r, boolean soft_bounds) {
	    if(DEBUG) {System.out.println("Updating Layout Bounds");}
	    boolean needToRestart = false;
	    // --- stop layout to prevent thread conflicts --- 
	    if(isRunningActivity()) {
	        if(DEBUG) {System.out.println("Layout is Running, so Stop");}
	        stopLayout();
	        needToRestart = true;
	    }

        // --- update layout bounds --- 
	    Activity a = getCurrentActivity();
        if (a instanceof LayoutActionList) {
            Layout l = ((LayoutActionList)a).getLayout();
            new_r = getAbsRectangle(getBounds());
            new_r.grow(-1 * DISPLAY_BOUNDARY_CUSHION, -1* DISPLAY_BOUNDARY_CUSHION);

            // SOFT bounds = maximal area coverage of old and new bounds
            if(soft_bounds) {
	            Rectangle2D old_r = l.getLayoutBounds();
	            double maxX = Math.max(new_r.getX() + new_r.getWidth(), old_r.getX() + old_r.getWidth());
	            double maxY = Math.max(new_r.getY() + new_r.getHeight(), old_r.getY() + old_r.getHeight());
	            double minX = Math.min(old_r.getX(), new_r.getX());
	            double minY = Math.min(old_r.getY(), new_r.getY());
	            new_r.setBounds((int)minX, (int)minY, (int)(maxX - minX), (int)(maxY - minY));
	        }

            updateBoundaryForces(new_r);
            l.setLayoutBounds(new_r);
        }
	        
        // --- restart --- 
        if(needToRestart) {
            startLayout();
        }
	} // end updateCurrentLayoutBounds

//    double oldX = old_r.getX();
//    double oldY = old_r.getY();
//    double oldH = old_r.getHeight();
//    double oldW = old_r.getWidth();
//    double newX = new_r.getX();
//    double newY = new_r.getY();
//    double newH = new_r.getHeight();
//    double newW = new_r.getWidth();
//    if(DEBUG) {System.out.println("OLD: x:" + oldX + " y:" + oldY + " h:" + oldH + " w:" + oldW);
//    System.out.println("NEW: x:" + newX + " y:" + newY + " h:" + newH + " w:" + newW);}
//    newW = maxX - minX;
//    newH = maxY - minY;
//    if(DEBUG) {System.out.println("FIN: x:" + newX + " y:" + newY + " h:" + newH + " w:" + newW);}
    

	
	/**
	 * GetAbsRectangle - takes a Rectangle in display coordinates and
	 * returns a Rectangle in absolute coordinates.
	 */
	public Rectangle getAbsRectangle(Rectangle r) {
	    if(DEBUG) {System.out.println("Getting Abs Rect");}
		Point p1 = new Point((int)r.getX(), (int)r.getY());
		Point p2 = new Point((int)(r.getX()+r.getWidth()), (int)(r.getY()+r.getHeight()));
        Point2D p1abs = getAbsoluteCoordinate(p1, new Point());
        Point2D p2abs = getAbsoluteCoordinate(p2, new Point());
        if(DEBUG) {System.out.println(" - disp: x1: " + p1.x + " y1:" + p1.y + " x2:" + p2.x + " y2:" + p2.y);}
        if(DEBUG) {System.out.println(" - absc: x1: " + p1abs.getX() + " y1:" + p1abs.getY() + " x2:" + p2abs.getX() + " y2:" + p2abs.getY());}
        r = new Rectangle((int)p1abs.getX(), (int)p1abs.getY(), (int)(p2abs.getX()-p1abs.getX()), (int)(p2abs.getY()-p1abs.getY()));
        if(DEBUG) {System.out.println(" - center: " + r.getCenterX() + ", " + r.getCenterY());}
        return r;
	} // -- end getAbsRectangle
	
	/**
	 * SetEnforceBoundaries - sets layout boundary enforcement on/off.
	 */
	public void setEnforceBounds(boolean b) {
	    _enforceBounds = b;
	} //
	
	
	/**
	 * GetEnforceBounds - returns true is layout bounds are enforced.
	 */
	public boolean getEnforceBounds() {
	    return _enforceBounds;
	} //
	
	public SmartWallForce getLeftBoundaryForce() {
	    return _boundary_left;
	}
	
	public SmartWallForce getRightBoundaryForce() {
	    return _boundary_right;
	}
	
	public SmartWallForce getTopBoundaryForce() {
	    return _boundary_top;
	}
	
	public SmartWallForce getBottomBoundaryForce() {
	    return _boundary_bottom;
	}
	
	// ------- View Modes / Control Listeners -------

	/**
	 * SetAnimateLayout - sets animation on/off.
	 */
	// TODO - should have listener interface to notify LayoutPlugin
	// if this is called from some other external force, so layout
	// can adjust accordingly.
	public void setAnimateLayout(boolean b) {
	    _animateLayout = b;
	}
	
	public boolean getAnimateLayout() {
	    return _animateLayout;
	}
	
	/**
	 * setViewMode
	 * 
	 * Sets the mouse action in the RenderBox to Pan, Zoom, Rotate, or PanZoom.
	 */
	public void setViewMode(String mode) {
//	    if(DEBUG) {System.out.println("Set View Tool Mode: " + mode);}
	    
	    // PAN only
	    if(mode.equals(VIEW_MODE_PAN)) {
	        _controls.removeControl(CTL_ROTATION_CONTROL);
	        _controls.removeControl(CTL_ZOOM_CONTROL);
	        _controls.addControl(CTL_PAN_CONTROL, new ActivePanControl(this));
	        _viewMode = VIEW_MODE_PAN;
	    }
	    // ZOOM only
	    else if(mode.equals(VIEW_MODE_ZOOM)) {
	        _controls.removeControl(CTL_ROTATION_CONTROL);
	        _controls.removeControl(CTL_PAN_CONTROL);
	        _controls.addControl(CTL_ZOOM_CONTROL, new ActiveZoomControl(this));
	        _viewMode = VIEW_MODE_ZOOM;
	    }
	    // ROTATE only
	    else if(mode.equals(VIEW_MODE_ROTATE)) {
	        _controls.removeControl(CTL_ZOOM_CONTROL);
	        _controls.removeControl(CTL_PAN_CONTROL);
	        _controls.addControl(CTL_ROTATION_CONTROL, new SmartRotationControl());
	        _viewMode = VIEW_MODE_ROTATE;
	    }	    
	    // PAN & ZOOM, by default
	    else {
	        //if(mode.equals(VIEW_MODE_PAN_ZOOM)) {
	        _controls.removeControl(CTL_ROTATION_CONTROL);
	        _controls.addControl(CTL_ZOOM_CONTROL, new ActiveZoomControl(this));
	        _controls.addControl(CTL_PAN_CONTROL, new ActivePanControl(this));
	        _viewMode = VIEW_MODE_PAN_ZOOM;
	    }
	}


	/**
	 * getViewMode
	 * 
	 * @return the name of the current view mode.
	 */
	public String getViewMode() {
	    return _viewMode;
	}
	

	/**
	 * FitGraphToWindow
	 * 
	 * Finds the smallest enclosing rectangle of the graph
	 * and expands this rectangle to the full window size.
	 */
	public void fitGraphToWindow() {
	    Iterator i = _registry.getNodeItems();
	    double x, y;
	    double maxX = Double.MIN_VALUE;
	    double maxY = Double.MIN_VALUE;
	    double minX = Double.MAX_VALUE;
	    double minY = Double.MAX_VALUE;
		while(i.hasNext()) {
		    NodeItem nitem = (NodeItem)i.next();
		    x = nitem.getX();
		    y = nitem.getY();
		    maxX = Math.max(maxX, x);
		    maxY = Math.max(maxY, y);
		    minX = Math.min(minX, x);
		    minY = Math.min(minY, y);
		}
		Rectangle rect = new Rectangle((int)minX, (int)minY, (int)(maxX-minX), (int)(maxY-minY));
        rect.grow(DISPLAY_BOUNDARY_CUSHION, DISPLAY_BOUNDARY_CUSHION);
        
	    DisplayLibExt.fitViewToBounds(this, rect, _animateLayout);

        if(_enforceBounds) {
            updateCurrentLayoutBounds(getBounds(), false);
        }
	}
	
	
	public void zoomGraph(double scale) {
	    // get center point
		Point p = new Point((int)(getWidth()/2), (int)(getHeight()/2));
//        Point2D pabs = getAbsoluteCoordinate(p, new Point());
        if(_animateLayout) {
//            this.animateZoomAbs(pabs, scale/this.getScale(), 1000);
            this.animateZoom(p, scale/this.getScale(), 1000);
        }
        else {
//            this.zoomAbs(pabs, scale/this.getScale());
            this.zoom(p, scale/this.getScale());
            this.repaint();
        }

        
	}
	
	/**
	 * ResizeNodes
	 */
	public void resizeNodes(double new_size) {
	    if(DEBUG) {System.out.println("Resizing Nodes to: " + new_size);}

//	    Iterator node_iter = _registry.getFilteredGraph().getNodes();
//	    while(node_iter.hasNext()) {
//	        Node n = (Node)node_iter.next();
//	        NodeItem nitem = _registry.getNodeItem(n);
//	        if(nitem != null) {
//	            nitem.updateSize(new_size);
//	        }
//	    }

	    Iterator i = _registry.getNodeItems();

		if(_animateLayout) {
			while(i.hasNext()) {
			    NodeItem nitem = (NodeItem)i.next();
			    nitem.updateSize(new_size);
			}
			
			ActionList animate = new ActionList(_registry, 500, 20);
			animate.add(new SizeAnimator());
			animate.add(new RepaintAction());
			// TODO - make this use the director
			animate.runNow();
			//_director.runNow();		
			//animate.cancel();
		}
		else {
			while(i.hasNext()) {
			    NodeItem nitem = (NodeItem)i.next();
			    nitem.setSize(new_size);
			}

		    repaint();
		}
	}

	
	
	/**
	 */
	public void unfixNodes() {
	    Iterator i = _registry.getNodeItems();

		while(i.hasNext()) {
		    NodeItem nitem = (NodeItem)i.next();
		    if(nitem.isFixed()) {
		        if(DEBUG) {System.out.println("FIXED NODE");}
		        nitem.setFixed(false);
		    }
		}
	}
	
	// ------- Controls Management -----
	
	public void panPerformed() {
	    if(DEBUG) {System.out.println("RenderBox Pan Performed");}
        if(_enforceBounds) {
            updateCurrentLayoutBounds(getBounds(), true);
        }
	}

	public void zoomPerformed() {
	    if(DEBUG) {System.out.println("RenderBox Zoom Performed");}
        if(_enforceBounds) {
            updateCurrentLayoutBounds(getBounds(), true);
        }
	}

	// ------- Layout Management -------
	
	/**
	 * SetActiveLayout
	 * 
	 * Sets the current selected layout that is controlled by
	 * startLayout and stopLayout.
	 */
	public void setActiveLayout(String name) {
		_director.setActive(name);
	}
	
	/**
	 * StartLayout
	 */
	public void startLayout() {
		if(_empty || _director.isRunning()) {
			return;
		}
		
		_director.runNow();
	}
	
	/**
	 * StopLayout
	 */
	public void stopLayout() {
		if(_empty) {
			return;
		}
		
		if(_director.isRunning()) {
			_director.stop();
		}
	}
	
	/**
	 * IsRunningActivity
	 */
	public boolean isRunningActivity() {
	    if(DEBUG) {System.out.println("isRunningActivity?: " + _director.isRunning());}
	    return _director.isRunning();
	}
	
	// TODO: add isRunningBackgroundActivity?
	
	public Activity getCurrentActivity() {
	    return _director.getCurrentActivity();
	}
	
	public String getCurrentLayoutName() {
	    return _director.getActive();
	}
	
	/**
	 * AddActivity
	 */
	public void addActivity(String name, Activity a) {
		_director.add(name, a);
	}
	
	/**
	 * RemoveActivity
	 */
	public void removeActivity(String name) {
		_director.remove(name);
	}

	public void clearHighlightedPaths() {
		Iterator i = _registry.getNodeItems();
		while(i.hasNext()) {
			PNode pnode = (PNode) ((VisualItem) i.next()).getEntity();
			pnode.setPathElement(false);
			pnode.setStartPoint(false);
			pnode.setEndPoint(false);
		}
		
		i = _registry.getEdgeItems();
		while(i.hasNext()) {
			PEdge pedge = (PEdge) ((VisualItem) i.next()).getEntity();
			pedge.setPathElement(false);
		}
		repaint();
		updateDisplay.runNow();
	}
	
	public void highlightPath(Path p) {
		// run through all the visible nodes
		Iterator i = _registry.getNodeItems();
		PNode pnode = null;
		while(i.hasNext()) {
			pnode = (PNode) ((VisualItem) i.next()).getEntity();
			pnode.setPathElement(p.contains(pnode.v()) ? true : false);
			pnode.setStartPoint(pnode.v().equals(p.start()) ? true : false);
			pnode.setEndPoint(false);
		}
		pnode.setEndPoint(true);
		
		i = _registry.getEdgeItems();
		while(i.hasNext()) {
			PEdge pedge = (PEdge) ((VisualItem) i.next()).getEntity();
			if(p.contains(pedge.e())) {
				pedge.setPathElement(true);
			} else {
				pedge.setPathElement(false);
			}
		}
		repaint();
		updateDisplay.runNow();
	}
	
	/** Randomly place the vertices of a graph on the drawing surface. */
	public void doRandomLayout() {
		if(_empty) {
			return;
		}
		
		// store previous active layout, just want to run random once
		String prev_activity = _director.getActive();
		_director.setActive(ACT_RANDOM);
		_director.runNow();
		_director.setActive(prev_activity);
        if(_enforceBounds) {
            updateCurrentLayoutBounds(getBounds(), false);
        }
	}
	
	public void doSemiRandomLayout() {
		if(_empty) {
			return;
		}
		String prev_activity = _director.getActive();
		_director.setActive(ACT_SEMIRANDOM);
		_director.runNow();
		_director.setActive(prev_activity);
        if(_enforceBounds) {
            updateCurrentLayoutBounds(getBounds(), false);
        }
	}
	
	public void doCenterLayout() {
	    // TODO
	    // show abs rectangle of window before
		Rectangle r1 = getAbsRectangle(getBounds());
		if(DEBUG) {System.out.println("R-Before: " + r1);}
	    // TODO
	    
		if(_empty) {
			return;
		}
		
		int ct = 0;
		double x = 0, y = 0;
		Iterator nodeIter = _registry.getNodeItems();
		while ( nodeIter.hasNext() ) {
			VisualItem item = (VisualItem) nodeIter.next();
			x += item.getLocation().getX();
			y += item.getLocation().getY();
			ct++;
		}
		x = x / (double) ct;
		y = y / (double) ct;
		if(DEBUG) {System.out.println("Centering to " + x + "," + y);}
        Point2D p_new = new Point2D.Double(x, y); 
        
        // TODO - ANIM here
        if(_animateLayout) {
            animatePanAndZoomToAbs(p_new, 1.0, 1000);
        }
        else {
            // TODO - can't figure out why this fails when
            // we zoom out
            // zoomAbs(p_new, 1.0);
            // panToAbs(p_new);
            // repaint();
            
            animatePanAndZoomToAbs(p_new, 1.0, 20);
        }
        
        if(_enforceBounds) {
        //    updateCurrentLayoutBounds(getBounds(), false);
        }
		repaint();

		// TODO
	    // show abs rectangle of window after
		Rectangle r2 = getAbsRectangle(getBounds());
		if(DEBUG) {System.out.println("R-After: " + r2);}
	    // TODO

	
	}
	
	/**
	 * Saves an image by opening a file chooser and saving to specified
	 * disk location.
	 *
	 * Can write JPG and PNG formats.
	 */
	public void handleSaveImage() {
		// Ensure layout is not running
		stopLayout();
		
		// create file chooser and show dialog
		javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
		fc.addChoosableFileFilter(new PNGFilter());
		fc.setFileFilter(new JPGFilter());
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			FileFilter filter = fc.getFileFilter();
			String outputExt;
			if(filter instanceof PNGFilter) {
				outputExt = FileFilterUtils.png;
			} else {
				// JPG is Default
				outputExt = FileFilterUtils.jpg;
			}
			
			// TODO: should we use AbsolutePath or CanonicalPath???
			// TODO: all of the logic here should probably be moved to NMenu
			// check if they already typed file extension
			String fileName = file.getName().toLowerCase();
			// if(!file.getName().toLowerCase().contains("." + outputExt)) { // 1.5 only apparently
			if(fileName.indexOf("." + outputExt) < 0) {
				file = new File(file.getAbsolutePath() + "." + outputExt);
			}
			
			try {
				FileOutputStream f_out = new FileOutputStream(file);
				this.saveImage(f_out, outputExt, 1.0);
				f_out.close();  // need to close stream to view file
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void doSaveVertexLocations() {
		// when graph is cleared, save the locations of the
		// current nodes in the Model (MainPanel._originalGraph)
		// so that if the action taken is a filter action,
		// we can place the nodes in the same place.
		if(_registry != null) {
			Iterator i = _registry.getNodeItems();
			while(i.hasNext()) {
				VisualItem item = (VisualItem) i.next();
				Vertex v = _ctl.getModel().findVertex(((PNode) item.getEntity()).v().id());
				if(null != v) {
					Point2D.Float loc = new Point2D.Float((float) item.getLocation().getX(), (float) item.getLocation().getY());
					v.setDatum(new Datum(DATUM_LASTLOCATION, loc));
				}
			}
		}
	}
	
	public RenderSettings getRenderSettings() {
		return _settings;
	}
	
	public ItemRegistry getItemRegistry() {
		return _registry;
	}
	
	public void postPaint(java.awt.Graphics2D g) {
		if(_empty) {
			return;
		}
		
		// overridden method to paint stuff _after_ graph elements
		// have been drawn
		FontMetrics fm = g.getFontMetrics();
		int fheight = fm.getAscent();
		
		
		// show node name/id
		Iterator i =_registry.getNodeItems();
		while(_settings.getBoolean(RenderSettings.SHOW_LABELS) && i.hasNext()) {
			if(_empty) return; // TODO: prefuse seems to use threads...?
			NodeItem item = (NodeItem) i.next();
			PNode n = (PNode) item.getEntity();
			Vertex v = n.v();
			int x = 10 + (int) _registry.getNodeItem(n).getX();
			int y = 10 + (int) _registry.getNodeItem(n).getY();
			Rectangle rect = new Rectangle(x - 2, y - fheight, fm.stringWidth(v.displayId()) + 4, fheight + 2);
			setAlpha(g, TRANSPARENCY);
			g.setPaint(Color.WHITE);
			g.fill(rect);
			setAlpha(g, 1.0f);
			
			g.setPaint(Color.BLACK);
			g.draw(rect);
			g.drawString(v.displayId(), x, y);
		}
		
		// show edge length
		i =_registry.getEdgeItems();
		double x1, y1, x2, y2;
		double theta;
		g.setPaint(Color.RED);
		g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 10));
		while(_settings.getBoolean(RenderSettings.SHOW_LENGTH) && i.hasNext()) {
			if(_empty) return; // TODO: prefuse seems to use threads...?
			EdgeItem item = (EdgeItem) i.next();
			PEdge p = (PEdge) item.getEntity();
			PNode v1 = PNode.v2p((Vertex) p.e().getEnds().car());
			PNode v2 = PNode.v2p((Vertex) p.e().getEnds().cdr());
			x1 = _registry.getNodeItem(v1).getX();
			y1 = _registry.getNodeItem(v1).getY();
			x2 = _registry.getNodeItem(v2).getX();
			y2 = _registry.getNodeItem(v2).getY();
			theta = getTheta((int) x1, (int) y1, (int) x2, (int) y2);
			String label = "[" + p.e().length() + "]";
			
			//g.translate((x1+x2)/2, (y1+y2)/2);
			//g.rotate(theta);
			g.drawString(label, (int) (x1 + (x2 - x1) * (3.0 / 5.0)),
					(int) (y1 + (y2 - y1) * (3.0 / 5.0)));
			
			//g.rotate(-theta);
			//g.translate(-(x1+x2)/2, -(y1+y2)/2);
		}
		
		/*
		String [][] test = {{"test", "12"},
		{"\\hline",""},
		{"asd","3232"},
		{"\\hline",""},
		{"test2","135"}};
		renderTable(g, (int) getDisplayX() + 5, (int) getDisplayY() + 5, "", test);
		 */
	}
	
	private double getTheta(int x1, int y1, int x2, int y2) {
		return Math.atan((double) (y1 - y2) / (double) (x1 - x2)) + (x1 > x2 ? 0 : Math.PI);
	}
	
	private void setAlpha(Graphics2D g, float alpha) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	}
	
	private class MouseAdapter extends ControlAdapter {
		private RenderBox _parent;
		private int _xOffset, _yOffset;
		
		public MouseAdapter(RenderBox parent) {
			_parent = parent;
		}
		
		public void mouseClicked(java.awt.event.MouseEvent e) {
			/*
			if(_popup != null) {
				// close it
				_popup.hide();
				_popup = null;
				repaint();
			}
			*/
			repaint();
		}
		
		public void itemEntered(VisualItem item, MouseEvent e) {
		    if(DEBUG) {System.out.println("Item Entered");}
			PElement p = (PElement) item.getEntity();
			nv2d.graph.GraphElement gElement = p.getNV2DGraphElement();
			((Display)e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			item.setHighlighted(true);
//			_registry.touch(item.getItemClass());
			_parent.setToolTipText(PopupProp.createHtml(gElement));	// turn off tooltips
//			repaint();
			
			updateDisplay.runNow();
		}
		
		public void itemPressed(VisualItem item, MouseEvent e) {
			PElement p = (PElement) item.getEntity();
			
			/*
			// first click selects, second click deselects
			if(checkMask(e.getModifiers(), MouseEvent.BUTTON1_MASK) && item.getEntity() instanceof PElement) {
				p.setSelected(!p.isSelected());
				
				// when showing popups, coordinates are calculated relative to the
				// top level container, so we need to add an offset to the popup to
				// fix this.
				Point pRenderBox = _parent.getLocationOnScreen();
				Point pWindow = _ctl.getView().gui().getLocationOnScreen();
				_xOffset = (int) (pRenderBox.getX() - pWindow.getX());
				_yOffset = (int) (pRenderBox.getY() - pWindow.getX());
				
				if(_popup == null) {
					// show popup
					nv2d.graph.GraphElement geData = p.getNV2DGraphElement();
					_popup = _pFactory.getPopup(_parent, new PopupProp(_ctl, geData), e.getX() + _xOffset, e.getY() + _yOffset);
					_popup.show();
				} else {
					_popup.hide();
					nv2d.graph.GraphElement geData = p.getNV2DGraphElement();
					_popup = _pFactory.getPopup(_parent, new PopupProp(_ctl, geData), e.getX() + _xOffset, e.getY() + _yOffset);
					_popup.show();
				}
				repaint();
			}
			*/
			
			_lastItemClicked = item;
			maybeShowPopup(e);
			repaint();
			updateDisplay.runNow();
		}
		
		public void itemReleased(VisualItem item, MouseEvent e) {
			maybeShowPopup(e);
			repaint();
			updateDisplay.runNow();
		}
		
		public void itemExited(VisualItem item, MouseEvent e) {
		    if(DEBUG) {System.out.println("Item Exited");}
			((Display)e.getSource()).setCursor(Cursor.getDefaultCursor());
			item.setHighlighted(false);
//			_registry.touch(item.getItemClass());
			_parent.setToolTipText(null);	// turn off tooltips
//			repaint();
			updateDisplay.runNow();
		}
		
		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				_vertexMenu.getMenu().show(e.getComponent(), e.getX(), e.getY());
				repaint();
				updateDisplay.runNow();
			}
		}
		
		private boolean checkMask(int value, int mask) {
			return (value & (~ mask)) == 0;
		}
	}
	
	class PopupMenu {
		private JMenuItem _centerDegreeFilter = new JMenuItem("Center DegreeFilter here");
		private JMenuItem _clearPath = new JMenuItem("Clear Path");
		private JMenuItem _setStartPoint = new JMenuItem("Start Path");
		private JMenuItem _setEndPoint = new JMenuItem("End Path");
		
		public PopupMenu() {
			_apspSource = null;
			
			_centerDegreeFilter.setToolTipText("Set this vertex as the center vertex for the degree filter");
			_centerDegreeFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// the current graph does not point to the same (i.e. filter runs
					// on _originalGraph while we are grabbing a node from _g
					String id = ((PNode) _lastItemClicked.getEntity()).v().id();
					
					Object [] fargs = new Object[2];
					fargs[0] = _ctl.getModel().findVertex(id);
					fargs[1] = _ctl.getDegreeFilter().lastArgs()[1];	// what was the last DegreeFilter setting used?
					// this menu item is only shown when a degreefilter is active
					// so this next line is safe
					_ctl.runFilter(fargs, true);
				}
			});
			
			
			_clearPath.setToolTipText("Clear any highlighted paths.");
			_clearPath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clearHighlightedPaths();
					updateDisplay.runNow();
				}
			});
			
			_setStartPoint.setToolTipText("Set the starting point for a shortest path calculation.");
			_setStartPoint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionPerformedSetStartPoint(e);
					updateDisplay.runNow();
				}
			});
			
			_setEndPoint.setToolTipText("Calculate and highlight the all-pairs shortest path from the start vertex to this vertex.");
			_setEndPoint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionPerformedSetEndPoint(e);
					updateDisplay.runNow();
					
//					ActionList a = new ActionList(_registry, 1000, 20);
//					a.add(new RepaintAction());
//					a.add(new Colorizer());
//					a.add(new LegendColorizer(_ctl));
//					a.runNow();
				}
			});
		}
		
		public JPopupMenu getMenu() {
			JPopupMenu m = new JPopupMenu();
			JMenu followUrl = new JMenu("Associated URLs");
			Vertex v;
			Iterator i;
			int ct;
			
			// find url datums and let the user follow the link
			v = ((PNode) _lastItemClicked.getEntity()).v();
			i = v.getVisibleDatumSet().iterator();
			ct = 0;
			while(i.hasNext()) {
				final Datum d = (Datum) i.next();
				if(d.get() instanceof java.net.URL) {
					ct++;
					JMenuItem link = new JMenuItem(d.get().toString());
					link.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								AppletContext act = ((Applet) _ctl.getView().getRootPaneContainer()).getAppletContext();
								act.showDocument((java.net.URL) d.get(), "nv2dnetshow");
							} catch (ClassCastException exception) {
								System.err.println("You must use the applet to follow links.");
							}
						}
					});
					followUrl.add(link);
				}
			}
			
			if(ct > 0) {
				m.add(followUrl);
				m.add(new JSeparator());
			}
			
			if(_ctl.getFilter() instanceof DegreeFilter) {
				m.add(_centerDegreeFilter);
				m.add(new JSeparator());
			}
			m.add(_clearPath);
			m.add(_setStartPoint);
			m.add(_setEndPoint);
			return m;
		}
	}
	
	private void actionPerformedSetEndPoint(ActionEvent e) {
		Vertex v = ((PNode) _lastItemClicked.getEntity()).v();
		if(v.equals(_apspSource)) {
			_ctl.getView().errorPopup("Path Error",
					"You picked the same node " + _apspSource.id() + " to be the source and destination.",
					null);
			return;
		}
		
		Path p = _g.shortestPath(_apspSource, v);
		
		if(p == null) {
			_ctl.getView().errorPopup("Path Error",
					"There is no path from " + _apspSource.id() + " to " + v.id() + ".",
					null);
			return;
		}

		clearHighlightedPaths();
		highlightPath(p);
		updateDisplay.runNow();
	}

	private void actionPerformedSetStartPoint(ActionEvent e) {
		try {
			Vertex v = ((PNode) _lastItemClicked.getEntity()).v();
			_apspSource = v;
		} catch (java.lang.ClassCastException err) {
			return;
		}
	}
}
