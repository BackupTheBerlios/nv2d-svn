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

package nv2d.utils;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarFile;

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
		/*}  catch (IOException e) {
			System.err.println(e);
			return null;
			*/
		} catch (java.lang.NullPointerException e) {
			System.err.println("JarListing: Could not find a plugin listing.");
			return null;
		}
	}
}
