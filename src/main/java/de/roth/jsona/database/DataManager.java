package de.roth.jsona.database;

import de.roth.jsona.config.Global;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.model.MusicListItemFile;
import de.roth.jsona.tag.MP3Tagger;
import de.roth.jsona.util.Logger;
import de.roth.jsona.util.Serializer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.util.*;

/**
 * Singleton class that load the cache and manages all cache and lucene actions.
 *
 * @author Frank Roth
 */
public class DataManager {

    private static DataManager instance = new DataManager();
    private HashMap<String, MusicListItem> cache;

    @SuppressWarnings("unchecked")
    public DataManager() {
        this.cache = (HashMap<String, MusicListItem>) Serializer.load(Global.FILES_CACHE);
        if (this.cache == null) {
            this.cache = new HashMap<String, MusicListItem>(5120, 0.5f);
        }
    }

    public static DataManager getInstance() {
        return (instance);
    }

    /**
     * Delete a music item from cache and from lucene.
     *
     * @param item
     */
    public void delete(MusicListItem item) {
        Logger.get().info("- Delete '" + item.toString() + "'.");
        LuceneManager.getInstance().delete(item.getId());
        this.cache.remove(item.toString());
    }

    /**
     * Index MusicListItem without a submit.
     *
     * @param item
     */
    public void addLuceneOnly(MusicListItem item) {
        try {
            LuceneManager.getInstance().add(DocumentCreator.create(item));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create MusicListItem for the over given file, and add the item to cache
     * and index it via lucene.
     *
     */
    public MusicListItem addItem(MusicListItem item) {
        try {
            item.setCreationDate(Calendar.getInstance().getTime());
            item.setKeepInCache(true);
            this.add(item);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Submit lucene changes and save cache to file.
     */
    public void commit() {
        try {
            LuceneManager.getInstance().commit();
            Serializer.save(Global.FILES_CACHE, this.cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete all items in the cache that no longer exists with the help of the
     * tmp_keep_in_cache property and save cache to file.
     */
    public void cleanup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Clean up cache with unsed files
                Iterator<Map.Entry<String, MusicListItem>> iter = cache.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, MusicListItem> pair = (Map.Entry<String, MusicListItem>) iter.next();
                    MusicListItem item = pair.getValue();

                    if (!item.keepInCache()) {
                        Logger.get().info("Remove file '" + item.toString() + "' from cache.");
                        iter.remove();
                    }

                    // Reset tmp property before saving...
                    item.setKeepInCache(false);
                }

                // Save new cache to file
                Serializer.save(Global.FILES_CACHE, cache);
            }
        }).start();
    }

    public MusicListItem get(String path) {
        return cache.get(path);
    }

    /**
     * Update a item in the cache
     *
     * @param item
     */
    public void updateCache(MusicListItem item) {
        this.cache.put(item.toString(), item);
    }

    /**
     * Completely output all indexed music files in lucene. Please use only for
     * debugging only.
     */
    public void outputLucene() {
        List<Document> documents = LuceneManager.getInstance().getAllDocuments();
        for (Document d : documents) {
            if (d == null) {
                continue;
            }
            List<IndexableField> fields = d.getFields();

            for (IndexableField f : fields) {
                System.out.println(f.name() + "=" + f.stringValue());
            }
            System.out.println();
        }
    }

    /**
     * Output the number of cached and indexed lucene items.
     */
    public void outputInfo() {
        Logger.get().info("Cache has " + this.cache.size() + " items.");
        Logger.get().info("Lucene has " + LuceneManager.getInstance().getAmount() + " items.");
    }

    private void add(MusicListItem item) {
        try {
            // tag music file
            if (item instanceof MusicListItemFile) {
                item = MP3Tagger.tag((MusicListItemFile)item);
            }

            // add to lucene
            LuceneManager.getInstance().add(DocumentCreator.create(item));

            // add to cache
            this.cache.put(item.toString(), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retag(MusicListItem item) {
        // Remove item from db
        LuceneManager.getInstance().delete(item.getId());

        // retag music file
        if (item instanceof MusicListItemFile) {
            item = MP3Tagger.tag((MusicListItemFile)item);
        }

        // Add to db
        addLuceneOnly(item);
    }
}
