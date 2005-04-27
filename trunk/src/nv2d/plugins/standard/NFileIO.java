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

package nv2d.plugins.standard;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import nv2d.graph.Datum;
import nv2d.graph.Graph;
import nv2d.graph.directed.DGraph;
import nv2d.graph.directed.DEdge;
import nv2d.graph.directed.DVertex;
import nv2d.plugins.IOInterface;
import nv2d.ui.NController;

public class NFileIO implements IOInterface {
	String _desc;
	String _name;
	String _author;
	Container _view;
	NController _control;

	private String _arg;	// argument for this module supplied from JMenu

	public NFileIO() {
		_desc = new String("This IO plugins allows you to import graphs from NV2D data files.");
		_name = new String("NFileIO");
		_author= new String("Bo Shi");
		_arg = null;
	}

	/** Construct a new graph from the data. */
	public Graph getData(String [] args) throws IOException {
		FileIO fio = new FileIO();
		Graph g;
		String loc;
		if(args.length == 0) {
			if(_arg == null) {
				System.err.println("Error, no argument provided.");
				return null;
			}
			loc = _arg;
		}
		else if(args.length == 1) {
			loc = args[0];
		} else {
			System.err.println("Error, wrong number of arguments");
			return null;
		}

		try {
			fio.setup(loc);
			fio.read();
		} catch (IOException e) {
			System.err.println(e.toString());
			return null;
		}

		System.out.println("[NFileIO] Getting graph");
		g = fio.buildGraph();
		return g;
	}

	/** 
	 * Requires a URL location to read a file.
	 * */
	public String [] requiredArgs() {
		String [] r = new String[1];
		r[0] = "Supply a valid URL (http://path/to/data or file:///path/to/data etc.)";
		return r;
	}

	/* Model, view, controller -> g, view, controller */
	public void initialize(Graph g, Container view, NController control) {
		// g is not used because this is an IOModule and it provides g
		_view = view;
		_control = control;
	}
	
	public void reloadAction(Graph g) {
		// nothing needs to be done
	}

	public void cleanup() {
	}

	public JMenu menu() {
		JMenu mod = new JMenu(name());
		JMenuItem open = new JMenuItem("Open");
		mod.add(open);
		open.addActionListener(new MenuListener());
		return mod;
	}

	public String require() {
		return "";
	}

	public String name() {
		return _name;
	}

	public String description() {
		return _desc;
	}
	public String author() {
		return _author;
	}
   
	// Note that the following routine is static and has no name, which
	// means it will only be run when the class is loaded
	//static {
		// put factory in the hashtable for detector factories.
		//NPluginLoader.reg("NFileIO", new NFileIO());
	//}

	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// open up a dialog asking for one argument
			String s = JOptionPane.showInputDialog(
					null,
					"Please provide the location on disk or URL of the data file");

			//If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
				String [] arglist = new String[2];
				arglist[0] = name();
				arglist[1] = s;
				_control.initialize(arglist);
			}
		}
	}
}

// supports grabbing a file over http or local disk
class FileIO {
	String _fname = null;
	InputStream _in = null;
	String [] _attributes = null;
	HashMap _data = null;

	public static final int VERTEX_ID = 0;
	public static final int VERTEX_OUTEDGE = 1;
	public static final int VERTEX_EDGELEN = 2;
	public static final int VERTEX_FULLID = 3;
	public static final int VERTEX_CUSTOM = 4;

	public void setup(String params) throws IOException {
		// we need to test for url or file

		URLConnection conn = null;
		DataInputStream data = null;
		URL url = null;
		File fd = null;

		_data = new HashMap();

		try {
			url = new URL(params);

			conn = url.openConnection();
			conn.connect();
			_in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
			System.out.println("Connection to " + params + " opened.");
			return;
		} catch (MalformedURLException e) {
			System.err.println("BAD URL: " + params);
		} catch (IOException e) {
			System.err.println("Could not process data as url source, trying to read from local disk...");
		}

		try {
			fd = new File(params);
			if(!fd.exists()) {
				// file does not exist
				throw (new IOException("Could not find the file " + params));
			} else if(!fd.canRead()) {
				throw (new IOException("Could not read from the file " + params + ".  If you are running in applet mode, your security restrictions may not allow you to read a file from your local hard disk."));
			}

			// looks okay to use a file on local disk. set the input stream.
			_in = new FileInputStream(fd);
			System.out.println("Opened file " + params + " for reading");
			return;
		} catch (IOException e) {
			throw (new IOException("Could not read file from url or local disk."));
		}
	}

	public void read() throws IOException {
		StringBuffer buf = new StringBuffer();
		int c;
		int line = 0;

		// read in the text file into a vector
		while((c = _in.read()) != -1) {
			if((char) c == '\n') {
				String s = buf.toString().trim();
				if(s.length() > 0) {
					// change s (one line) to a vector
					String [] v = s.split(";");

					for(int i = 0; i < v.length; i++) {
						try { 
							v[i] = cleanUp(v[i]);
						} catch (IOException e) {
							throw new IOException("Error on line " + line + ":" + e.toString());
						}
					}

					// don't process the first line (extension parameters)
					if(line != 0) {
						process(v);
					} else {
						// process the extension parameters (first line)
						_attributes = v;
						for(int i = 0; i < _attributes.length; i++) {
							if(_attributes[i] == null || _attributes[i].length() < 1) {
								_attributes[i] = new String("Unnamed Attribute " + i);
							}
						}
					}
					buf = new StringBuffer();
					line = line + 1;
				}
			} else {
				buf.append((char) c);
			}
		}

		System.out.println("Read " + line + " lines");
	}

