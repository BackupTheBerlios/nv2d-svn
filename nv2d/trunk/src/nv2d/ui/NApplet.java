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
	public static final String PARAM_DATAFILE = "NFileIO";
	public static final String PARAM_DEGREEFILTER = "DegreeFilter";
	MainPanel panel;

	String _dataFile;

	public void init() {
		_dataFile = getParameter(PARAM_DATAFILE);

		panel = new MainPanel(this, getContentPane());
		getContentPane().add(panel.getCenterPane(), "Center");
		getContentPane().add(panel.getBottomPane(), "South");
		setJMenuBar(panel.getMenu());
		setVisible(true);
	}
	
	/* TODO: figure out a scheme for command line arguments which are passed in*/
	public void start() {
		if(_dataFile == null) {
			panel.initialize(null);
		} else {
			panel.initialize(new String[] {"NFileIO", _dataFile});
		}
	}
}
