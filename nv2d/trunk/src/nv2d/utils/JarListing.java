package nv2d.utils;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarListing {
	/* Testing */

	public static Enumeration getListing(URL url) {
		try {
			// Get the jar file
			JarURLConnection conn = (JarURLConnection) url.openConnection();
			JarFile jar = conn.getJarFile();

			return jar.entries();
		} catch (IOException e) {
			System.err.println(e);
			return null;
		}
	}

	public static Enumeration getListing(File f) {
		try {
			JarFile jar = new JarFile(f);

			return jar.entries();
		} catch (IOException e) {
			System.err.println(e);
			return null;
		}
	}

	/** Get a listing of the directories of plugins in the directory
	 * indicated by <code>filter</code>. */
	public static Enumeration getPluginListing(String url, String filter) {
		Vector v;
		try {
			v = new Vector();
			for (Enumeration e = getListing(new URL(url)) ; e.hasMoreElements() ;) {
				String s = e.nextElement().toString();
				if(s.startsWith(filter)) {
					v.add(s);
				}
			}
			return v.elements();
		} catch (MalformedURLException e) {
			System.err.println(e);
			return null;
		} catch (IOException e) {
			System.err.println(e);
			return null;
		}
	}

	/* // for testing only
	public static void main(String [] args) {
		for(Enumeration e = getPluginListing("jar:http://web.mit.edu/bshi/www/N2.jar!/", "nv2d/plugins/standard"); e.hasMoreElements();) {
			System.out.println(e.nextElement());
		}
	}
	*/
}
