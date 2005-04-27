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

package nv2d.render;

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

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefusex.controls.DragControl;
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

/**
 * Creates a new graph and draws it on the screen.
 */
public class RenderBox extends Display {
	public static final float TRANSPARENCY = 0.7f;
	public static final String DATUM_LASTLOCATION = "__renderbox:lastloc";
	
	private NController _ctl;
	private ItemRegistry _registry;
	private RenderSettings _settings;
	private Graph _g;
	private ActivityDirector _director;
	private ControlManager _controls;
	
	private PopupFactory _pFactory;
	private Popup _popup = null;
	
	private boolean _empty;
	private boolean _isInitialized;
	
	// various colorizers
	ColorFunction _colorizer;
	ColorFunction _legendColorizer;
	
	private PopupMenu _vertexMenu;
	
	// for the mouse interface
	private static VisualItem _lastItemClicked;

	// Activity and Layout String Names
	public static final String ACT_BACKGROUND = "standard_BackgroundActivity";
	public static final String ACT_SEMIRANDOM = "standard_SemiRandomLayout";
	public static final String ACT_RANDOM = "standard_RandomLayout";
	public static final String ACT_FORCEDIRECTED = "standard_ForceDirectedLayout";

	// Control Listener String Names
	public static final String CTL_MOUSE_ADAPTOR = "standard_MouseAdaptor";
	public static final String CTL_DRAG_CONTROL = "standard_DragControl";
	public static final String CTL_PAN_CONTROL = "standard_PanControl";
	public static final String CTL_ZOOM_CONTROL = "standard_ZoomControl";
	public static final String CTL_ROTATION_CONTROL = "standard_RotationControl";
	
	
	public RenderBox(NController ctl) {
		// (1) convert NV2D graph to a data structure usable by Prefuse
		// (2) create a new item registry
		//  the item registry stores all the visual
		//  representations of different graph elements
		super(new ItemRegistry(new DefaultGraph(true)));

		// establish settings controller
		_settings = new RenderSettings();
		_pFactory = PopupFactory.getSharedInstance();
		
		_ctl = ctl;

		// setup the popup menu for vertices
		_vertexMenu = new PopupMenu();
		_lastItemClicked = null;

		_director = new ActivityDirector();
		_controls = new ControlManager();
		initStandardControls();

		// create a new display component to show the data
		// setSize(400,400);
		// pan(350, 350);
		// lets users drag nodes around on screen (Display class method)
		addControlListener(_controls.getControl(CTL_MOUSE_ADAPTOR));
		addControlListener(_controls.getControl(CTL_DRAG_CONTROL));
		addControlListener(_controls.getControl(CTL_PAN_CONTROL));
		addControlListener(_controls.getControl(CTL_ZOOM_CONTROL));
		
		_empty = true;
		
		initialize(null);
		
		//_isInitialized = false;
	}


