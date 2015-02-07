package de.roth.jsona.external;

import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;

import de.roth.jsona.information.Information;
import de.roth.jsona.http.ImageType;
import de.roth.jsona.model.MusicListItem;

/**
 * Thread to load external informations
 * 
 * @author Frank Roth
 * 
 */
public class ExternalInformationsThread implements Runnable {

	private MusicListItem item;
	private HttpClient client;
	private ImageType type;
	private ExternalArtistInformationListener l;
	private HashMap<String, Information> cachedArtists;

	public ExternalInformationsThread(HttpClient client, MusicListItem item, ImageType type, ExternalArtistInformationListener l, HashMap<String, Information> cachedArtists) {
		this.client = client;
		this.item = item;
		this.type = type;
		this.l = l;
		this.cachedArtists = cachedArtists;
	}

	@Override
	public void run() {
		ExternalInformationFetcher.getInstance().collectArtistInformations(item, client, type, l, cachedArtists);
	}
}
