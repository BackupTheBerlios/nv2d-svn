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
	
	private Set _bottomSet, _centerSet, _sideSet;
	private Hashtable _containerNames;

	private Container _historyCtlPanel;
	private Container _layoutCtlPanel;

	private Container _side;
	private Container _center;
	private Container _bottom;
	
	private Container _gui;
	
	boolean _sideVis, _bottomVis;

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
		
		_containerNames = new Hashtable(9);
		
		_bottomSet = new HashSet(3);
		_centerSet = new HashSet(3);
		_sideSet = new HashSet(3);
		
		_sideVis = false;
		_bottomVis = true;
		
		_ctl = ctl;
		_window = window;
		
		_menu = new NMenu(_ctl, _ctl.getRenderBox());
		_layoutCtlPanel = new BottomPanel(_ctl);
		_historyCtlPanel = new HistoryUI(_ctl.getHistory());

		initComponents();
	}
	
	public void toggleSidePane(boolean b) {
		_sideVis = b;
		update();
	}
	
	public void toggleBottomPane(boolean b) {
		_bottomVis = b;
		update();
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
		boolean b;
		b = registerComponent(component, name, location);
		update();
		return b;
	}

	public boolean removeComponent(Container c) {
		boolean b = (_bottomSet.remove(c) || _centerSet.remove(c) || _sideSet.remove(c));
		update();
		return b;
	}
	
	public void errorPopup(String title, String msg, String extra) {
		System.err.println(msg);
		JOptionPane.showMessageDialog(null,
			msg,
			title,
			JOptionPane.WARNING_MESSAGE);
	}
	
	/** Rebuild the layout */
	private void update() {
		Iterator j;
		Container major, minor;
		
		_bottom = makePane(_bottomSet);
		_side = makePane(_sideSet);
		_center = makePane(_centerSet);
		
		if(_bottom instanceof JTabbedPane) {
			((JTabbedPane) _bottom).setTabPlacement(JTabbedPane.BOTTOM);
		}
		
		// Looks like
		// |----------|
		// |        | |
		// | center | |
		// |--------| |
		// |________|_|

		if(_bottomVis) {
			minor = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					_center, _bottom);
			((JSplitPane) minor).setDividerSize(2);
			((JSplitPane) minor).setResizeWeight(1.0);
		} else {
			minor = _center;
		}
		
		if(_sideVis) {
			major = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
					minor, _side);
			((JSplitPane) major).setDividerSize(2);
			((JSplitPane) major).setResizeWeight(1.0);
		} else {
			major = minor;
		}
		
		_gui = major;
		_window.setContentPane(_gui);
		validate();
		_window.getRootPane().validate();
	}

	/** If the window list contains only one window, there is no need for
	 * tabs.  Otherwise, a tabbed pane is built.
	 */
	private Container makePane(Set windowList) {
		Iterator j;
		if(windowList.size() == 1) {
			j = windowList.iterator();
			return (Container) j.next();
		} else if (windowList.size() > 1) {
			Container tabs = new JTabbedPane();
			j = windowList.iterator();
			while(j.hasNext()) {
				Container c = (Container) j.next();
				String title = (String) _containerNames.get(c);
				tabs.add(title, c);
			}
			return tabs;
		}
		return null;
	}
	
	private boolean registerComponent(Container component, String name, int location) {
		boolean success = false;
		if(location == ViewInterface.SIDE_PANEL) {
			success = _sideSet.add(component);
			_containerNames.put(component, name);
		} else if (location == ViewInterface.BOTTOM_PANEL) {
			success = _bottomSet.add(component);
			_containerNames.put(component, name);
		} else if (location == ViewInterface.MAIN_PANEL) {
			success = _centerSet.add(component);
			_containerNames.put(component, name);
		}
		return success;
	}

	private void initComponents() {
		registerComponent(_ctl.getRenderBox(), "Main", ViewInterface.MAIN_PANEL);
		registerComponent(_layoutCtlPanel, "Layout", ViewInterface.BOTTOM_PANEL);
		registerComponent(_historyCtlPanel, "History", ViewInterface.SIDE_PANEL);

		// trap output to standard streams and display them in a text box
		JTextArea errTxt = new JTextArea();
		JTextArea outTxt = new JTextArea();
		JScrollPane sp1 = new JScrollPane(errTxt);
		JScrollPane sp2 = new JScrollPane(outTxt);
		_err = new NPrintStream(System.err);
		_out = new NPrintStream(System.out);
		_outTextBox = sp2;
		_errTextBox = sp1;
		System.setOut(_out);
		System.setErr(_err);
		_err.addNotifyClient(errTxt);
		_out.addNotifyClient(outTxt);
		
		registerComponent(sp2, "Output", ViewInterface.MAIN_PANEL);
		registerComponent(sp1, "Errors", ViewInterface.MAIN_PANEL);

		update();
	}
}