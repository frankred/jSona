package de.roth.jsona.file;

import java.io.File;
import java.util.ArrayList;

import de.roth.jsona.model.MusicListItem;

public interface FileTaggerListener {

	public void taggerStart(int filesAmount);

	public void taggerProgress(int current, int total, MusicListItem item, boolean addedToCache, boolean addedToLucene);

	public void taggingFinished(File rootFile, boolean cacheHasChanged, ArrayList<MusicListItem> items, ArrayList<MusicListItem> recentlyAddedItems);

}
