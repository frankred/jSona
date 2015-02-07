package de.roth.jsona.api.google.images.test;

import de.roth.jsona.api.google.images.GoogleImageAPI;
import de.roth.jsona.api.musicbrainz.Musicbrainz;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.codehaus.jettison.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class GoogleImageAPITest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws IOException, JSONException {
        MultiThreadedHttpConnectionManager mgr = new MultiThreadedHttpConnectionManager();
        mgr.getParams().setDefaultMaxConnectionsPerHost(4);
        HttpClient httpClient = new HttpClient(mgr);
        String imageUrl = GoogleImageAPI.getFirstImageURL("Obama", httpClient);
        Assert.assertNotNull(imageUrl);
    }
}
