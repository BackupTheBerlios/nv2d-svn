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

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 *
 * @author bshi
 */
public class HistoryListRenderer extends DefaultListCellRenderer {
	public Component getListCellRendererComponent(JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean hasFocus) {
		final JLabel label =
				(JLabel)super.getListCellRendererComponent(list,
				value,
				index,
				isSelected,
				hasFocus);
		
		assert(value instanceof HistoryElement);
		
		final HistoryElement h = (HistoryElement) value;
		label.setIcon(h.getIcon());
		label.setText(h.getDesc());
		
		label.setVerticalTextPosition(SwingConstants.TOP);
		label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
		return(label);
	}
}
