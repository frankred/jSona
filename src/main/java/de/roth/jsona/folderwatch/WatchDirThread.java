package de.roth.jsona.folderwatch;

import java.io.IOException;
import java.nio.file.Path;

public class WatchDirThread implements Runnable {

    private Path path;
    private WatchDirListener listener;

    public WatchDirThread(Path pathToWatch, WatchDirListener listener) {
        this.path = pathToWatch;
        this.listener = listener;
    }

    @Override
    public void run() {
        WatchDir watchDir;
        try {
            watchDir = new WatchDir(path, true);    // recursive = true
            watchDir.addEntryListener(listener);
            watchDir.processEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
