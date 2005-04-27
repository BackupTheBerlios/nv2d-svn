package nv2d.utils.filefilter;

import java.io.File;

/**
 * FileFilterUtils
 * 
 * Adapted from Java FileFilter Tutorial
 * 
 * @author sam
 * */
public class FileFilterUtils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String png = "png";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

}
