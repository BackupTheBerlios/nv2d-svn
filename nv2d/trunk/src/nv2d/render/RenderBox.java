package nv2d.render;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.AlphaComposite;
import java.awt.FontMetrics;
//import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
//import java.awt.geom.AffineTransform;
//import java.awt.image.BufferedImage;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
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

import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.graph.Edge;

/**
 * Creates a new graph and draws it on the screen.
 */
public class RenderBox extends Display {
	public static final float TRANSPARENCY = 0.7f;

	private ItemRegistry _registry;
	private ActionList _actions;
	private RenderSettings _settings;

	private ForceSimulator _fsim;
	private ForceDirectedLayout _flayout;
	
	public RenderBox(Graph g) {
		// (1) convert NV2D graph to a data structure usable by Prefuse
		// (2) create a new item registry
		//  the item registry stores all the visual
		//  representations of different graph elements
		super(new ItemRegistry(new PGraph(g)));
		_registry = getRegistry();

		// set up attract/repulse
		_fsim = new ForceSimulator();
        _fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
        _fsim.addForce(new SpringForce(4E-5f, 75f));
        _fsim.addForce(new DragForce(-0.005f));
		_flayout = new ForceDirectedLayout(_fsim, false, false);
		
		// create a new display component to show the data
		setSize(400,400);
		pan(350, 350);
		// lets users drag nodes around on screen (Display class method)
		addControlListener(new MouseController());
		addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());
		
		// create a new action list that
		// (a) filters visual representations from the original graph
		// (b) performs a random layout of graph nodes
		// (c) calls repaint on displays so that we can see the result
		_actions = new ActionList(_registry, -1, 20);
		_actions.add(new GraphFilter());
		_actions.add(new Colorizer()); 		// colors nodes & edges
		_actions.add(new RepaintAction());

		// establish settings controller
		_settings = new RenderSettings();
	}

	public void init() {
		// now execute the actions to visualize the graph
		_actions.runNow();
		doRandomLayout();
	}

	public void startForceDirectedLayout() {
		_actions.add(_flayout);
	}

	public void stopForceDirectedLayout() {
		_actions.remove(_flayout);
	}

	public void doRandomLayout() {
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
		// overridden method to paint stuff _after_ graph elements
		// have been drawn
		FontMetrics fm = g.getFontMetrics();
		int fheight = fm.getAscent();


		// show node name/id
		Iterator i =_registry.getNodeItems();
		while(_settings.getBoolean(RenderSettings.SHOW_LABELS) && i.hasNext()) {
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
		while(_settings.getBoolean(RenderSettings.SHOW_LENGTH) && i.hasNext()) {
			EdgeItem item = (EdgeItem) i.next();
			PEdge p = (PEdge) item.getEntity();
			PNode v1 = PNode.v2p((Vertex) p.e().getEnds().car());
			PNode v2 = PNode.v2p((Vertex) p.e().getEnds().cdr());
			double x1 = _registry.getNodeItem(v1).getX();
			double y1 = _registry.getNodeItem(v1).getY();
			double x2 = _registry.getNodeItem(v2).getX();
			double y2 = _registry.getNodeItem(v2).getY();
			double theta = java.lang.Math.atan(((y2 - y1)/(x1 - x2)));
			String label = "[" + p.e().length() + "]";

			g.translate((x1+x2)/3, (y1+y2)/3);
			//g.rotate(theta);
			g.setPaint(new Color(255, 0, 0));
			g.drawString(label, 0, 0);
			//g.rotate(-theta);
			g.translate(-(x1+x2)/3, -(y1+y2)/3);
		}
	}

	private void setAlpha(Graphics2D g, float alpha) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	}

	public class MouseController extends ControlAdapter {
		public void itemEntered(VisualItem item, MouseEvent e) {
			((Display)e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			item.setHighlighted(true);
		}

		// TODO: does not behave exactly as it should for vertices
		public void itemPressed(VisualItem item, MouseEvent e) {
			// first click selects, second click deselects
			item.setFixed(!item.isFixed());
		}

		public void itemExited(VisualItem item, MouseEvent e) {
			((Display)e.getSource()).setCursor(Cursor.getDefaultCursor());
			item.setHighlighted(false);
		}
	}
}