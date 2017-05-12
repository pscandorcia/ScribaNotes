package com.materialnotes.util;

/**
 * Created by Pablo Saavedra on 12/05/2017.
 */

public class FilenameUtils {
    public static String getExtension(String filename){
        String ext = "";
        int lastDotPos = filename.lastIndexOf(".");
        int lastBarPos = filename.lastIndexOf("/");
        if (lastBarPos>lastDotPos)
            return "";
        ext = filename.substring(lastDotPos);
        return ext;
    }
}
