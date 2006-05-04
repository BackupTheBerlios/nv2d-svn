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

package nv2d.plugins.standard;



import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import nv2d.plugins.IOInterface;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.GraphElement;
import nv2d.graph.Vertex;
import nv2d.graph.directed.DEdge;
import nv2d.graph.directed.DGraph;
import nv2d.graph.directed.DVertex;
import nv2d.graph.undirected.UEdge;
import nv2d.graph.undirected.UGraph;
import nv2d.graph.undirected.UVertex;
import nv2d.ui.NController;

public class GraphmlImporter implements IOInterface {
	public static final int TYPE_BOOLEAN = 0;
	public static final int TYPE_INT = 1;
	public static final int TYPE_LONG = 2;
	public static final int TYPE_FLOAT = 3;
	public static final int TYPE_DOUBLE = 4;
	public static final int TYPE_STRING = 5;
	
	private String _desc;
	private String _name;
	private String _author;

	private NController _control;

	private Document _document;

	/* GraphElement storage variables used to build the graph. */
	private boolean _directed;
	private int _vCount, _eCount;	// used to keep track of elements with no id's given
	private Hashtable _keyTable;
	private Hashtable _vertexSet, _edgeSet;

	public GraphmlImporter() {
		_desc = new String("Import a graph from a GraphML file.");
		_name = new String("GraphmlImporter");
		_author= new String("Bo Shi");
	}

	/** Construct a new graph from the data. */
	public Graph getData(String [] args) throws IOException {
		if(args.length != 1) {
			System.err.println(name() + ": wrong number of arguments");
			return null;
		};
		if(buildDomDocument(args[0])) {
			return process(_document);
		} else {
			return null;
		}
	}

	/** 
	 * Requires a URL location to read a file.
	 * */
	public String [] requiredArgs() {
		String [] r = new String[1];
		r[0] = new String("Location of GraphML file");
		return r;
	}

	public void initialize(Graph g, Container view, NController control) {
		// io-plugins can ignore this
		_control = control;
	}
	
	public void reloadAction(Graph g) {
		// nothing needs to be done
	}

	public void cleanup() {
		System.out.print("--> cleanup()\n");
	}

	public JMenu menu() {
		JMenu mod = new JMenu(name());
		JMenuItem open = new JMenuItem("Load GraphML File");
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

	private boolean buildDomDocument(String fileName) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//factory.setValidating(true);

		boolean success = false;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new Nv2dXmlErrorHandler());

