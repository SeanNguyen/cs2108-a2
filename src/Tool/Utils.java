package Tool;

import javax.swing.*;
import java.io.File;

public class Utils {
    public final static String wav = "wav";

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
    
    public static String getCategoryFromFileName(String fileName) {
    	String category = fileName.replaceAll("\\d","");
    	return category;
    }
    
    public static boolean isFileExist(String path) {
    	File f = new File(path);
    	if(f.exists() && !f.isDirectory()) { 
    	    return true;
    	}
    	return false;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createAudioIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
