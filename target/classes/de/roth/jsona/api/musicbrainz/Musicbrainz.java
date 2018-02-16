package de.roth.jsona.api.musicbrainz;

import de.roth.jsona.http.HttpClientHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Abstraction class for musicbrainz.org api calls.
 *
 * @author Frank Roth
 */
public class Musicbrainz {

    public static String MUSICBRAINZ_URL = "http://musicbrainz.org/ws/2";

    /**
     * Find an artist with the over given mbid.
     *
     * @param key    - api search key => artist
     * @param client HttpClient
     * @return
     */
    public static MusicbrainzSearchResult findMbidByArtist(String key, HttpClient client) {
        if (key == null) {
            return null;
        }

        try {
            key = URLEncoder.encode(key, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        String url = MUSICBRAINZ_URL + "/artist?query=" + key;

        try {
            StringBuffer content = HttpClientHelper.getPageContentAsHttpGet(url, client);
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            ArrayList<MusicbrainzSearchResult> mbids = new ArrayList<MusicbrainzSearchResult>();
            xmlReader.setContentHandler(new MusicbrainzXMLv2ContentHandler(mbids));
            xmlReader.parse(new InputSource(new StringReader(content.toString())));

            if (mbids.size() == 0) {
                return null;
            }
            return mbids.get(0);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;
    }
}
