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

import java.util.*;
import java.awt.*;
import javax.swing.*;

public class NGUI implements ViewInterface {
	private NController _ctl;
	private NMenu _menu;
	private RootPaneContainer _window;
	
	private Vector _bottomSet, _centerSet, _sideSet;
	private Hashtable _containerNames;

	private Container _side;
	private Container _center;
	private Container _bottom;
	
	private Container _gui;
	
	boolean _sideVis, _bottomVis;
	
	/** These ratios keep track of the sizing in the split-panes. */
	double _verticalResizeWeight, _horizontalResizeWeight;

	private JComponent _outTextBox, _errTextBox;
	private NPrintStream _err, _out;

	public NGUI(NController ctl, RootPaneContainer window) {
		/* The following font bit is taken from
		 * http://forum.java.sun.com/thread.jsp?thread=125315&forum=57&message=330309
		 * Thanks to 'urmasoft' for the post
		 */
		Hashtable oUIDefault = UIManager.getDefaults();
		Enumeration oKey = oUIDefault.keys();
		String oStringKey = null;
		
		while (oKey.hasMoreElements()) {
			oStringKey = oKey.nextElement().toString();
			if (oStringKey.endsWith("font") || oStringKey.endsWith("acceleratorFont")) {
				UIManager.put(oStringKey, new Font("Dialog", Font.PLAIN, 10));
			}
		}
		
		_verticalResizeWeight = 1.0;
		_horizontalResizeWeight = 0.75;
		
		_containerNames = new Hashtable(9);
		
		_bottomSet = new Vector(5);
		_centerSet = new Vector(5);
		_sideSet = new Vector(5);
		
		_sideVis = false;
		_bottomVis = true;
		
		_ctl = ctl;
		_window = window;
		
		_menu = new NMenu(_ctl, _ctl.getRenderBox());

		initComponents();
	}
	
	public void toggleSidePane(boolean b) {
		if(b != _sideVis) {
			_sideVis = b;
			update();
		}
	}
	
	public boolean sidePaneState() {
		return _sideVis;
	}
	
	public void toggleBottomPane(boolean b) {
		if(b != _bottomVis) {
			_bottomVis = b;
			update();
		}
	}
	
	public boolean bottomPaneState() {
		return _bottomVis;
	}

	public boolean contains(Container c) {
		return (_bottomSet.contains(c) || _centerSet.contains(c) || _sideSet.contains(c));
	}
	
	public void validate() {
		Set s = new HashSet(10);
		s.addAll(_bottomSet);
		s.addAll(_centerSet);
		s.addAll(_sideSet);
		Iterator j = s.iterator();
		while(j.hasNext()) {
			((Container) j.next()).validate();
		}
	}

	public Container gui() {
		return _gui;
	}

	public RootPaneContainer getRootPaneContainer() {
		return _window;
	}
	
	public NMenu getMenu() {
		return _menu;
	}
	
	public boolean addComponent(Container component, String name, int location) {
		boolean b = addComponentNoUpdate(component, name, location);
		update();
		return b;
	}
	
	public boolean addComponentNoUpdate(Container component, String name, int location) {
		boolean b;
		b = registerComponent(component, name, location);
		return b;
	}

	public boolean removeComponent(Container c) {
		boolean b = removeComponentNoUpdate(c);
		update();
		return b;
	}
	
	public boolean removeComponentNoUpdate(Container c) {
		return (_bottomSet.remove(c) || _centerSet.remove(c) || _sideSet.remove(c));
	}
	
	public void errorPopup(String title, String msg, String extra) {
		System.err.println(msg);
		JOptionPane.showMessageDialog(null,
			msg,
			title,
			JOptionPane.WARNING_MESSAGE);
	}
	
	public boolean setComponentVisible(Container c) {
		update();
		
		if(_bottomSet.contains(c)) {
			toggleBottomPane(true);
			if(_side instanceof JTabbedPane) {
				((JTabbedPane) _bottom).setSelectedIndex(((JTabbedPane) _bottom).indexOfComponent(c));
			}
			return true;
		} else if (_centerSet.contains(c)) {
			if(_center instanceof JTabbedPane) {
				((JTabbedPane) _center).setSelectedIndex(((JTabbedPane) _center).indexOfComponent(c));
			}
			return true;
		} else if (_sideSet.contains(c)) {
			toggleSidePane(true);
			if(_side instanceof JTabbedPane) {
				((JTabbedPane) _side).setSelectedIndex(((JTabbedPane) _side).indexOfComponent(c));
			}
			return true;
		}
		return false;
	}
	
