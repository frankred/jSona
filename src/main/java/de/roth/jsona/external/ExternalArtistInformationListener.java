package de.roth.jsona.external;

import de.roth.jsona.model.MusicListItem;
import de.umass.lastfm.Track;

import java.util.Collection;

/**
 * Interface for external information listeners
 *
 * @author Frank Roth
 */
public interface ExternalArtistInformationListener {

    public void ready(MusicListItem item, String artistImagePath, String artistWiki, Collection<Track> artistTopTracks);
    public void ready(MusicListItem item);

}