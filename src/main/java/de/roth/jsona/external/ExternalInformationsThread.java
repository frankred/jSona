package de.roth.jsona.external;

import de.roth.jsona.http.ImageType;
import de.roth.jsona.information.ArtistCacheInformation;
import de.roth.jsona.model.MusicListItem;
import org.apache.commons.httpclient.HttpClient;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Thread to load external informations
 *
 * @author Frank Roth
 */
public class ExternalInformationsThread implements Runnable {

    private MusicListItem item;
    private HttpClient client;
    private ImageType type;
    private ExternalArtistInformationListener l;
    private HashMap<String, ArtistCacheInformation> cachedArtists;

    public ExternalInformationsThread(HttpClient client, MusicListItem item, ImageType type, ExternalArtistInformationListener l, HashMap<String, ArtistCacheInformation> cachedArtists) {
        this.client = client;
        this.item = item;
        this.type = type;
        this.l = l;
        this.cachedArtists = cachedArtists;
    }

    @Override
    public void run() {
        try {
            ExternalInformationFetcher.getInstance().collectArtistInformations(item, client, type, l, cachedArtists);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
