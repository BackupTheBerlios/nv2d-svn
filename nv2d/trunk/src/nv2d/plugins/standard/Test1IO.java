package nv2d.plugins.standard;

import java.awt.Container;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JPanel;

import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;
import nv2d.plugins.IOInterface;

import nv2d.graph.Graph;

import nv2d.graph.directed.DEdge;
import nv2d.graph.directed.DGraph;
import nv2d.graph.directed.DVertex;
import nv2d.ui.NController;

public class Test1IO implements IOInterface {
	String _desc;
	String _name;
	String _author;

	public Test1IO() {
		_desc = new String("This gives us a test Graph to work with.");
		_name = new String("Test1IO");
		_author= new String("Bo Shi");
	}

	/** Construct a new graph from the data. */
	public Graph getData(String [] args) throws IOException {
		return mkgraph();
	}

	/** 
	 * Requires a URL location to read a file.
	 * */
	public String [] requiredArgs() {
		String [] r = new String[0];
		return r;
	}

	public void initialize(Graph g, Container view, NController control) {
		// io-plugins can ignore this
	}

	public void heartbeat() {
		// io-plugins can ignore this
	}

	public void cleanup() {
		System.out.print("--> cleanup()\n");
	}

	public JPanel ui() {
		return null;
	}

	public JMenu menu() {
		return null;
	}

	public String require() {
		return "";
	}

	public String name() {
		return _name;
	}
	public String description() {
		return _desc;
	}
	public String author() {
		return _author;
	}
   
	// Note that the following routine is static and has no name, which
	// means it will only be run when the class is loaded
	static {
		// put factory in the hashtable for detector factories.
		NPluginLoader.reg("Test1IO", new Test1IO());
	}

	public static DGraph mkgraph() {
		DGraph graph = new DGraph();
		DVertex a = new DVertex("a");
		DVertex b = new DVertex("b");
		DVertex c = new DVertex("c");
		DVertex d = new DVertex("d");
		DVertex e = new DVertex("e");
		DVertex f = new DVertex("f");
		DVertex g = new DVertex("g");
		DVertex h = new DVertex("h");
		DVertex i = new DVertex("i");
		DVertex j = new DVertex("j");

		DEdge e0 = new DEdge(a, b, 4.0f);
		DEdge e1 = new DEdge(b, a, 74.0f);
		DEdge e2 = new DEdge(b, c, 18.0f);
		DEdge e3 = new DEdge(c, b, 12.0f);
		DEdge e4 = new DEdge(a, d, 85.0f);
		DEdge e5 = new DEdge(b, e, 12.0f);
		DEdge e6 = new DEdge(c, f, 74.0f);
		DEdge e7 = new DEdge(c, j, 12.0f);
		DEdge e8 = new DEdge(d, e, 32.0f);
		DEdge e9 = new DEdge(e, d, 66.0f);
		DEdge e10 = new DEdge(e, f, 76.0f);
		DEdge e11 = new DEdge(f, j, 21.0f);
		DEdge e12 = new DEdge(j, f, 8.0f);
		DEdge e13 = new DEdge(g, d, 12.0f);
		DEdge e14 = new DEdge(d, g, 38.0f);
		DEdge e15 = new DEdge(e, h, 33.0f);
		DEdge e16 = new DEdge(i, f, 31.0f);
		DEdge e17 = new DEdge(f, i, 11.0f);
		DEdge e18 = new DEdge(i, j, 78.0f);
		DEdge e19 = new DEdge(g, h, 10.0f);
		DEdge e20 = new DEdge(h, g, 2.0f);
		DEdge e21 = new DEdge(h, i, 72.0f);
		DEdge e22 = new DEdge(i, h, 18.0f);

		graph.add(a);
		graph.add(b);
		graph.add(c);
		graph.add(d);
		graph.add(e);
		graph.add(f);
		graph.add(g);
		graph.add(h);
		graph.add(i);
		graph.add(j);

		graph.add(e0);
		graph.add(e1);
		graph.add(e2);
		graph.add(e3);
		graph.add(e4);
		graph.add(e5);
		graph.add(e6);
		graph.add(e7);
		graph.add(e8);
		graph.add(e9);
		graph.add(e10);
		graph.add(e11);
		graph.add(e12);
		graph.add(e13);
		graph.add(e14);
		graph.add(e15);
		graph.add(e16);
		graph.add(e17);
		graph.add(e18);
		graph.add(e19);
		graph.add(e20);
		graph.add(e21);
		graph.add(e22);

		return graph;
	}
}
