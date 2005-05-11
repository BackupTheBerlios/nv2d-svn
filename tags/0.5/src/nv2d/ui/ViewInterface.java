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

import java.awt.*;
import javax.swing.*;

public interface ViewInterface {
	public static final int MAIN_PANEL = 0;
	public static final int SIDE_PANEL = 1;
	public static final int BOTTOM_PANEL = 2;

	/** Get the top level view.
	 * @return A container which can be embedded into frames or applets.
	 */
	public Container gui();
	
	/**
	 * Returns a Frame or Applet depending on the top level container.
	 * @return a {@link java.awt.Frame} or {@link java.applet.Applet} object
	 */
	public RootPaneContainer getRootPaneContainer();
	
	/**
	 * Get the instance of the main menu.
	 * @return a {@link NMenu} object.
	 */
	public NMenu getMenu();
	
	public void setPreferredSize(int x, int y);

	/**
	 * Validates all of the components included in this view.
	 * @see {@link java.awt.Container#validate}.
	 */
	public void validate();

	/**
	 * Show/Hide the side panel.
	 * @param b true toggles on the side panel.
	 */
	public void toggleSidePane(boolean b);

	/**
	 * The visibility of the side panel.
	 * @return true if the side panel is visible to the user.
	 */
	public boolean sidePaneState();

	/**
	 * Show/Hide the bottom panel.
	 * @param b true toggles on the bottom panel.
	 */
	public void toggleBottomPane(boolean b);

	/**
	 * The visibility of the bottom panel.
	 * @return true if the bottom panel is visible to the user.
	 */
	public boolean bottomPaneState();

	/**
	 * Test to see whether a component has already been added.
	 * @param c a component.
	 * @return true if the component has been registers using {@link #addComponent}
	 * or {@link #addComponentNoUpdate}.
	 */
	public boolean contains(Container c);
	
	/**
	 * Add a visual component into the GUI.  This class uses a {@link java.util.Vector} to keep
	 * track of all added components, however, the storage behavior is more like a
	 * {@link java.util.Set} where an object  can only be added once.
	 * @param c a GUI component
	 * @param location this interface defines a few fields which can be used for this
	 *   parameter (i.e. MAIN_PANEL, etc)
	 * @return a boolean representing whether the component was successfully added
	 * @see {@link nv2d.ui.ViewInterface#removeComponent}
	 */
	public boolean addComponent(Container c, String name, int location);
	
	public boolean addComponentNoUpdate(Container c, String name, int location);

	public boolean removeComponent(Container c);
	
	public boolean removeComponentNoUpdate(Container c);
	
	/**
	 * If a component is not visible to the user, this opens the side/bottom panel
	 * and selects the appropriate tab to do so.
	 * @param c the component to set visible
	 * @return returns true if the component was successfully made visible.  A false
	 * return value is usually due to the component not being registered with the
	 * <code>ViewInterface</code>
	 */
	public boolean setComponentVisible(Container c);
	
	/**
	 * Show a warning dialog and log the error message.
	 * @param title the title of the dialog window
	 * @param msg error message to be logged and shown
	 * @param extra details about the error
	 */
	public void errorPopup(String title, String msg, String extra);
}
