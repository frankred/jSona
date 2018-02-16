package de.roth.jsona.tag;

import com.mpatric.mp3agic.*;
import com.mpatric.mp3agic.ID3v1;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.model.MusicListItemFile;
import de.roth.jsona.util.Logger;


import java.io.IOException;

public class MP3Tagger {

    public static MusicListItem tag(MusicListItemFile item) {

        if (item.getFile() == null) {
            return item;
        }

        try {
            Mp3File mp3file = new Mp3File(item.getFile(), 32768, false);

            if (mp3file.hasId3v1Tag()) {
                ID3v1 v1 = mp3file.getId3v1Tag();
                item.setTitle(v1.getTitle());
                item.setArtist(v1.getArtist());
                item.setAlbum(v1.getAlbum());
                item.setTrackNo(v1.getTrack());
                item.setYear(v1.getYear());
                item.setGenre(v1.getGenreDescription());
            }

            if (mp3file.hasId3v2Tag()) {
                ID3v1 v2 = mp3file.getId3v2Tag();
                item.setTitle(v2.getTitle());
                item.setArtist(v2.getArtist());
                item.setAlbum(v2.getAlbum());
                item.setTrackNo(v2.getTrack());
                item.setYear(v2.getYear());
                item.setGenre(v2.getGenreDescription());
            }
        } catch (IOException e) {
            Logger.get().error(e);
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            Logger.get().error(e);
            e.printStackTrace();
        } catch (InvalidDataException e) {
            Logger.get().error(e);
            e.printStackTrace();
        }
        return item;
    }
}