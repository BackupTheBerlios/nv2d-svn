package nv2d.gui;

import javax.swing.*;

import edu.umd.cs.piccolox.PFrame;

import nv2d.graph.directed.DGraph;
import nv2d.render.Palette;
import nv2d.testsuit.graph.DirectedGraphTest;

public class NV2DMain {
	private static void gui() {
		DGraph g = DirectedGraphTest.testDGraphOne();
		Palette p = new Palette();
		PFrame f = new PFrame(false, p);
		f.setJMenuBar(new NMenu());
		p.initialize(g);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui();
			}
		});
	}
}