	/** Rebuild the layout */
	private void update() {
		Iterator j;
		Container major, minor;

		Component bottomOld = null;
		Component sideOld = null;
		Component centerOld = null;

		/* find out which views are displayed and note them */
		if(_bottom instanceof CloseableTabbedPane) {
			bottomOld = ((CloseableTabbedPane) _bottom).getSelectedComponent();
		}
		if(_side instanceof CloseableTabbedPane) {
			sideOld = ((CloseableTabbedPane) _side).getSelectedComponent();
		}
		if(_center instanceof CloseableTabbedPane) {
			centerOld = ((CloseableTabbedPane) _center).getSelectedComponent();
		}
		
		// TODO: these two if blocks are still buggy & don't save sizing parameters correctly
		// save the split-pane resize weights (if there are any) for restoration
		/*
		if(_center != null && _center.getParent() instanceof JSplitPane) {
			JSplitPane jsp = (JSplitPane) _center.getParent();
			//getDividerSize() getLastDividerLocation() 
			//_horizontalResizeWeight = (double) jsp.getLastDividerLocation() / (double) jsp.getHeight();
			System.out.println("set horizontal ratio to " + jsp.getLastDividerLocation());
		}
		if(_side != null && _side.getParent() instanceof JSplitPane) {
			JSplitPane jsp = (JSplitPane) _side.getParent();
			//_verticalResizeWeight = (double) jsp.getLastDividerLocation() / (double) jsp.getWidth();
			System.out.println("set vertical ratio to " + jsp.getLastDividerLocation());
		}
		*/

		/* build the panes */
		_bottom = makePane(_bottomSet);
		_side = makePane(_sideSet);
		_center = makePane(_centerSet);

		/* restore whichever views were open before the update */
		if(_bottom instanceof CloseableTabbedPane) {
			CloseableTabbedPane tmpPane = (CloseableTabbedPane) _bottom;
			tmpPane.setTabPlacement(CloseableTabbedPane.BOTTOM);
			if(tmpPane.indexOfComponent(bottomOld) >= 0) {
				tmpPane.setSelectedIndex(tmpPane.indexOfComponent(bottomOld));
			}
		}

		if(_side instanceof CloseableTabbedPane && ((CloseableTabbedPane) _side).indexOfComponent(sideOld) >= 0) { 
			CloseableTabbedPane tmpPane = (CloseableTabbedPane) _side;
			tmpPane.setSelectedIndex(tmpPane.indexOfComponent(bottomOld));
		}

		if(_center instanceof CloseableTabbedPane && ((CloseableTabbedPane) _center).indexOfComponent(centerOld) >= 0) { 
			CloseableTabbedPane tmpPane = (CloseableTabbedPane) _center;
			tmpPane.setSelectedIndex(tmpPane.indexOfComponent(centerOld));
		}
		
		// Looks like
		// |----------|
		// |        | |
		// | center | |
		// |--------| |
		// |________|_|

		if(_bottomVis && _bottom != null) {
			minor = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					_center, _bottom);
			((JSplitPane) minor).setDividerSize(2);
			((JSplitPane) minor).setResizeWeight(_verticalResizeWeight);
		} else {
			minor = _center;
		}
		
		if(_sideVis && _side != null) {
			major = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
					minor, _side);
			((JSplitPane) major).setDividerSize(2);
			((JSplitPane) major).setResizeWeight(_horizontalResizeWeight);
		} else {
			major = minor;
		}
		
		if(_gui != null) {
			_gui = major;
		} else {
			_gui = new JPanel();
		}
		_window.setContentPane(_gui);
		validate();
		_window.getRootPane().validate();
	}

	/** If the window list contains only one window, there is no need for
	 * tabs.  Otherwise, a tabbed pane is built.
	 */
	private Container makePane(Vector windowList) {
		if(windowList.size() == 1) {
			return (Container) windowList.get(0);
		} else if (windowList.size() > 1) {
			CloseableTabbedPane tabs = new CloseableTabbedPane();
			for (Enumeration e = windowList.elements() ; e.hasMoreElements() ;) {
				Container c = (Container) e.nextElement();
				String title = (String) _containerNames.get(c);
				tabs.add(title, c);
				tabs.addCloseableTabbedPaneListener(new ViewCloseListener(this, (Component) c));
			}
			return tabs;
		}
		return null;
	}
	
	private boolean registerComponent(Container component, String name, int location) {
		Set s = new HashSet(15);
		s.addAll(_bottomSet);
		s.addAll(_centerSet);
		s.addAll(_sideSet);
		if(s.contains(component)) {
			return false;
		}

		if(location == ViewInterface.SIDE_PANEL) {
			_sideSet.add(component);
			_containerNames.put(component, name);
		} else if (location == ViewInterface.BOTTOM_PANEL) {
			_bottomSet.add(component);
			_containerNames.put(component, name);
		} else if (location == ViewInterface.MAIN_PANEL) {
			_centerSet.add(component);
			_containerNames.put(component, name);
		}
		return true;
	}

	private void initComponents() {
		registerComponent(_ctl.getRenderBox(), "Main", ViewInterface.MAIN_PANEL);

		update();
	}
	
	public class ViewCloseListener implements CloseableTabbedPaneListener {
		Component _c;
		ViewInterface _vInterface;
		
		public ViewCloseListener(ViewInterface vInterface, Component c) {
			_c = c;
			_vInterface = vInterface;
		}
		
		public boolean closeTab(int tabIndexToClose, Component componentToClose) {
			if(componentToClose.equals(_c)) {
				removeComponentNoUpdate((Container) _c);
			}
			return true;
		}
	}
}
