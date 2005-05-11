package nv2d.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.EventListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import javax.swing.event.EventListenerList;

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

/**
 * A JTabbedPane which has a close ('X') icon on each tab.
 *
 * http://forum.java.sun.com/thread.jspa?threadID=337070&messageID=3348119
 *
 * To add a tab, use the method addTab(String, Component)
 *
 * To have an extra icon on each tab (e.g. like in JBuilder, showing the file
 * type) use the method addTab(String, Component, Icon). Only clicking the 'X'
 * closes the tab.
 */
public class CloseableTabbedPane extends JTabbedPane
		implements MouseListener, MouseMotionListener {
	
	/**
	 * The <code>EventListenerList</code>.
	 */
	private EventListenerList listenerList = null;
	
	/**
	 * The viewport of the scrolled tabs.
	 */
	private JViewport headerViewport = null;
	
	/**
	 * Creates a new instance of <code>CloseableTabbedPane</code>
	 */
	public CloseableTabbedPane() {
		super();
		init(SwingUtilities.LEFT);
	}
	
	/**
	 * Creates a new instance of <code>CloseableTabbedPane</code>
	 * @param horizontalTextPosition the horizontal position of the text (e.g.
	 * SwingUtilities.TRAILING or SwingUtilities.LEFT)
	 */
	public CloseableTabbedPane(int horizontalTextPosition) {
		super();
		init(horizontalTextPosition);
	}
	
	/**
	 * Initializes the <code>CloseableTabbedPane</code>
	 * @param horizontalTextPosition the horizontal position of the text (e.g.
	 * SwingUtilities.TRAILING or SwingUtilities.LEFT)
	 */
	private void init(int horizontalTextPosition) {
		listenerList = new EventListenerList();
		addMouseListener(this);
		addMouseMotionListener(this);
		
		if (getUI() instanceof MetalTabbedPaneUI)
			setUI(new CloseableMetalTabbedPaneUI(horizontalTextPosition));
		else
			setUI(new CloseableTabbedPaneUI(horizontalTextPosition));
	}
	
	/**
	 * Adds a <code>Component</code> represented by a title and no icon.
	 * @param title the title to be displayed in this tab
	 * @param component the component to be displayed when this tab is clicked
	 */
	public void addTab(String title, Component component) {
		addTab(title, component, null);
	}
	
	/**
	 * Adds a <code>Component</code> represented by a title and an icon.
	 * @param title the title to be displayed in this tab
	 * @param component the component to be displayed when this tab is clicked
	 * @param extraIcon the icon to be displayed in this tab
	 */
	public void addTab(String title, Component component, Icon extraIcon) {
		boolean doPaintCloseIcon = true;
		try {
			Object prop = null;
			if ((prop = ((JComponent) component).
					getClientProperty("isClosable")) != null) {
				doPaintCloseIcon = ((Boolean) prop).booleanValue();
			}
		} catch (Exception ignored) {/*Could probably be a ClassCastException*/}
		
		component.addPropertyChangeListener("isClosable",
				new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				Object newVal = e.getNewValue();
				int index = -1;
				if (e.getSource() instanceof Component) {
					index = indexOfComponent((Component) e.getSource());
				}
				if (index != -1 && newVal != null && newVal instanceof Boolean) {
					setCloseIconVisibleAt(index, ((Boolean) newVal).booleanValue());
				}
			}
		});
		
		super.addTab(title,
				doPaintCloseIcon ? new CloseIcon() : null,
				component);
		
		if (headerViewport == null) {
			Component [] components = getComponents();
			for (int j = 0; j < components.length; j++) {
				Component c = components[j];
				if ("TabbedPane.scrollableViewport".equals(c.getName()))
					headerViewport = (JViewport) c;
			}
		}
	}
	
	/**
	 * Sets the closeicon at <code>index</code>.
	 * @param index the tab index where the icon should be set
	 * @param icon the icon to be displayed in the tab
	 * @throws IndexOutOfBoundsException if index is out of range (index < 0 ||
	 * index >= tab count)
	 */
	private void setCloseIconVisibleAt(int index, boolean iconVisible)
			throws IndexOutOfBoundsException {
		super.setIconAt(index, iconVisible ? new CloseIcon() : null);
	}
	
	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on
	 * a component.
	 * @param e the <code>MouseEvent</code>
	 */
	public void mouseClicked(MouseEvent e) {
		processMouseEvents(e);
	}
	
	/**
	 * Invoked when the mouse enters a component.
	 * @param e the <code>MouseEvent</code>
	 */
	public void mouseEntered(MouseEvent e) { }
	
	/**
	 * Invoked when the mouse exits a component.
	 * @param e the <code>MouseEvent</code>
	 */
	public void mouseExited(MouseEvent e) {
		for (int i=0; i<getTabCount(); i++) {
			CloseIcon icon = (CloseIcon) getIconAt(i);
			if (icon != null)
				icon.mouseover = false;
		}
		repaint();
	}
	
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * @param e the <code>MouseEvent</code>
	 */
	public void mousePressed(MouseEvent e) {
		processMouseEvents(e);
	}
	
	/**
	 * Invoked when a mouse button has been released on a component.
	 * @param e the <code>MouseEvent</code>
	 */
	public void mouseReleased(MouseEvent e) { }
	
	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * <code>MOUSE_DRAGGED</code> events will continue to be delivered to the
	 * component where the drag originated until the mouse button is released
	 * (regardless of whether the mouse position is within the bounds of the
	 * component).<br/>
	 * <br/>
	 * Due to platform-dependent Drag&Drop implementations,
	 * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
	 * Drag&Drop operation.
	 * @param e the <code>MouseEvent</code>
	 */
	public void mouseDragged(MouseEvent e) {
		processMouseEvents(e);
	}
	
	/**
	 * Invoked when the mouse cursor has been moved onto a component but no
	 * buttons have been pushed.
	 * @param e the <code>MouseEvent</code>
	 */
	public void mouseMoved(MouseEvent e) {
		processMouseEvents(e);
	}
	
	/**
	 * Processes all caught <code>MouseEvent</code>s.
	 * @param e the <code>MouseEvent</code>
	 */
	private void processMouseEvents(MouseEvent e) {
		int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
		if (tabNumber < 0) return;
		boolean otherWasOver = false;
		for (int i=0; i<getTabCount(); i++) {
			if (i != tabNumber) {
				CloseIcon ic = (CloseIcon) getIconAt(i);
				if (ic != null) {
					if (ic.mouseover)
						otherWasOver = true;
					ic.mouseover = false;
				}
			}
		}
		if (otherWasOver)
			repaint();
		CloseIcon icon = (CloseIcon) getIconAt(tabNumber);
		if (icon != null) {
			Rectangle rect = icon.getBounds();
			boolean vpIsNull = headerViewport == null;
			Point pos = vpIsNull ? new Point() : headerViewport.getViewPosition();
			int vpDiffX = (vpIsNull ? 0 : headerViewport.getX());
			int vpDiffY = (vpIsNull ? 0 : headerViewport.getY());
			Rectangle drawRect = new Rectangle(rect.x - pos.x + vpDiffX,
					rect.y - pos.y + vpDiffY, rect.width, rect.height);
			
			if (e.getID() == e.MOUSE_PRESSED) {
				icon.mousepressed = e.getModifiers() == e.BUTTON1_MASK;
				repaint(drawRect);
			} else if (e.getID() == e.MOUSE_MOVED || e.getID() == e.MOUSE_DRAGGED ||
					e.getID() == e.MOUSE_CLICKED) {
				pos.x += e.getX() - vpDiffX;
				pos.y += e.getY() - vpDiffY;
				if (rect.contains(pos)) {
					if (e.getID() == e.MOUSE_CLICKED) {
						int selIndex = getSelectedIndex();
						if (fireCloseTab(selIndex)) {
							if (selIndex > 0) {
								// to prevent uncatchable null-pointers
								Rectangle rec = getUI().getTabBounds(this, selIndex - 1);
								
								MouseEvent event = new MouseEvent((Component) e.getSource(),
										e.getID() + 1,
										System.currentTimeMillis(),
										e.getModifiers(),
										rec.x,
										rec.y,
										e.getClickCount(),
										e.isPopupTrigger(),
										e.getButton());
								dispatchEvent(event);
							}
							//the tab is being closed
							remove(selIndex);
						} else {
							icon.mouseover = false;
							icon.mousepressed = false;
							repaint(drawRect);
						}
					} else {
						icon.mouseover = true;
						icon.mousepressed = e.getModifiers() == e.BUTTON1_MASK;
					}
				} else {
					icon.mouseover = false;
				}
				repaint(drawRect);
			}
		}
	}
	
	/**
	 * Adds an <code>CloseableTabbedPaneListener</code> to the tabbedpane.
	 * @param l the <code>CloseableTabbedPaneListener</code> to be added
	 */
	public void addCloseableTabbedPaneListener(CloseableTabbedPaneListener l) {
		listenerList.add(CloseableTabbedPaneListener.class, (EventListener) l);
	}
	
	/**
	 * Removes an <code>CloseableTabbedPaneListener</code> from the tabbedpane.
	 * @param l the listener to be removed
	 */
	public void removeCloseableTabbedPaneListener(CloseableTabbedPaneListener l) {
		listenerList.remove(CloseableTabbedPaneListener.class, (EventListener) l);
	}
	
	/**
	 * Returns an array of all the <code>SearchListener</code>s added to this
	 * <code>SearchPane</code> with addSearchListener().
	 * @return all of the <code>SearchListener</code>s added or an empty array if
	 * no listeners have been added
	 */
	public CloseableTabbedPaneListener[] getCloseableTabbedPaneListener() {
		return (CloseableTabbedPaneListener []) listenerList.getListeners(CloseableTabbedPaneListener.class);
	}
	
	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 * @param tabIndexToClose the index of the tab which should be closed
	 * @return true if the tab can be closed, false otherwise
	 */
	protected boolean fireCloseTab(int tabIndexToClose) {
		boolean closeit = true;
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		for (int j = 0; j < listeners.length; j++) {
			Object i = listeners[j];
			if (i instanceof CloseableTabbedPaneListener) {
				if (!((CloseableTabbedPaneListener) i).closeTab(tabIndexToClose, getComponentAt(tabIndexToClose))) {
					closeit = false;
					break;
				}
			}
		}
		return closeit;
	}

	/**
	 * A specific <code>BasicTabbedPaneUI</code>.
	 */
	class CloseableTabbedPaneUI extends BasicTabbedPaneUI {
		
		/**
		 * the horizontal position of the text
		 */
		private int horizontalTextPosition = SwingUtilities.LEFT;
		
		/**
		 * Creates a new instance of <code>CloseableTabbedPaneUI</code>
		 */
		public CloseableTabbedPaneUI() {
		}
		
		/**
		 * Creates a new instance of <code>CloseableTabbedPaneUI</code>
		 * @param horizontalTextPosition the horizontal position of the text (e.g.
		 * SwingUtilities.TRAILING or SwingUtilities.LEFT)
		 */
		public CloseableTabbedPaneUI(int horizontalTextPosition) {
			this.horizontalTextPosition = horizontalTextPosition;
		}
		
		/**
		 * Layouts the label
		 * @param tabPlacement the placement of the tabs
		 * @param metrics the font metrics
		 * @param tabIndex the index of the tab
		 * @param title the title of the tab
		 * @param icon the icon of the tab
		 * @param tabRect the tab boundaries
		 * @param iconRect the icon boundaries
		 * @param textRect the text boundaries
		 * @param isSelected true whether the tab is selected, false otherwise
		 */
		protected void layoutLabel(int tabPlacement, FontMetrics metrics,
				int tabIndex, String title, Icon icon,
				Rectangle tabRect, Rectangle iconRect,
				Rectangle textRect, boolean isSelected) {
			
			textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
			
			javax.swing.text.View v = getTextViewForTab(tabIndex);
			if (v != null) {
				tabPane.putClientProperty("html", v);
			}
			
			SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
					metrics, title, icon,
					SwingUtilities.CENTER,
					SwingUtilities.CENTER,
					SwingUtilities.CENTER,
					//SwingUtilities.TRAILING,
					horizontalTextPosition,
					tabRect,
					iconRect,
					textRect,
					textIconGap + 2);
			
			tabPane.putClientProperty("html", null);
			
			int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
			int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
			iconRect.x += xNudge;
			iconRect.y += yNudge;
			textRect.x += xNudge;
			textRect.y += yNudge;
		}
	}
	
	/**
	 * A specific <code>MetalTabbedPaneUI</code>.
	 */
	class CloseableMetalTabbedPaneUI extends MetalTabbedPaneUI {
		
		/**
		 * the horizontal position of the text
		 */
		private int horizontalTextPosition = SwingUtilities.LEFT;
		
		/**
		 * Creates a new instance of <code>CloseableMetalTabbedPaneUI</code>
		 */
		public CloseableMetalTabbedPaneUI() {
		}
		
		/**
		 * Creates a new instance of <code>CloseableMetalTabbedPaneUI</code>
		 * @param horizontalTextPosition the horizontal position of the text (e.g.
		 * SwingUtilities.TRAILING or SwingUtilities.LEFT)
		 */
		public CloseableMetalTabbedPaneUI(int horizontalTextPosition) {
			this.horizontalTextPosition = horizontalTextPosition;
		}
		
		/**
		 * Layouts the label
		 * @param tabPlacement the placement of the tabs
		 * @param metrics the font metrics
		 * @param tabIndex the index of the tab
		 * @param title the title of the tab
		 * @param icon the icon of the tab
		 * @param tabRect the tab boundaries
		 * @param iconRect the icon boundaries
		 * @param textRect the text boundaries
		 * @param isSelected true whether the tab is selected, false otherwise
		 */
		protected void layoutLabel(int tabPlacement, FontMetrics metrics,
				int tabIndex, String title, Icon icon,
				Rectangle tabRect, Rectangle iconRect,
				Rectangle textRect, boolean isSelected) {
			
			textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
			
			javax.swing.text.View v = getTextViewForTab(tabIndex);
			if (v != null) {
				tabPane.putClientProperty("html", v);
			}
			
			SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
					metrics, title, icon,
					SwingUtilities.CENTER,
					SwingUtilities.CENTER,
					SwingUtilities.CENTER,
					//SwingUtilities.TRAILING,
					horizontalTextPosition,
					tabRect,
					iconRect,
					textRect,
					textIconGap + 2);
			
			tabPane.putClientProperty("html", null);
			
			int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
			int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
			iconRect.x += xNudge;
			iconRect.y += yNudge;
			textRect.x += xNudge;
			textRect.y += yNudge;
		}
	}
}
 


