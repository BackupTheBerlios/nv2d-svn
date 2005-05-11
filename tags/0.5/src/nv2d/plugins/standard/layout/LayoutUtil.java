package nv2d.plugins.standard.layout;

import javax.swing.ImageIcon;
import java.net.URL;

public class LayoutUtil {
    
	/**
	 * Given an image path name, returns an ImageIcon, 
	 * or null if the path was invalid.
	 * 
	 * Path base is assumed to be LayoutPlugin Resource path. 
	 */
	static public ImageIcon createImageIcon(String path) {
	    URL imgURL = LayoutPlugin.class.getResource(path);		        
	    // imgURL = new java.net.URL("http://web.mit.edu/prentice/www/" + path); //LayoutForceCtlSidePanel.class.getResource(path);

	    if (imgURL != null) {
	        return new ImageIcon(imgURL);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
	
}