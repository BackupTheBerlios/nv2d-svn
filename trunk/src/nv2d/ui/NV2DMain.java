/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Bo Shi
 * $Id$
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

import javax.swing.*;

public class NV2DMain extends JApplet {
	public static final String PARAM_DATAFILE = "NFileIO";
	public static final String PARAM_SIDEVIS = "SidePanel";
	public static final String PARAM_BOTTOMVIS = "BottomPanel";
	public static final String PARAM_DEGREEFILTER = "DegreeFilter";
	
	private static MainPanel panel;
	
	public NV2DMain(String [] args) {
		JFrame frame = new JFrame("NV2D");
		panel = new MainPanel(frame, args);
		panel.initialize(null);
		frame.setJMenuBar(panel.getView().getMenu());
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void init() {
		String sideVis = getParameter(PARAM_SIDEVIS);
		String bottomVis = getParameter(PARAM_BOTTOMVIS);

		// build command line arguments
		java.util.Vector vargs = new java.util.Vector();
		if(sideVis != null) {
			vargs.add("-sidebar");
			vargs.add(sideVis);
		}
		if(bottomVis != null) {
			vargs.add("-bottombar");
			vargs.add(bottomVis);
		}
		
		String [] args = null;
		if(vargs.size() > 0) {
			args = (String []) vargs.toArray();
		}
		
		panel = new MainPanel(this, args);
		setContentPane(panel.getView().gui());
		setJMenuBar(panel.getView().getMenu());
		setVisible(true);
	}
	
	/* TODO: figure out a scheme for command line arguments which are passed in*/
	public void start() {
		String dataFile = getParameter(PARAM_DATAFILE);

		if(dataFile == null) {
			panel.initialize(null);
		} else {
			panel.initialize(new String[] {"NFileIO", dataFile});
		}
	}
	
	public static void main(String [] args) {
		new NV2DMain(args);
	}
}