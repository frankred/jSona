package de.roth.jsona.api.youtube;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class YoutubeAPI {

    public static String getSearchQueryUrl(String query){
        try {
            return "http://www.youtube.com/results?search_query=" + URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
