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

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.JTextArea;

public class NPrintStream extends PrintStream {
	private Vector _log;
	private Set _notifyList;
	private PrintStream _parent;
	
	public NPrintStream(PrintStream p) {
		super(p);
		_parent = p;
		
		_log = new Vector(); // strings
		_notifyList = new HashSet(); // JComponents
	}
	
	public void addNotifyClient(JTextArea j) {
		_notifyList.add(j);
	}
	
	public void flushNotifyList() {
		_notifyList.clear();
	}
	
	public void notifyAllClients(boolean linefeed) {
		Iterator i = _notifyList.iterator();
		while(i.hasNext()) {
			((JTextArea) i.next()).append((String) _log.lastElement() + (linefeed ? "\n" : ""));
		}
	}
	
	// override print and println for Strings
	public void print(String s) {
		_parent.print(s);
		_log.add(s);
		notifyAllClients(false);
	}
	
	public void println(String s) {
		_parent.println(s);
		_log.add(s);
		notifyAllClients(true);
	}
}
