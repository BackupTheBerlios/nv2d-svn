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
