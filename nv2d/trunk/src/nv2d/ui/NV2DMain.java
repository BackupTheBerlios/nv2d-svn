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
		MainPanel panel = new MainPanel(this, getContentPane());
		panel.initialize(null);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(panel.getCenterPane(), "Center");
		getContentPane().add(panel.getBottomPane(), "South");
		getContentPane().add(panel.getHistoryPane(), "East");
		setJMenuBar(panel.getMenu());
		setTitle("NV2D");
		pack();
		setVisible(true);
	}
	
	public static void main(String [] args) {
		new NV2DMain();
	}
}
