package de.roth.jsona.external;

import de.roth.jsona.artist.JSonaArtist;
import de.roth.jsona.model.MusicListItem;
import de.umass.lastfm.Track;

import java.util.Collection;

/**
 * Interface for external information listeners
 *
 * @author Frank Roth
 */
public interface ExternalInformationsListener {

    public void artistInformationsReady(MusicListItem item, String artistImagePath, String artistWiki, Collection<Track> artistTopTracks);
    public void artistInformationsReady(MusicListItem item);

}
