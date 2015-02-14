package de.roth.jsona.file;

import de.roth.jsona.config.Config;
import de.roth.jsona.model.MusicListItem;
import javafx.concurrent.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Task to scan and tag the over given files and create a for "recently added" items.
 *
 * @author Frank Roth
 */
public class FileScannerTask extends Task<Void> {

    private File rootFolder;
    private int index;
    private ArrayList<MusicListItem> recentlyAdded;

    private Calendar now = Calendar.getInstance();
    private Calendar untilPast = Calendar.getInstance();
    private Date nowDate;

    private FileTaggerListener ftl;
    private FileScannerListener fsl;

    private String target;

    private boolean createRecentlyAddedList;

    public FileScannerTask(File file, int index, FileTaggerListener ftl, FileScannerListener fsl, String target, boolean createRecentlyAddedList) {
        this.rootFolder = file;
        this.index = index;
        if (this.index < 0) {
            this.index = 0;
        }
        this.recentlyAdded = new ArrayList<MusicListItem>();
        this.nowDate = now.getTime();
        this.untilPast.add(Calendar.DATE, Config.getInstance().RECENTLY_ADDED_UNITL_TIME_IN_DAYS);

        this.ftl = ftl;
        this.fsl = fsl;
        this.target = target;

        this.createRecentlyAddedList = createRecentlyAddedList;
    }

    @Override
    protected Void call() throws Exception {
        File[] files = {this.rootFolder};
        LinkedList<File> items = FileScanner.scan(files, index, true, this.fsl, target);
        if (this.createRecentlyAddedList) {
            FileTagger.tagFiles(this.rootFolder, items, this.recentlyAdded, this.nowDate, this.untilPast.getTime(), this.ftl);
        } else {
            FileTagger.tagFiles(this.rootFolder, items, this.nowDate, this.untilPast.getTime(), this.ftl);
        }
        return null;
    }
}
