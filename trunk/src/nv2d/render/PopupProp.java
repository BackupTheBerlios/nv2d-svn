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

package nv2d.render;

import java.util.Arrays;
import java.net.*;
import javax.swing.*;
import javax.swing.SpringLayout;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;


import nv2d.graph.GraphElement;
import nv2d.graph.Datum;
import nv2d.ui.NController;

public class PopupProp extends JPanel {
	private NController _ctl;
	public PopupProp(NController ctl, GraphElement ge) {
		_ctl = ctl;
		
		setLayout(new SpringLayout());
		setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
		//setBorder(new javax.swing.border.TitledBorder(ge.id() + " [" + ge.getClass() + "]"));
		
		Object [] datums = ge.getDatumSet().toArray();
		Arrays.sort(datums);
		
		if(datums.length < 1) {
			return;
		}
		
		for(int j = 0; j < datums.length; j++) {
			Datum d = (Datum) datums[j];
			JLabel col1, col2;
			col1 = new JLabel(d.name() + ": ");
			
			if(d.get() instanceof URL) {
				col2 = createURLButton((URL) d.get());
			}
			else if(d.get().toString().length() > 32) {
				col2 = new JLabel(d.get().toString().substring(0,31) + "...");
				col2.setToolTipText(d.get().toString());
			} else {
				col2 = new JLabel(d.get().toString());
			}
			
			add(col1);
			add(col2);
		}
		
		makeCompactGrid(this,
				datums.length, 2,
				1, 0,
				5, 0);
	}
	
	private JLabel createURLButton(final URL url) {
		String str = url.toString();
		JLabel l = new JLabel(str.length() > 32 ? str.substring(0, 31) + "..." : str);
		l.setToolTipText("Open location [" + str + "]");
		l.setForeground(Color.BLUE);
		if(_ctl.getView().getRootPaneContainer() instanceof Applet) {
			l.addMouseListener(new MouseListener() {
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {
					_openURLActionHandler(url);
				}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
		}
		return l;
	}
	
	private void _openURLActionHandler(URL url) {
		AppletContext act = ((Applet) _ctl.getView().getRootPaneContainer()).getAppletContext();
		act.showDocument(url, "nv2dnetshow");
	}
	
	/**
	 * Taken from http://java.sun.com/docs/books/tutorial/uiswing/layout/example-1dot4/SpringUtilities.java
	 * Aligns the first <code>rows</code> * <code>cols</code>
	 * components of <code>parent</code> in
	 * a grid. Each component in a column is as wide as the maximum
	 * preferred width of the components in that column;
	 * height is similarly determined for each row.
	 * The parent is made just big enough to fit them all.
	 *
	 * @param rows number of rows
	 * @param cols number of columns
	 * @param initialX x location to start the grid at
	 * @param initialY y location to start the grid at
	 * @param xPad x padding between cells
	 * @param yPad y padding between cells
	 */
	public static void makeCompactGrid(Container parent,
			int rows, int cols,
			int initialX, int initialY,
			int xPad, int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout)parent.getLayout();
		} catch (ClassCastException exc) {
			System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}
		
		//Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width,
						getConstraintsForCell(r, c, parent, cols).
						getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints =
						getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}
		
		//Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height,
						getConstraintsForCell(r, c, parent, cols).
						getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints =
						getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}
		
		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}
	
	/* Used by makeCompactGrid. */
	private static SpringLayout.Constraints getConstraintsForCell(
			int row, int col,
			Container parent,
			int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}
}
