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

import nv2d.graph.*;
import nv2d.render.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.util.Set;

public class HistoryElement {
	public static final int ICON_WIDTH = 70;
	public static final int ICON_HEIGHT = 50;
	Graph _model;
	Graph _subGraph;
	Icon _icon;
	String _desc;
	NController _ctl;

	public HistoryElement(NController ctl) {
		_ctl = ctl;
		RenderBox display = (RenderBox) ctl.getView();
		BufferedImage bi = new BufferedImage(
				(int) display.getWidth(),
				(int) display.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();

		// draw what we have
		display.update(g);

		AffineTransform tx = new AffineTransform();
		tx.scale((double ) ICON_WIDTH / display.getWidth(),
				(double) ICON_HEIGHT / display.getHeight());
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		bi = op.filter(bi, null);

		// bi can now be used as an icon
		_icon = new ImageIcon(bi);
		_desc = "V(" + ctl.getSubgraph().getVertices().size() + "), E(" + ctl.getSubgraph().getEdges().size() + ")";
		_model = ctl.getModel();
		_subGraph = ctl.getSubgraph();		
	}
	
	public Icon getIcon() {
		return _icon;
	}
	
	public String getDesc() {
		return _desc;
	}
	
	public Graph getModel() {
		return _model;
	}
	
	public Graph getSubgraph() {
		return _subGraph;
	}
}
