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

package nv2d.plugins;

import java.io.IOException;

import nv2d.graph.Graph;

public interface IOInterface extends NV2DPlugin {
	/** Construct a new graph from the data. */
	public Graph getData(String [] args) throws IOException;

	/** Return an array of strings describing the arguments which this plugin
	 * needs to establish a connection and to get the data to build the graph.
	 * A user supplied list of arguments (properly mapped to the array supplied
	 * by this method) needs to be supplied to the method
	 * <code>getData()</code>.  This method allows plugin writers some freedom
	 * in implementing the features they need.
	 * */
	public String [] requiredArgs();
}
