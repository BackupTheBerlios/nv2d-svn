package nv2d.ui;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.awt.*;
import javax.swing.*;

import nv2d.graph.FilterInterface;
import nv2d.graph.Graph;
import nv2d.graph.filter.DefaultFilter;
import nv2d.render.RenderBox;
import nv2d.plugins.IOInterface;
import nv2d.plugins.NPluginManager;
import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;

public class NV2DMain extends JFrame {
	public NV2DMain() {
		MainPanel panel = new MainPanel();
		panel.start();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(panel);
		setJMenuBar(panel.getMenu());
		setTitle("NV2D");
		pack();
		setVisible(true);
	}

	public static void main(String [] args) {
		new NV2DMain();
	}
}
