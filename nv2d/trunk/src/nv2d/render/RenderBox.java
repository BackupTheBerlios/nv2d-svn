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
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

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

	private NController _ctl;
	private ItemRegistry _registry;
	private ActionList _actions;
	private RenderSettings _settings;
	private Graph _g;

	private boolean _empty;

	private ForceSimulator _fsim;
	private ForceDirectedLayout _flayout;

	private PopupMenu _vertexMenu;

	// for the mouse interface
	private static VisualItem _lastItemClicked;
	
	public RenderBox(NController ctl) {
		// (1) convert NV2D graph to a data structure usable by Prefuse
		// (2) create a new item registry
		//  the item registry stores all the visual
		//  representations of different graph elements
		super(new ItemRegistry(new DefaultGraph(true)));

		// establish settings controller
		_settings = new RenderSettings();

		_ctl = ctl;
		// setup the popup menu for vertices
		_vertexMenu = new PopupMenu();
		_lastItemClicked = null;

		// create a new display component to show the data
		setSize(400,400);
		pan(350, 350);
		// lets users drag nodes around on screen (Display class method)
		addControlListener(new MouseController());
		addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());

		_empty = true;
	}

	public void clear() {
		if(_empty) return;
		_registry.clear();
		_actions = null;
		_fsim = null;
		_flayout = null;
		_empty = true;
	}

	public void initialize(Graph g) {
		_g = g;
		_registry = getRegistry();
		_registry.setGraph(new PGraph(g));

		// set up attract/repulse
		_fsim = new ForceSimulator();
        _fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
        _fsim.addForce(new SpringForce(4E-5f, 75f));
        _fsim.addForce(new DragForce(-0.005f));
		_flayout = new ForceDirectedLayout(_fsim, false, false);

		// create a new action list that
		// (a) filters visual representations from the original graph
		// (b) performs a random layout of graph nodes
		// (c) calls repaint on displays so that we can see the result
		_actions = new ActionList(_registry, -1, 20);
		_actions.add(new GraphFilter());
		_actions.add(new Colorizer()); 		// colors nodes & edges
		_actions.add(new RepaintAction());

		// antialias?
		setHighQuality(_settings.getBoolean(RenderSettings.ANTIALIAS));

		_empty = false;

		// now execute the actions to visualize the graph
		_actions.runNow();
		doRandomLayout();
	}

	public void startForceDirectedLayout() {
		if(_empty) {
			return;
		}
		_actions.add(_flayout);
	}

	public void stopForceDirectedLayout() {
		if(_empty) {
			return;
		}
		_actions.remove(_flayout);
	}

	/** Saves the current visualization to a PNG or JPEG file.
	 *
	 * @param filename	the name of the file to save to.
	 */
	public void saveVisualFile(String filename) {
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
	}

	public void doRandomLayout() {
		if(_empty) {
			return;
		}
		// run once action list
		ActionList act = new ActionList(_registry);
		act.add(new RandomLayout());
		act.runNow();
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
			Rectangle rect = new Rectangle(x - 2, y - fheight, fm.stringWidth(v.id()) + 4, fheight + 2);
			setAlpha(g, TRANSPARENCY);
			g.setPaint(Color.WHITE);
			g.fill(rect);
			setAlpha(g, 1.0f);

			g.setPaint(Color.BLACK);
			g.draw(rect);
			g.drawString(v.id(), x, y);
		}

		// show edge length
		i =_registry.getEdgeItems();
		double x1, y1, x2, y2;
		double theta;
		g.setPaint(Color.RED);
		g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 8));
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

			g.translate((x1+x2)/2, (y1+y2)/2);
			g.rotate(theta);
			g.drawString(label, 0, 0);
			g.rotate(-theta);
			g.translate(-(x1+x2)/2, -(y1+y2)/2);
		}

		String [][] test = {{"test", "12"},
			{"\\hline",""},
			{"asd","3232"},
			{"\\hline",""},
			{"test2","135"}};
		renderTable(g, (int) getDisplayX() + 5, (int) getDisplayY() + 5, "", test);
	}

	private double getTheta(int x1, int y1, int x2, int y2) {
		return Math.atan((double) (y1 - y2) / (double) (x1 - x2)) + (x1 > x2 ? 0 : Math.PI);
	}

	private void setAlpha(Graphics2D g, float alpha) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	}

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

	class MouseController extends ControlAdapter {
		public void itemEntered(VisualItem item, MouseEvent e) {
			((Display)e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			item.setHighlighted(true);
		}

		// TODO: does not behave exactly as it should for vertices
		public void itemPressed(VisualItem item, MouseEvent e) {
			// first click selects, second click deselects
			item.setFixed(!item.isFixed());
			_lastItemClicked = item;
			maybeShowPopup(e);
		}

		public void itemReleased(VisualItem item, MouseEvent e) {
			maybeShowPopup(e);
		}

		public void itemExited(VisualItem item, MouseEvent e) {
			((Display)e.getSource()).setCursor(Cursor.getDefaultCursor());
			item.setHighlighted(false);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				_vertexMenu.getMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	class PopupMenu {
		private Vertex _apspSource;
		private JMenuItem _centerDegreeFilter = new JMenuItem("Center DegreeFilter here");
		private JMenuItem _setStartPoint = new JMenuItem("Set start vertex");
		private JMenuItem _setEndPoint = new JMenuItem("Highlight APSP");

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
					fargs[1] = new Integer(1);
					// this menu item is only shown when a degreefilter is active
					// so this next line is safe
					_ctl.runFilter(fargs);
				}
			});

			_setStartPoint.setToolTipText("Set the starting point for a shortest path calculation.");
			_setStartPoint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Vertex v = ((PNode) _lastItemClicked.getEntity()).v();
					_apspSource = v;
				}
			});

			_setEndPoint.setToolTipText("Calculate and highlight the all-pairs shortest path from the start vertex to this vertex.");
			_setEndPoint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Vertex v = ((PNode) _lastItemClicked.getEntity()).v();
					if(v.equals(_apspSource)) {
						JOptionPane.showMessageDialog(null,
							"You picked the same node " + _apspSource.id() + " to be the source and destination.",
							"Path Error",
							JOptionPane.ERROR_MESSAGE);
						return;
					}

					Path p = _g.shortestPath(_apspSource, v);

					if(p == null) {
						JOptionPane.showMessageDialog(null,
							"There is no path from " + _apspSource.id() + " to " + v.id() + ".",
							"Path Error",
							JOptionPane.ERROR_MESSAGE);
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
			m.add(_setStartPoint);
			m.add(_setEndPoint);
			return m;
		}
	}
}
