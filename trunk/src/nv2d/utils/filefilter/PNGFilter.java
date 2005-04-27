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