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
 *
 * Created on Mar 6, 2005
 */

package nv2d.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/** Create an colored Icon.  This class is meant to be used with the {@link nv2d.ui.ColorLegend}
 * class.
 * @author bshi
 */
public class ColorIcon implements Icon {
	private int _h, _w;
	private Color _c;
	
	public ColorIcon(Color c) {
		_c = c;
		_h = 17;
		_w = 30;
	}
	
	public ColorIcon(Color c, int w, int h) {
		_c = c;
		_h = h;
		_w = w;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(_c);
		g.fillRect(x, y, getIconWidth(), getIconHeight());
		g.setColor(Color.BLACK);
		g.drawRect(x, y, getIconWidth(), getIconHeight());
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return 30;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return 17;
	}

}
