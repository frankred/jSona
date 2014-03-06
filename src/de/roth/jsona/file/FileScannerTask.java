package de.roth.jsona.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javafx.concurrent.Task;
import de.roth.jsona.config.Config;
import de.roth.jsona.model.MusicListItem;

public class FileScannerTask extends Task<Void> {

	private File file;
	private int index;
	private ArrayList<MusicListItem> recentlyAdded;
	
	private Calendar now = Calendar.getInstance();
	private Calendar untilPast = Calendar.getInstance();
	private Date nowDate;
	
	private FileTaggerListener ftl;
	private FileScannerListener fsl;
	
	private String target;
	
	public FileScannerTask(File file, int index, FileTaggerListener ftl, FileScannerListener fsl, String target) {
		this.file = file;
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
	}

	@Override
	protected Void call() throws Exception {
		File[] files = { this.file };
		LinkedList<File> items = FileScanner.scan(files, index, true, this.fsl, target);
		FileTagger.tagFiles(this.file, items, this.recentlyAdded, this.nowDate, this.untilPast.getTime(), this.ftl);
		return null;
	}
}