	public void useLegendColoring() {
		_colorizer.setEnabled(false);
		_legendColorizer.setEnabled(true);
		
		_legendColorizer.run(_registry, 0.0);
	}

	
	public void useDefaultColoring() {
		_colorizer.setEnabled(true);
		_legendColorizer.setEnabled(false);
		
		_colorizer.run(_registry, 0.0);
	}

	
	public void clear() {
//	    System.out.println("Clearing RenderBox");
		if(_empty) {
			return;
		}
		
		doSaveVertexLocations();
		_registry.clear();
		//_director.clear();
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
	
	// TODO - initialize is called twice.  why does this happen?
	// necessitates some if statements to tell if it has happened
	// before, but works for now with bug.
	//
	// Also, is it possible to initialize the renderbox before
	// loading all of the plugins.  If the plugin requires use
	// of the renderbox or registry, it is difficult to handle
	// through the plugin constructor or init methods.
	//
	// -sp
	public void initialize(Graph g) {
//	    System.out.println("** Initializing Renderbox");
        _g = g;
        _registry = getRegistry();
		
		if(g != null) {
			_registry.setGraph(new PGraph(g));
		}

        _colorizer = new Colorizer();
        _legendColorizer = new LegendColorizer(_ctl);

        _colorizer.setEnabled(true);
        _legendColorizer.setEnabled(false);

        // antialias?
        setHighQuality(_settings.getBoolean(RenderSettings.ANTIALIAS));

        //if (!_isInitialized) {
        initStandardLayouts();
        if (!_director.isRunning()) {
            _director.setActive(ACT_FORCEDIRECTED);
        }
        if (!_director.isBackgroundRunning()) {
            _director.setBackground(ACT_BACKGROUND);
            _director.startBackground();
        }
        //}

        _empty = false;

        // Begin by randomly setting all the items
        doSemiRandomLayout();

        //_isInitialized = true;
	}

	
	/**
	 * Standard Controls
	 */
	private void initStandardControls() {
	    _controls.addControl(CTL_MOUSE_ADAPTOR, new MouseAdapter(this));
	    _controls.addControl(CTL_DRAG_CONTROL, new DragControl());
	    _controls.addControl(CTL_PAN_CONTROL, new PanControl());
	    _controls.addControl(CTL_ZOOM_CONTROL, new ZoomControl());
	    _controls.addControl(CTL_ROTATION_CONTROL, new RotationControl());
	    // TODO - add more
	}
	
	
	/**
     * Standard Layouts
     */
	private void initStandardLayouts() {
		ForceSimulator _fsim;
		ForceDirectedLayout _flayout;
		RandomLayout _randomLayout;
		SemiRandomLayout _semiRandomLayout;
		ActionList s, r, f;
		
		GraphFilter graphFilter = new GraphFilter();
		RepaintAction repaintAction = new RepaintAction();
	    
		// Default Background Activity
		ActionList background = new ActionList(_registry, -1, 20);
		background.add(_colorizer);
		background.add(_legendColorizer);
		//background.add(repaintAction);
		_director.add(ACT_BACKGROUND, background);
		
		
	    // Semi-Random Layout
		_semiRandomLayout = new SemiRandomLayout(_ctl);
		s = new ActionList(_registry);
		s.add(graphFilter);
		//s.add(_colorizer);
		//s.add(_legendColorizer);
		s.add(repaintAction);
		s.add(_semiRandomLayout);
		_director.add(ACT_SEMIRANDOM, s);
		
		// init random layout
		_randomLayout = new RandomLayout();
		r = new ActionList(_registry);
		r.add(graphFilter);
		//r.add(_colorizer);
		//r.add(_legendColorizer);
		r.add(repaintAction);
		r.add(_randomLayout);
		_director.add(ACT_RANDOM, r);
		
		// init fd layout
		_fsim = new ForceSimulator();
		_fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
		_fsim.addForce(new SpringForce(4E-5f, 75f));
		_fsim.addForce(new DragForce(-0.005f));
		_flayout = new ForceDirectedLayout(_fsim, false, false);
		
		f = new ActionList(_registry, -1, 20);
		f.add(graphFilter);
		//f.add(_colorizer);
		//f.add(_legendColorizer);
		f.add(repaintAction);
		f.add(_flayout);
		_director.add(ACT_FORCEDIRECTED, f);
	}
	
	
	// ---- Control Listeners ----
	
	// TODO - implement all of this as ControlSchemes in the ControlManager
	public void setRotateMode(boolean mode) {
	    if(mode) {
	        removeControlListener(_controls.getControl(CTL_DRAG_CONTROL));
	        removeControlListener(_controls.getControl(CTL_PAN_CONTROL));
	        addControlListener(_controls.getControl(CTL_ROTATION_CONTROL));	        
	    }
	    else {
	        removeControlListener(_controls.getControl(CTL_ROTATION_CONTROL));	
	        addControlListener(_controls.getControl(CTL_DRAG_CONTROL));
	        addControlListener(_controls.getControl(CTL_PAN_CONTROL));
	    }
	}
	
	
	// ---- Layouts ----
	
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
	}
	
	public void doSemiRandomLayout() {
		if(_empty) {
			return;
		}
		String prev_activity = _director.getActive();
		_director.setActive(ACT_SEMIRANDOM);
		_director.runNow();
		_director.setActive(prev_activity);		
	}
	
	public void doCenterLayout() {
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
		panToAbs(new java.awt.geom.Point2D.Double(x, y));
		repaint();
	}
	
	/**
	 * Saves an image by opening a file chooser and saving to specified 
	 * disk location
	 */
	public void handleSaveImage() {
	    // Ensure layout is not running
	    stopLayout();

	    // create file chooser and show dialog
	    javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
	    int returnVal = fc.showSaveDialog(this);
	    if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
	        File file = fc.getSelectedFile();
		    try {
		        this.saveImage(new FileOutputStream(file), "JPG", 1.0);
		    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }
	    }
	}

    
	/** TODO: Saves the current visualization to a PNG or JPEG file.
	 *
	 * @param filename	the name of the file to save to.
	 */
