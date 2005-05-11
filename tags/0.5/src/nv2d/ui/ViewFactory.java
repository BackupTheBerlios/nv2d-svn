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

/**
 * Provides a place for the standard GUI views such as History Panes, etc.
 * @author bshi
 */
public class ViewFactory {
	private NPrintStream _err, _out;
	private JScrollPane _consolePane, _errorPane;
	
	private NController _ctl;
	
	private Container _historyPane;
	private Container _layoutPane;
	private Container _helpPane;
	
	/** Creates a new instance of ViewFactory */
	public ViewFactory(NController ctl) {
		_ctl = ctl;
		
		initComponents();
	}
	
	/**
	 * The object returned is guaranteed to be the same object every time this
	 * method is called. */
	public JScrollPane getConsolePane() {
		return _consolePane;
	}

	/**
	 * The object returned is guaranteed to be the same object every time this
	 * method is called. */
	public JScrollPane getErrorPane() {
		return _errorPane;
	}
	
	public Container getHistoryPane() {
		return _historyPane;
	}
	
	public Container getHelpPane() {
		return _helpPane;
	}
	
	public Container getLayoutPane() {
		return _layoutPane;
	}

	private void initComponents() {
		// trap output to standard streams and display them in a text box
		JTextArea errTxt = new JTextArea();
		JTextArea outTxt = new JTextArea();
		_errorPane = new JScrollPane(errTxt);
		_consolePane = new JScrollPane(outTxt);
		_err = new NPrintStream(System.err);
		_out = new NPrintStream(System.out);
		System.setOut(_out);
		System.setErr(_err);
		_err.addNotifyClient(errTxt);
		_out.addNotifyClient(outTxt);
		
		_layoutPane = new BottomPanel(_ctl);
		_historyPane = new HistoryUI(_ctl.getHistory());

		JEditorPane editorPane = null;
		try {
			String url = "http://web.mit.edu/bshi/Public/nv2d/";
			editorPane = new JEditorPane(url);
			editorPane.setEditable(false);
		} catch (java.io.IOException e) {
		}
		
		if(null != editorPane) {
			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane.setPreferredSize(new Dimension(600,400));
			_helpPane = scrollPane;
		} else {
			_helpPane = null;
		}
	}
}
