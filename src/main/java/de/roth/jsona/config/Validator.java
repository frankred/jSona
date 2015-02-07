package de.roth.jsona.config;


import de.roth.jsona.config.Config;
import de.roth.jsona.javafx.ViewManagerFX;

import java.io.File;
import java.net.URL;

public class Validator {

    public static boolean isInvalidRelPath(String path) {
        if (path == null || "".equals(path)) {
            return true;
        }

        URL themeFolder = Validator.class.getClassLoader().getResource(path);
        if (themeFolder == null) {
            return true;
        }
        return false;
    }

    public static boolean isInvalidAbsPath(String path) {
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
