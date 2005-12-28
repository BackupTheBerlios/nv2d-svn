/**
 * NV2D - Social Network Visualization
 * FileExport.java - June 6, 2005
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

package nv2d.plugins.standard.export;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/* Copyright (C) 2005 Eddie Fagin */
public class FileExport {
    
    private PrintWriter _out = null;
    private String _path = null;
    private File _file = null;
    
    /** Default constructor. */
    public FileExport() {}


    /** Returns the private file data member. */    
    public File file() {
        return _file;
    }
    
    
    /** Attempts to create or open file for writing. */
    public void open() {
        try {
            getFile();
        } catch (IOException e) {
            System.err.print(e.toString());
        }
    }
    
    /** Closes output stream. This is necessary to ensure a saved file. */
    public void close() {
        _out.close();
    }
    
    /** Takes a string and writes it to the file. This is an abstraction meant
     * to allow an easy switch between console and file output. */
    public void print(String s) {
           _out.write(s);        
    }
    
    /** Takes a string and writes it plus a line return to the file. 
     * This is an abstraction meant to allow an easy switch between console 
     * and file output. */
    public void println(String s) {
           _out.write(s + '\n');
        
    }
    
    /** Uses a JFileChooser to select a file, then checks the File object for
      writability. It also sets up the writer for use. */
    private void getFile() throws IOException {
       JFileChooser fc = new JFileChooser();

       if (fc.showSaveDialog(fc) == JFileChooser.APPROVE_OPTION) {
		_file = fc.getSelectedFile();
       }
       
        // Create and test the new file for writability
        try {  
            // If the file exists and is unwritable, generate exception
            if (_file.exists() && !_file.canWrite()) {
                throw (new IOException("Could not write to already existing file"));
            }
            
            // If the file does not exist, create it
            else if (!_file.exists()) {
                _file.createNewFile();
            }
            
            if (!_file.canWrite()) {
                System.err.print("Could not write to file.");
            }
            // Establish connection to output stream
            _out = new PrintWriter(new FileWriter(_file));            
        } catch (IOException e){
            throw (new IOException("Could not write to file."));
        } catch (NullPointerException e){
        }       
    }
    
}

