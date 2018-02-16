package de.roth.jsona.config;


import java.io.File;
import java.net.URL;

public class Validator {


    public static boolean isInvalidRelativePath(String path) {
        if (path == null || "".equals(path)) {
            return true;
        }

        URL url = Validator.class.getClassLoader().getResource(path);
        if (url == null) {
            return true;
        }
        return false;
    }

    public static boolean isInvalidAbsolutePath(String path) {
        if (path == null || "".equals(path)) {
            return true;
        }

        File f = new File(path);
        if (f.exists()) {
            return false;
        }
        return true;
    }
}
