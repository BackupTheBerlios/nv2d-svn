package nv2d.ui;

import java.awt.*;
import javax.swing.*;

public interface ViewInterface {
	public static final int MAIN_PANEL = 0;
	public static final int SIDE_PANEL = 1;
	public static final int BOTTOM_PANEL = 2;
	/**
	 * Returns the parent container for the main panel.
	 * @return returns a content pane object which holds this controller.
	 */
	public Container getParent();
	
	/**
	 * Returns a Frame or Applet depending on the top level container.
	 * @param returns a {@link java.awt.Frame} or {@link java.awt.Applet} object
	 */
	public Container getWindow();
	
	/**
	 * Get the instance of the main menu.
	 * @return a {@link NMenu} object.
	 */
	public JMenuBar getMenu();

	/**
	 * Get the instance of the JComponent containing the top level center GUI component.
	 * @return a {@link javax.swing.JTabbedPane} instance.
	 */
	public Container getCenterPane();

	/**
	 * Add a visual component into the GUI.  An integer reference value is returned.
	 * To remove the component, 
	 * @param component a GUI component
	 * @param location this interface defines a few fields which can be used for this
	 *   parameter (i.e. MAIN_PANEL, etc)
	 * @return a unique ID for the component useful for the removal operation
	 * @see {@link #removeComponent(int)}
	 */
	public int addComponent(Container component, int location);

	public boolean removeComponent(Container component);

	public boolean removeComponent(int id);
	
	/**
	 * Get the instance of the JComponent containing the top level bottom GUI component.
	 * @return a {@link javax.swing.JPanel} instance */
	public Container getBottomPane();
	
	/**
	 * Show a warning dialog and log the error message.
	 * @param title the title of the dialog window
	 * @param msg error message to be logged and shown
	 * @param extra details about the error
	 */
	public void errorPopup(String title, String msg, String extra);
}
