package de.roth.jsona.file;

import de.roth.jsona.model.MusicListItem;

import java.io.File;
import java.util.ArrayList;

public interface FileTaggerListener {

    public void taggerStart(int filesAmount);

    public void taggerProgress(int current, int total, MusicListItem item, boolean addedToCache, boolean addedToLucene);

    public void taggingFinished(File rootFile, boolean cacheHasChanged, ArrayList<MusicListItem> items, ArrayList<MusicListItem> recentlyAddedItems);

}
