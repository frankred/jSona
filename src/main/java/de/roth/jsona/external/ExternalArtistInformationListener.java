package de.roth.jsona.external;

import de.roth.jsona.information.ArtistCacheInformation;
import de.roth.jsona.model.MusicListItem;

/**
 * Interface for external information listeners
 *
 * @author Frank Roth
 */
public interface ExternalArtistInformationListener {

    public void ready(MusicListItem item, ArtistCacheInformation artistInformation);

    public void ready(MusicListItem item);

}