/**
 * Deze klasse tekent een kruisje en houdt de absolute coordinaten
 * van de afbeelding vast.
 */
class CloseIcon implements Icon {
	private int SIZE = 6;

	private int x_pos;

	private int y_pos;

	private Rectangle iconRect;
	
	/**
	 * true whether the mouse is over this icon, false otherwise
	 */
	public boolean mouseover = false;
	
	/**
	 * true whether the mouse is pressed on this icon, false otherwise
	 */
	public boolean mousepressed = false;
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		this.x_pos = x;
		this.y_pos = y;
		drawCross(g, x, y);
		iconRect = new Rectangle(x, y, SIZE, SIZE);
	}
	
	private void drawCross(Graphics g, int xo, int yo) {
		if (mousepressed && mouseover) {
			g.setColor(Color.RED);
		} else if (mouseover) {
			g.setColor(Color.BLUE);
		}
		g.drawRect(xo-1, yo-1, SIZE+2, SIZE+2);
		g.drawLine(xo, yo, xo+SIZE, yo+SIZE);
		g.drawLine(xo, yo+SIZE, xo+SIZE, yo);
	}
	
	public boolean coordinatenInIcon(int x, int y) {
		boolean isInIcon = false;
		if (iconRect != null) {
			isInIcon = iconRect.contains(x,y);
		}
		return isInIcon;
	}

	public int getIconWidth() {
		return SIZE;
	}

	public int getIconHeight() {
		return SIZE;
	}
	
	/**
	 * Gets the bounds of this icon in the form of a <code>Rectangle<code>
	 * object. The bounds specify this icon's width, height, and location
	 * relative to its parent.
	 * @return a rectangle indicating this icon's bounds
	 */
	public Rectangle getBounds() {
		return new Rectangle(x_pos, y_pos, SIZE, SIZE);
	}
}
