package de.roth.jsona.external;

import de.roth.jsona.artist.JSonaArtist;
import de.roth.jsona.model.MusicListItem;

/**
 * Interface for external information listeners
 * 
 * @author Frank Roth
 * 
 */
public interface ExternalInformationsListener {

	public void artistInformationsReady(MusicListItem item, JSonaArtist artist);

}
