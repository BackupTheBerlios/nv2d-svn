package nv2d.utils.filefilter;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * JPGFilter
 * 
 * Filters JPG files in a directory.
 * 
 * @author sam
 */
public class JPGFilter extends FileFilter {

    public boolean accept(File f) {
        // Accept all directories
        if (f.isDirectory()) {
            return true;
        }

        // Accept all JPG's
        String extension = FileFilterUtils.getExtension(f);
        if (extension != null) {
            if (extension.equals(FileFilterUtils.jpeg) || extension.equals(FileFilterUtils.jpg)) {
                return true;
            }
        }

        return false;
    }

    public String getDescription() {
        return "JPEG Images (.jpg)";
    }
}