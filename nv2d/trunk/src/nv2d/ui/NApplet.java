package nv2d.ui;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;

import nv2d.graph.FilterInterface;
import nv2d.graph.Graph;
import nv2d.graph.filter.DefaultFilter;
import nv2d.render.RenderBox;
import nv2d.plugins.IOInterface;
import nv2d.plugins.NPluginManager;
import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;

public class NApplet extends JApplet {
	MainPanel panel;
	public void init() {
		panel = new MainPanel(getContentPane());
		getContentPane().add(panel.getCenterPane(), "Center");
		getContentPane().add(panel.getBottomPane(), "South");
		setJMenuBar(panel.getMenu());
		setVisible(true);
	}
	
	public void start() {
		panel.start();
	}
}
