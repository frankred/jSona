package de.roth.jsona.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import de.roth.jsona.database.DataManager;
import de.roth.jsona.database.LuceneManager;
import de.roth.jsona.model.MusicListItem;

/**
 * Util class to create music items for each new file and tag files. Also create
 * lucene indexes with the help of the DataManager.
 *
 * @author Frank Roth
 *
 */
public class FileTagger {

	public static void tagFiles(File rootFolder, LinkedList<File> files, ArrayList<MusicListItem> recentlyAdded, Date nowDate, Date recentlyAddedDeadline, FileTaggerListener listener) {

		ArrayList<MusicListItem> items = new ArrayList<>(files.size());

		listener.taggerStart(files.size());

		int total = files.size();
		int progress = 1;
		
		boolean somethingChanged = false;
		File currentParentFolder = files.getFirst().getParentFile();
		int colorClassCounter = 0;

		// Tag all files - taking time...
		for (File f : files) {

			// Check if file already exists in cache
			MusicListItem item = DataManager.getInstance().get(f);

			// new file -> tag it
			if (item == null) {
				somethingChanged = true;
				// create item
				item = DataManager.getInstance().add(f, rootFolder);
				item.setTmp_keep_in_cache(true);
				recentlyAdded.add(item);
				
				// File added to cache and lucene
				listener.taggerProgress(progress, total, item, true, true);
			}

			// old file -> check modification
			else {
				// Found in cache, it's needed again so keep file in cache
				item.setTmp_keep_in_cache(true);

				// Check for changes
				if (item.getLastFileModification() > f.lastModified()) {
					somethingChanged = true;

					DataManager.getInstance().delete(item);

					// create item

					item = DataManager.getInstance().add(f, rootFolder);

					// File changed so add to cache and lucene
					listener.taggerProgress(progress, total, item, true, true);
				} else {

					// add to lucene only
					DataManager.getInstance().addLuceneOnly(item);
					
					// File added to lucene, not to cache
					listener.taggerProgress(progress, total, item, false, true);
				}

				// Check if is recently added
				if (item.getCreationDate().after(recentlyAddedDeadline)) {
					recentlyAdded.add(item);
				}
			}

			// Set color
			if (!currentParentFolder.getAbsolutePath().equals(f.getParentFile().getAbsolutePath())) {
				colorClassCounter = (colorClassCounter + 1) % 12;
			}
			item.setColorClass(colorClassCounter);
			currentParentFolder = f.getParentFile();
			
			item.setTmp_status(MusicListItem.Status.SET_NONE);

			// Add
			items.add(item);
			++progress;
		}

		if (somethingChanged) {
			DataManager.getInstance().commit();
		} else {
			LuceneManager.getInstance().commit();
		}

		listener.taggingFinished(rootFolder, somethingChanged, items, recentlyAdded);
	}
}
