package nv2d.ui;

import javax.swing.*;

import nv2d.graph.directed.DGraph;
import nv2d.testsuit.graph.DirectedGraphTest;

public class NV2DMain {
	private static void gui() {
		/*
		DGraph g = DirectedGraphTest.testDGraphOne();
		Palette p = new Palette();
		PFrame f = new PFrame(false, p);
		f.setJMenuBar(new NMenu());
		p.initialize(g);
		*/
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui();
			}
		});
	}
}
