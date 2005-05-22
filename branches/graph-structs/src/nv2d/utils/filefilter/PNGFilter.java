/**
 * NV2D - Social Network Visualization
 * Copyright (C) 2005 Sam Prentice
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

package nv2d.utils.filefilter;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * PNGFilter
 * 
 * Filters PNG files in a directory.
 * 
 * @author sam
 */
public class PNGFilter extends FileFilter {

    public boolean accept(File f) {
        // Accept all directories
        if (f.isDirectory()) {
            return true;
        }

        // Accept all PNG's
        String extension = FileFilterUtils.getExtension(f);
        if (extension != null) {
            if (extension.equals(FileFilterUtils.png)) {
                return true;
            }
        }

        return false;
    }

    public String getDescription() {
        return "PNG Images (.png)";
    }
}