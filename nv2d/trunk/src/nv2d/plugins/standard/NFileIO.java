package nv2d.plugins.standard;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JPanel;

import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;
import nv2d.plugins.IOInterface;

import nv2d.graph.Graph;

public class NFileIO implements IOInterface {
	String _desc;
	String _name;
	String _author;

	public NFileIO() {
		_desc = new String("This IO plugins allows you to import graphs from NV2D data files.");
		_name = new String("NFileIO");
		_author= new String("Bo Shi");
	}

	/** Construct a new graph from the data. */
	public Graph getData(String [] args) throws IOException {
		return null;
	}

	/** 
	 * Requires a URL location to read a file.
	 * */
	public String [] requiredArgs() {
		String [] r = new String[1];
		r[0] = "Supply a valid URL (http://path/to/data or file:///path/to/data etc.)";
		return r;
	}

	public void initialize(Graph g/* Model, View */) {
		System.out.print("--> initialize()\n");
	}

	public void heartbeat() {
		System.out.print("--> heartbeat()\n");
	}

	public void cleanup() {
		System.out.print("--> cleanup()\n");
	}

	public JPanel ui() {
		return null;
	}

	public JMenu menu() {
		return null;
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
	static {
		// put factory in the hashtable for detector factories.
		NPluginLoader.reg("NFileIO", new NFileIO());
	}
}

// supports grabbing a file over http or local disk
class FileIO {
	String _fname = null;
	InputStream _in = null;
	Vector _text = null;
	HashMap _data = null;
	String [] _attributes = null;

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
			System.out.println("BAD URL: " + params);
		} catch (IOException e) {
			System.out.println("Could not process data as url source, trying to read from local disk...");
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
		} catch (IOException e) {
			throw (new IOException("Could not read file from url or local disk."));
		}

		read();
		_attributes = ((String) _text.get(0)).split(";");
	}

	private void read() throws IOException {
		StringBuffer buf = new StringBuffer();
		int c;
		_text = new Vector();

		// read in the text file into a vector
		while((c = _in.read()) != -1) {
			if((char) c == '\n') {
				String s = buf.toString().trim();
				if(s.length() > 0) {
					// change s (one line) to a vector
					Vector v = new Vector(Arrays.asList(s.split(";")));
					Iterator i = v.iterator();
					while(i.hasNext()) {
						String stmp = (String) i.next();
						if(stmp.trim().length() < 1) {
							// remove it if there was a substring like ';;'
							v.remove(stmp);
						} else {
							stmp = new String(stmp.trim());
							if(stmp.endsWith("\"") && stmp.startsWith("\"")) {
								stmp = new String(stmp.substring(1, stmp.length() - 2));
							} else {
								// error
								System.err.print("There was an error in your CSV file.  Aborting import.");
								throw new IOException("Line [***]: entries must begin and terminate with a '\"' character.");
							}
						}
					}
					_text.add(v);
				}
			} else {
				buf.append((char) c);
			}
		}
	}
}