/*	public void saveVisualFile(String filename) {
		BufferedImage bi = new BufferedImage(
				(int) getWidth(),
				(int) getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		
		// draw what we have
		this.update(g);
		
		// saves according to extension.  if extension is invalid
		// (i.e. not .jpg or .png) will default to jpg file.
		try {
			File f = new File(filename);
			if(filename.substring(filename.length() - 4).equals(".png")) {
				ImageIO.write((RenderedImage) bi, "png", f);
			} else {
				ImageIO.write((RenderedImage) bi, "jpg", f);
			}
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
		g.dispose();
	}*/

	
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
	
	/*
	private void renderTable(Graphics2D g, int x, int y, String layout, String [][] content) {
		// overridden method to paint stuff _after_ graph elements
		// have been drawn
		if(content.length > 0) {
			return;
		}
		String HLINE = "\\hline";
		FontMetrics fm = g.getFontMetrics();
		int padding = 5;
		int fheight = fm.getAscent() + padding;
		int [] maxWidth = new int[content[0].length];
		int totalx, width, i, j;
		int cols;
		
		cols = 0;
		for(i = 0; i < content.length; i++) {
			if(content[i][0].equals(HLINE)) {
				continue;
			}
			for(j = 0; j < content[i].length; j++) {
				width = fm.stringWidth(content[i][j]);
				maxWidth[j] = (width > maxWidth[j] ? width : maxWidth[j]);
			}
			cols++;
		}
		
		totalx = 0;
		for(i = 0; i < maxWidth.length; i++ ) {
			totalx += maxWidth[i];
		}
		
		g.setPaint(Color.WHITE);
		setAlpha(g, TRANSPARENCY);
		g.fill(new Rectangle(x, y, totalx, fheight * cols));
		setAlpha(g, 1.0f);
		g.setPaint(Color.BLACK);
		g.draw(new Rectangle(x, y, totalx, fheight * cols));
		
		y = y + fm.getAscent();
		for(i = 0; i < content.length; i++) {
			if(content[i][0].equals(HLINE)) {
				g.draw(new Line2D.Double((double) x, (double) (y + padding),
						(double) (x + totalx), (double) (y + padding)));
			}
			for(j = 0; j < content[i].length; j++) {
				
			}
		}
	}
	*/
	
	private class MouseAdapter extends ControlAdapter {
		private RenderBox _parent;
		private int _xOffset, _yOffset;
		
		public MouseAdapter(RenderBox parent) {
			_parent = parent;
		}
		
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if(_popup != null) {
				// close it
				_popup.hide();
				_popup = null;
				repaint();
			}
		}
		
		public void itemEntered(VisualItem item, MouseEvent e) {
			((Display)e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			item.setHighlighted(true);
			repaint();
		}
		
		public void itemPressed(VisualItem item, MouseEvent e) {
			PElement p = (PElement) item.getEntity();
			
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
			
			_lastItemClicked = item;
			maybeShowPopup(e);
		}
		
		public void itemReleased(VisualItem item, MouseEvent e) {
			maybeShowPopup(e);
		}
		
		public void itemExited(VisualItem item, MouseEvent e) {
			((Display)e.getSource()).setCursor(Cursor.getDefaultCursor());
			item.setHighlighted(false);
			repaint();
		}
		
		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				_vertexMenu.getMenu().show(e.getComponent(), e.getX(), e.getY());
				repaint();
			}
		}
		
		private boolean checkMask(int value, int mask) {
			return (value & (~ mask)) == 0;
		}
	}
	
	class PopupMenu {
		private Vertex _apspSource;
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
				}
			});
			
			_setStartPoint.setToolTipText("Set the starting point for a shortest path calculation.");
			_setStartPoint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Vertex v = ((PNode) _lastItemClicked.getEntity()).v();
						_apspSource = v;
					} catch (java.lang.ClassCastException err) {
						return;
					}
				}
			});
			
			_setEndPoint.setToolTipText("Calculate and highlight the all-pairs shortest path from the start vertex to this vertex.");
			_setEndPoint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
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
				}
			});
		}
		
		public JPopupMenu getMenu() {
			JPopupMenu m = new JPopupMenu();
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
}
