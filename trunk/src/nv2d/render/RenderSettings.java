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

package nv2d.render;

public class RenderSettings {
	public static final int ANTIALIAS = 0;
	public static final int SHOW_LABELS = 1;
	public static final int SHOW_STRESS = 2;
	public static final int SHOW_LENGTH = 3;

	public static final String TYPE_BOOLEAN = "bool";

	/* uniqueID, description, setting */
	static String [][] _settings = {
		{/*antialias*/ TYPE_BOOLEAN, "t", "Anti-alias rendering."},
		{/*show_arrows*/ TYPE_BOOLEAN, "t", "Show labels for each vertex."},
		{/*show_einfo*/ TYPE_BOOLEAN, "f", "Show stress value for each edge."},
		{/*show_vinfo*/ TYPE_BOOLEAN, "f", "Show edge length next to each edge."},
		{/*show_ginfo*/ TYPE_BOOLEAN, "f", "Show graph information and statistics."},
		{/*show_ginfo*/ TYPE_BOOLEAN, "f", "Show graph vertices measures."}
		//{/**/, "", ""}
		//{/**/, "", ""}
	};

	public void setBoolean(int setting, boolean value) {
		if(setting < 0 || setting >= _settings.length || !_settings[setting][0].equals(TYPE_BOOLEAN)) {
			// programming error
			System.err.print("RenderSettings.setBoolean(int, boolean): index out of range or type not boolean.");
			return;
		}
		_settings[setting][1] = (value ? "t" : "f");
	}

	public boolean getBoolean(int setting) {
		if(setting < 0 || setting >= _settings.length || !_settings[setting][0].equals(TYPE_BOOLEAN)) {
			// programming error
			System.err.print("RenderSettings.setBoolean(int, boolean): index out of range or type not boolean.");
			return false;
		}
		return _settings[setting][1].equals("t") ? true : false;
	}
}
