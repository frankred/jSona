package de.roth.jsona.file;

import java.io.File;
import java.io.FileFilter;

public class FileUtils {

    public static File[] listSubDirectories(File directory) {

        FileFilter directoryFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        return directory.listFiles(directoryFilter);
    }
}