			_document = builder.parse(new File(fileName));
			success = true;
		} catch (SAXParseException spe) {
			// Error generated by the parser
			System.err.println("\n** Parsing error"
					+ ", line " + spe.getLineNumber()
					+ ", uri " + spe.getSystemId());
			System.err.println("   " + spe.getMessage() );
			
			// Use the contained exception, if any
			Exception  x = spe;
			if (spe.getException() != null)
				x = spe.getException();
			x.printStackTrace();
			
		} catch (SAXException sxe) {
			// Error generated during parsing
			Exception  x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
			
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
			
		} catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();
		}
		return success;
	}

	private Graph process(Document doc) {
		_keyTable = new Hashtable();
		_vertexSet = new Hashtable();
		_edgeSet = new Hashtable();

		NodeList nodeList = doc.getChildNodes();
		for(int j = 0; j < nodeList.getLength(); j++) {
			Node n = nodeList.item(j);
			System.out.println(n.getNodeName() + ", " + n.getLocalName());
			if(n.getNodeName().equals("graphml")) {
				return processGraphml(n);
			}
		}

		System.err.println("No Graph node found.");
		return null;
	}

	/**
	 * The GraphML specification allows for multiple <code>graph</code> nodes
	 * within each file.
	 *
	 * This method will grab all instances of graphs that it encounters and
	 * create a popup dialog which allows the user to select which one (s)he
	 * would like to load.
	 *
	 * TODO
	 */
	private Graph processGraphml(Node n) {
		// desc, key, data, graph
		int graphCounter = 0;
		_vCount = 0;
		_eCount = 0;

		// graphml keys are nfileio attribute names
		processKeys();

		NodeList graphList = n.getChildNodes();
		for(int k = 0; k < graphList.getLength(); k++) {
			Node graphNode = graphList.item(k);
			if(graphNode.getNodeName().equals("graph")) {
				processGraph(graphNode);
				break;
			}
		}
		
		Graph g;
		if(_directed) {
			g = new DGraph();
		} else {
			g = new UGraph();
		}

		Iterator i = _vertexSet.values().iterator();
		while(i.hasNext()) {
			g.add((GraphElement) i.next());
		}

		i = _edgeSet.values().iterator();
		while(i.hasNext()) {
			g.add((GraphElement) i.next());
		}
		
		return g;
	}

	private void processKeys() {
		NodeList keys = _document.getElementsByTagName("key");
		for(int j = 0; j < keys.getLength(); j++) {
			Node key = keys.item(j);
			// attributes
			// id
			// for
			// attr.name
			// attr.type
			// -> default
			NamedNodeMap attributes = key.getAttributes();
			String id = attributes.getNamedItem("id").getNodeValue();
			String name = attributes.getNamedItem("attr.name").getNodeValue();
			String type = attributes.getNamedItem("attr.type").getNodeValue();
			KeyItem keyItem = new KeyItem(type, name);
			System.out.println(key + " (putting '"+id+"' KeyItem("+type+","+name+") id="+id+") into hash");
			_keyTable.put(id,keyItem);
		}
	}
	
	private void processGraph(Node n) {
		String parentType = n.getParentNode().getNodeName();
		if(parentType.equals("edge") || parentType.equals("node")) {
			System.err.println("GraphmlImporter: nested graphs are not yet supported by NV2D");
			return;
		}

		NodeList children = n.getChildNodes();
		// process the keys first
		for(int j = 0; j < children.getLength(); j++) {
			Node childNode = children.item(j);
			if (childNode.getNodeName().equals("desc")) {
				// TODO
				System.out.println(childNode);
			} else if (childNode.getNodeName().equals("data")) {
			} else {
				// System.err.println("GraphmlImporter: NV2D does not yet support the ["
				//		+ childNode.getNodeName() + "] element under the ["
				//		+ n.getNodeName() + "] element");
			}
		}
		// process vertices
		for(int j = 0; j < children.getLength(); j++) {
			Node childNode = children.item(j);
			if(childNode.getNodeName().equals("node")) {
				processNode(childNode);
			}
		}
		// process edges
		for(int j = 0; j < children.getLength(); j++) {
			Node childNode = children.item(j);
			if (childNode.getNodeName().equals("edge")) {
				processEdge(childNode);
			}
		}
	}

	/**
	 * Take a "data" node and create a {@link nv2d.graph.Datum} from it.
	 */
	private Datum processData(Node n) {
		// attributes
		// key
		NamedNodeMap attributes = n.getAttributes();
		String id = attributes.getNamedItem("key").getNodeValue();
		String aName = ((KeyItem) _keyTable.get(id)).getAttributeName();
		Object val = getKeyItemValue(getText(n), ((KeyItem) _keyTable.get(id)).getType());
		
		System.out.println("   Datum("+aName+","+val+") ["+val.getClass()+"]");
		
		Datum datum = new Datum(aName, val);
		return datum;
	}

	private void processNode(Node n) {
		// attributes:
		// id
		NamedNodeMap attributes = n.getAttributes();
		Node idNode = attributes.getNamedItem("id");
		String id;
		if(idNode != null) {
			id = idNode.getNodeValue();
		} else {
			id = "__nv2d_id_v" + _vCount;
			_vCount++;
		}
		System.out.println("node id="+id);
		
		if(_vertexSet.containsKey(id)) {
			System.err.println("Duplicate vertex ID found: " + id + "; not adding.");
			return;
		}
		
		Vertex v;
		if(_directed) {
			v = new DVertex(id);
		} else {
			v = new UVertex(id);
		}

		// make datum for data node
		NodeList children = n.getChildNodes();
		for(int j = 0; j < children.getLength(); j++) {
			Node childNode = children.item(j);
			// TODO get datum from this
			if(childNode.getNodeName().equals("data")) {
				v.setDatum(processData(childNode));
			}
		}
		
		_vertexSet.put(id, v);
	}

	/**
	 * The GraphML standard does not contain a special length attribute for edges
	 * so this plugin will treat a number of attribute names as length attributes.
	 * In order to specify a length attribute, <code>attr.name</code> must be
	 * one of <code>(length, weight, distance)</code> and <code>attr.type</code>
	 * must be one of <code>(int, long, float, double)</code>
	 */
	private void processEdge(Node n) {
		// attributes:
		// id
		// source
		// target
		NamedNodeMap attributes = n.getAttributes();
		Node idNode = attributes.getNamedItem("id");
		String id;
		if(idNode != null) {
			id = idNode.getNodeValue();
		} else {
			id = "__nv2d_id_e" + _eCount;
			_eCount++;
		}
				
		String source = attributes.getNamedItem("source").getNodeValue();
		String target = attributes.getNamedItem("target").getNodeValue();
		System.out.println("edge id="+id+" source="+source+" target="+target);

		if(!(_vertexSet.containsKey(source) && _vertexSet.containsKey(target))) {
			System.out.println("Invalid edge endpoint found ("
					+ source + " or " + target + ".  Ignoring.");
			return;
		}
		
		Edge e;
		// set the edge length to 1.0, change it later if the file specifies
		if(_directed) {
			e = new DEdge((DVertex) _vertexSet.get(source), (DVertex) _vertexSet.get(target), 1.0);
		} else {
			e = new UEdge((UVertex) _vertexSet.get(source), (UVertex) _vertexSet.get(target), 1.0);
		}

		// make datum for data node
		NodeList children = n.getChildNodes();
		for(int j = 0; j < children.getLength(); j++) {
			Node childNode = children.item(j);
			// TODO get datum from this
			if(childNode.getNodeName().equals("data")) {
				Datum d = processData(childNode);

				// is this an edge length parameter? (weight, length, distance)
				if((d.get() instanceof Integer || d.get() instanceof Double
						|| d.get() instanceof Float)
						&& (d.name().equals("weight") || d.name().equals("length")
						|| d.name().equals("distance"))) {
					try {
						e.setLength(Double.parseDouble(d.get().toString()));
					} catch (NumberFormatException exception) {
						e.setDatum(d);
					}
				} else {
					e.setDatum(d);
				}
			}
		}
		
		_edgeSet.put(id, e);
	}
	
	private static String getText(Node n) {
		NodeList list = n.getChildNodes();
		for(int j = 0; j < list .getLength(); j++) {
			if(list.item(j).getNodeName().equals("#text")) {
				return list.item(j).getNodeValue();
			}
		}
		return "";
	}

	public static Object getKeyItemValue(String val, int type) {
		switch(type) {
			case TYPE_BOOLEAN: return Boolean.valueOf(val);
			case TYPE_INT: return Integer.valueOf(val);
			case TYPE_LONG: return Long.valueOf(val);
			case TYPE_FLOAT: return Float.valueOf(val);
			case TYPE_DOUBLE: return Double.valueOf(val);
			default: return val;
		}
	}

	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// open up a dialog asking for one argument
			String s = javax.swing.JOptionPane.showInputDialog(
					null,
					"Please provide the location of the GraphML data file ");

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

