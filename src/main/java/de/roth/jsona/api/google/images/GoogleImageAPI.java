package de.roth.jsona.api.google.images;

import de.roth.jsona.http.HttpClientHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.util.URIUtil;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class GoogleImageAPI {

    private static final String IMAGE_API = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";

    public static String getFirstImageURL(String query, HttpClient client) throws IOException, JSONException, URISyntaxException {
        String url = IMAGE_API + URLEncoder.encode(query, "UTF-8");
        StringBuffer content = HttpClientHelper.getPageContentAsHttpGet(url, client);
        JSONObject json = new JSONObject(content.toString());
        JSONArray results = json.getJSONObject("responseData").getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            return results.getJSONObject(i).getString("url");
        }
        return null;
    }
}
