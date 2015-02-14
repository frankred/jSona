package de.roth.jsona.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * File helper class
 *
 * @author Frank Roth
 */
public class FileUtil {

    /**
     * Copy an internal jar path file to the filesystem.
     *
     * @param jarPath
     * @param external
     */
    public static void copyFile(URL jarPath, File external) {
        if (external.exists()) {
            return;
        }

        try {
            FileUtils.copyURLToFile(jarPath, external);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
