package de.roth.jsona.api.youtube;

import de.roth.jsona.information.ArtistCacheInformation;
import de.roth.jsona.information.Link;
import de.roth.jsona.util.Logger;
import de.umass.lastfm.Track;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeAPI {

    public static String getID(String url) {
        Pattern pattern = Pattern.compile("v=[a-zA-Z0-9_-]{11}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            return matcher.group().substring(2);
        }
        return null;
    }

    public static ArrayList<YoutubeVideoStreamURL> getVideoStreamURLs(URL youtubeVideoUrl) throws Exception {
        ArrayList<YoutubeVideoStreamURL> videos = new ArrayList<YoutubeVideoStreamURL>();
        HashSet<String> pageContentResultSets = new HashSet<String>();

        // Load youtube video html page
        String youtubeVideoPageContent = IOUtils.toString(new BufferedReader(new InputStreamReader(youtubeVideoUrl.openStream())));
        String[] list = youtubeVideoPageContent.split("url=");

        // Splitting and regexp magic
        for (String hits : list) {
            if (hits.startsWith("https")) {
                String link = hits.split(",")[0];
                pageContentResultSets.add(link);
            }
        }

        String url = null;
        String itag = null;
        String type = null;

        for (String set : pageContentResultSets) {
            set = set.replace("\\", "\\\\");

            for (String sdata : set.split("\\\\")) {
                if (sdata.startsWith("https"))
                    url = sdata;
            }

            if (url != null) {
                if (url.startsWith("https")) {
                    url = url.split(" ")[0];
                    url = StringEscapeUtils.unescapeJava(url);
                    url = URLDecoder.decode(url, "UTF-8");

                    if (url.contains("mime=")) {
                        type = (url.split("mime=")[1]).split("&")[0];
                    }

                    if (url.contains("itag")) {
                        itag = (url.split("itag=")[1]).split("&")[0];
                    }

                    YoutubeVideoStreamURL youtubeStreamUrl = new YoutubeVideoStreamURL(url, itag);
                    youtubeStreamUrl.setType(type);

                    Pattern p = Pattern.compile("<title>(.*?)</title>");
                    Matcher m = p.matcher(youtubeVideoPageContent);
                    while (m.find() == true) {
                        String title = m.group(1);
                        // Remove " - YouTube" string
                        youtubeStreamUrl.setTitle(title.substring(0, title.length() - 10));
                        break;
                    }

                    videos.add(youtubeStreamUrl);
                    Logger.get().info("Youtube stream detected: " + youtubeStreamUrl);
                }
            }
        }

        return videos;
    }

    public static boolean isYoutubeLink(String url) {
        if (url == null) {
            return false;
        }

        return Pattern.matches("^(https?\\:\\/\\/)?(www\\.youtube\\.com|youtu\\.?be)\\/.+$", url);
    }

    public static String getSearchQueryUrl(String query) {
        try {
            return "http://www.youtube.com/results?search_query=" + URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Link> getYoutubeLinksByTracks(Collection<Track> tracks) {
        List<Link> links = new ArrayList<Link>();

        if (tracks == null) {
            return links;
        }

        for (Track track : tracks) {
            links.add(new Link(YoutubeAPI.getSearchQueryUrl(track.getArtist() + " " + track.getName()), track.getName()));
        }

        return links;
    }
}