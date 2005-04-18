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
import javax.swing.*;

public interface ViewInterface {
	public static final int MAIN_PANEL = 0;
	public static final int SIDE_PANEL = 1;
	public static final int BOTTOM_PANEL = 2;

	public Container gui();
	
	/**
	 * Returns a Frame or Applet depending on the top level container.
	 * @param returns a {@link java.awt.Frame} or {@link java.awt.Applet} object
	 */
	public RootPaneContainer getRootPaneContainer();
	
	/**
	 * Get the instance of the main menu.
	 * @return a {@link NMenu} object.
	 */
	public NMenu getMenu();
	
	public void validate();
	
	public void toggleSidePane(boolean b);
	
	public void toggleBottomPane(boolean b);
	/**
	 * Add a visual component into the GUI.  This class uses a {@link Set} to keep
	 * track of all added components.
	 * @param c a GUI component
	 * @param location this interface defines a few fields which can be used for this
	 *   parameter (i.e. MAIN_PANEL, etc)
	 * @return a boolean representing whether the component was successfully added
	 * @see {@link #removeComponent(int)}
	 */
	public boolean addComponent(Container c, String name, int location);

	public boolean removeComponent(Container c);
	
	public boolean removeComponentNoUpdate(Container c);
	
	/**
	 * Show a warning dialog and log the error message.
	 * @param title the title of the dialog window
	 * @param msg error message to be logged and shown
	 * @param extra details about the error
	 */
	public void errorPopup(String title, String msg, String extra);
}
