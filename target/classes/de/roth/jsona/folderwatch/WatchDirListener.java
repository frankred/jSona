package de.roth.jsona.folderwatch;

import java.nio.file.Path;

public interface WatchDirListener {

    public void fileCreated(Path pathCreated, Path pathToWatch);

    public void fileDeleted(Path pathDeleted, Path pathToWatch);

    public void fileModified(Path pathModified, Path pathToWatch);

}
