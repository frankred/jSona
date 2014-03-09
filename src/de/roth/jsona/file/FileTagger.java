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

		int progress = 0;
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
			}

			// old file -> check modification
			else {
				item.setTmp_keep_in_cache(true);

				// Check for changes
				if (item.getLastFileModification() > f.lastModified()) {
					somethingChanged = true;

					DataManager.getInstance().delete(item);

					// create item
					item = DataManager.getInstance().add(f, rootFolder);
				} else {
					// add du lucene only
					DataManager.getInstance().addLuceneOnly(item);
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

			// Add
			items.add(item);
			listener.taggerProgress(progress, item);
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