class Nv2dXmlErrorHandler
		implements org.xml.sax.ErrorHandler {
	public void fatalError(SAXParseException exception)
			throws SAXException {
	}
	
	// treat validation errors as fatal
	public void error(SAXParseException e)
	throws SAXParseException {
		throw e;
	}
	
	// dump warnings too
	public void warning(SAXParseException err)
	throws SAXParseException {
		System.err.println("** Warning"
				+ ", line " + err.getLineNumber()
				+ ", uri " + err.getSystemId());
		System.err.println("   " + err.getMessage());
	}
}

class KeyItem {

	private String _attr;
	private int _type;

	public KeyItem(String type, String attribute) {
		_type = getType(type);
		_attr = attribute;
	}

	public static int getType(String t) {
		if(t.equals("boolean")) {
			return GraphmlImporter.TYPE_BOOLEAN;
		} else if (t.equals("int")) {
			return GraphmlImporter.TYPE_INT;
		} else if (t.equals("long")) {
			return GraphmlImporter.TYPE_LONG;
		} else if (t.equals("float")) {
			return GraphmlImporter.TYPE_FLOAT;
		} else if (t.equals("double")) {
			return GraphmlImporter.TYPE_DOUBLE;
		} else if (t.equals("string")) {
			return GraphmlImporter.TYPE_STRING;
		}
		return -1;
	}

	public String getAttributeName() {
		return _attr;
	}

	public int getType() {
		return _type;
	}
}