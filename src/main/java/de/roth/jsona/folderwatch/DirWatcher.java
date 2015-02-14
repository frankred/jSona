package de.roth.jsona.folderwatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction to watch directories
 *
 * @author 933
 */
public class DirWatcher {

    private List<Thread> watchThreads;

    public DirWatcher() {
        watchThreads = new ArrayList<Thread>();
    }

    /**
     * Watch a directory
     *
     * @param folder
     * @param l      - Listener
     * @throws java.io.IOException
     */
    public void watch(File folder, WatchDirListener l) throws IOException {
        WatchDirThread f = new WatchDirThread(Paths.get(folder.getAbsolutePath()), l);
        Thread t = new Thread(f);
        watchThreads.add(t);
        t.start();
    }
}