	/** Create a directed graph. */
	public DGraph buildGraph() {
		// create all edges first, then filter out invalid edges then vertices
		// TODO: filter invalid vertices (those which are not connected to any other one)

		HashMap vertmap = new HashMap();
		HashMap edgemap = new HashMap();
		Iterator i;
		DGraph graph;
		int j;

		// collect all the edge and graph objects
		i = _data.values().iterator();
		while(i.hasNext()) {
			String [] data = (String []) i.next();
			String source = data[VERTEX_ID];
			String fullName = data[VERTEX_FULLID];
			String [] edges = data[VERTEX_OUTEDGE].split(",");
			String [] edgelen = data[VERTEX_EDGELEN].split(",");
			int [] lengths = new int[edges.length];

			if(!vertmap.containsKey(source)) {
				DVertex vtx = new DVertex(source);
				vertmap.put(source, vtx);
			}

			// parse edge lengths
			if(edgelen.length != edges.length) {
				System.err.println("Warning: the edge lengths provided for vertex " + source + " do not map correctly.  Setting all edge lengths to 1.");
				for(j = 0; j < lengths.length; j++) {
					lengths[j] = 1;
				}
			} else if (data[VERTEX_OUTEDGE].length() > 0) {
				// only parse if there are outedges for this vertex
				for(j = 0; j < lengths.length; j++) {
					try {
						lengths[j] = java.lang.Integer.parseInt(edgelen[j]);
					} catch (java.lang.NumberFormatException e) {
						System.err.println("Warning: could not parse length '" + edgelen[j] + "' for vertex " + source + ".  Setting value to 1.");
						lengths[j] = 1;
					}
				}
			}

			// create vertices and edges
			for(j = 0; j < edges.length; j++) {
				String dest = edges[j].trim();
				if(dest.length() < 1) {
					// no outgoing edges for this vertex
					continue;
				} else if (dest.equals(source)) {
					System.err.println("Warning: attempted to create an edge using two identical vertices (" + source + ").");
					continue;
				}
				// make sure the destination exists
				if(_data.containsKey(dest)) {
					// create the nodes
					if(!vertmap.containsKey(dest)) {
						vertmap.put(dest, new DVertex(dest));
					}

					try {
						DEdge e = new DEdge((DVertex) vertmap.get(source), (DVertex) vertmap.get(dest), (float) lengths[j]);
						edgemap.put(source + "->" + dest, e);
					} catch (IllegalArgumentException e) {
						System.out.println("source=" + source + "       dest=" + dest);
						System.err.println("Error: " + e.toString());
					}
				} else {
					// invalid edge, notify
					System.err.println("Warning: the vertex " + dest + " does not exist in the data file and will not be added.");
				}
			}
		}

		// assign fullName to each vertex
		i = _data.values().iterator();
		while(i.hasNext()) {
			String [] data = (String []) i.next();
			DVertex vtx = (DVertex) vertmap.get(data[VERTEX_ID]);
			vtx.setDisplayId(data[VERTEX_FULLID]);
		}

		// reuse i - construct the graph
		graph = new DGraph();
		i = vertmap.values().iterator();
		while(i.hasNext()) {
			graph.add((DVertex) i.next());
		}
		i = edgemap.values().iterator();
		while(i.hasNext()) {
			graph.add((DEdge) i.next());
		}

		// enter in extra attributes
		i = vertmap.values().iterator();
		while(i.hasNext()) {
			DVertex v = (DVertex) i.next();
			String [] data = (String []) _data.get(v.id());
			for(j = VERTEX_CUSTOM; j < data.length; j++) {
				if(j - VERTEX_CUSTOM < _attributes.length) {
					String aname = _attributes[j - VERTEX_CUSTOM];
					// URL support
					if(aname.equals("URL")) {
						URL url = null;
						Datum d = null;
						try {
							url = new URL(data[j]);
							d = new Datum(aname, url);
						} catch (java.net.MalformedURLException exception) {
							d = new Datum(aname, data[j]);
						}
						v.setDatum(d);
					} else {
						v.setDatum(new Datum(_attributes[j - VERTEX_CUSTOM], data[j]));
					}
				} else {
					System.err.println("Syntax error: too many attributes for vertex '" + v.id() + "'");
				}
			}
		}
		
		// this is a good time to run garbage collector
		System.gc();

		return graph;
	}

	/* Takes a string formated like .".*". 
	 * Requires at least Java 1.4 to compile (uses regular expressions) */
	private String cleanUp(String s) throws IOException {
		s = s.trim();

		if(s.matches("\"\\s*\"")) {
			// there is an empty entry (inside quotation marks is white space)
			return new String();
		} else if(!s.matches("\".+\"")) {
			throw new IOException("Syntax error near [" + s + "]");
		}
		s = new String(s.substring(1, s.length() - 1));

		return s.trim();
	}

	/* Takes a vector resulting from parsing one line */
	private void process(String [] v) throws IOException {
		if(v.length < 4) {
			throw new IOException("Too few parameters given");
		}

		if(v[VERTEX_FULLID].length() < 1) {
			System.err.println("Warning: no full ID for vertex " + v[VERTEX_ID] + " given.");
			v[VERTEX_FULLID] = v[VERTEX_ID];
		}
		_data.put(v[VERTEX_ID], v);
	}
}
