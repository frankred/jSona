package de.roth.jsona.api.musicbrainz.test;

import de.roth.jsona.api.musicbrainz.Musicbrainz;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.junit.Before;
import org.junit.Test;

public class MusicbrainzTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        MultiThreadedHttpConnectionManager mgr = new MultiThreadedHttpConnectionManager();
        mgr.getParams().setDefaultMaxConnectionsPerHost(4);
        HttpClient httpClient = new HttpClient(mgr);

        Musicbrainz.findMbidByArtist("Absolute Beginner", httpClient);
    }
}
