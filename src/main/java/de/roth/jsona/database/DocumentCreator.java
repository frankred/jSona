package de.roth.jsona.database;

import de.roth.jsona.genre.GenreManager;
import de.roth.jsona.model.MusicListItem;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

/**
 * Class for creating a lucene searchable document
 *
 * @author Frank Roth
 */
public class DocumentCreator {

    private static final String SPACER = new String(" ");

    /**
     * All meaningful fields (file path, artist, title, album) are added all to
     * the 'all' field seperated by a whitespace. There some more fields: id,
     * file, genre, year
     *
     * @param item
     * @return
     */
    public static Document create(MusicListItem item) {
        Document doc = new Document();

        doc.add(new StringField("id", item.getId(), Field.Store.YES));

        if(item.getFile() != null){
            doc.add(new StringField("file", item.getFile().getAbsolutePath(), Field.Store.YES));
        }

        if(item.getUrl() != null){
            doc.add(new StringField("url", item.getUrl(), Field.Store.YES));
        }

        // put all search relevant stuff in the "all" fields. Only this field
        // will be scanned for later user searches
        StringBuffer all = new StringBuffer();

        if(item.getFile() != null){
            all.append(item.getFile().getAbsolutePath());
        }

        if (item.getArtist() != null && !item.getArtist().equals("")) {
            all.append(SPACER);
            all.append(item.getArtist());
        }

        if (item.getTitle() != null && !item.getTitle().equals("")) {
            all.append(SPACER);
            all.append(item.getTitle());
        }

        if (item.getAlbum() != null && !item.getAlbum().equals("")) {
            all.append(SPACER);
            all.append(item.getAlbum());
        }
        doc.add(new StringField("all", all.toString().toLowerCase(), Field.Store.YES));

        if (item.getGenre() >= 0) {
            String genreName = GenreManager.getInstance().getGenreNameByIndex(item.getGenre());

            if (genreName != null) {
                doc.add(new StringField("genre", genreName.toLowerCase(), Field.Store.YES));
            }
        }

        if (item.getYear() != null) {
            doc.add(new StringField("year", item.getYear(), Field.Store.YES));
        }

        return doc;
    }
}
