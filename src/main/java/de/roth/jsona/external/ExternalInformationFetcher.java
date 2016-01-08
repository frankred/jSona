package de.roth.jsona.external;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.roth.jsona.api.musicbrainz.Musicbrainz;
import de.roth.jsona.api.musicbrainz.MusicbrainzSearchResult;
import de.roth.jsona.api.youtube.YoutubeAPI;
import de.roth.jsona.config.Global;
import de.roth.jsona.http.HttpClientHelper;
import de.roth.jsona.information.ArtistCacheInformation;
import de.roth.jsona.model.MusicListItemFile;
import de.roth.jsona.model.MusicListItemYoutube;
import de.roth.jsona.util.Logger;
import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;

public class ExternalInformationFetcher {

    private static ExternalInformationFetcher instance; // Artist, Track, User
    private StringSimilarityService stringSimilarityService;

    public ExternalInformationFetcher() {
        this.stringSimilarityService = new StringSimilarityServiceImpl(new JaroWinklerStrategy());
    }

    public static ExternalInformationFetcher getInstance() {
        if (instance == null) {
            instance = new ExternalInformationFetcher();
        }
        return (instance);
    }

    public void collectYoutubeImagePreview(final MusicListItemYoutube item, final HttpClient client, final ExternalInformationListener externalInformationListener, final HashMap<String, String> youtubeCache) throws IOException, JSONException, URISyntaxException {
        Runnable collectYoutubeInformation = new Runnable() {
            @Override
            public void run() {
                Logger.get().info("Collection information for '" + item.getUrl() + "'.");

                String videoID = YoutubeAPI.getID(item.getUrl());
                Logger.get().info("Video ID detected '" + videoID + "'");

                String imageURL = "http://img.youtube.com/vi/" + videoID + "/maxresdefault.jpg";
                String imageFilePath = getYoutubeImageFilePath(videoID);

                if (HttpClientHelper.downloadFile(imageURL, imageFilePath, client) == false) {
                    Logger.get().info("Image download from '" + imageURL + "' failed.");
                    return;
                }

                item.setImagePath(imageFilePath);
                saveYoutubeInformationToCache(item.getUrl(), imageFilePath, youtubeCache);
                externalInformationListener.ready(item);
            }
        };
        new Thread(collectYoutubeInformation).start();
    }

    public void collectArtistInformation(final MusicListItemFile item, final HttpClient client, final ExternalInformationListener externalInformationListener, final HashMap<String, ArtistCacheInformation> artistsCache) throws IOException, JSONException, URISyntaxException {
        Runnable collectArtistInformation = new Runnable() {
            @Override
            public void run() {
                Logger.get().info("Collection informations for '" + item.getArtist() + "'.");

                Artist artist = searchArtistOnLastFMInfo(item.getArtist());

                if (artist == null) {
                    artist = searchArtistOnLastFMSearch(item.getArtist());
                }

                if (artist == null) {
                    artist = searchArtistOnMusicbrainz(item.getArtist(), client);
                }

                if (artist == null) {
                    artist = searchArtistWithLastFMCorrection(item.getArtist(), client);
                }

                if (artist == null) {
                    externalInformationListener.ready(item);
                }

                item.setSummary(artist.getWikiSummary());

                String artistImageUrl = getBiggestImageUrl(artist);
                String artistImageFilePath = null;

                // Image found
                if (StringUtils.isNotBlank(artistImageUrl)) {
                    createFolderIfNotExists(Global.ARTIST_IMAGE_FOLDER);
                    artistImageFilePath = Global.ARTIST_IMAGE_FOLDER + File.separator + Base64.getEncoder().encodeToString(artist.getName().getBytes());

                    // Download
                    boolean downloadSucceed = HttpClientHelper.downloadFile(artistImageUrl, artistImageFilePath, client);

                    if (downloadSucceed == false) {
                        Logger.get().info("Image download from '" + artistImageUrl + "' failed.");
                        return;
                    }
                    item.setImagePath(artistImageFilePath);
                    Logger.get().info("Image download from  '" + artistImageUrl + "' to the file '" + artistImageFilePath + "'.");

                    ArtistCacheInformation artistCacheInformation = new ArtistCacheInformation(artist, artistImageFilePath);
                    externalInformationListener.ready(item);
                    saveArtistInformationToCache(item.getArtist(), artistCacheInformation, artistsCache);
                }

                // Top Tracks
                Collection<Track> artistTopTracks = getLastFMTopTracks(artist.getName());
                if (artistTopTracks != null && artistTopTracks.size() > 0) {
                    item.setLinks(YoutubeAPI.getYoutubeLinksByTracks(artistTopTracks));

                    ArtistCacheInformation artistCacheInformation = new ArtistCacheInformation(artist, artistImageFilePath, artistTopTracks);
                    externalInformationListener.ready(item);
                    saveArtistInformationToCache(item.getArtist(), artistCacheInformation, artistsCache);
                }
            }
        };
        new Thread(collectArtistInformation).start();
    }


    private Collection<Track> getLastFMTopTracks(String artistQuery) {
        try {
            Collection<Track> tracks = Artist.getTopTracks(artistQuery, Global.LASTFM_API_KEY);
            return tracks;
        } catch (Exception e) {
            Logger.get().info("Error during LastFM.information.getTopTracks('" + artistQuery + "'), maybe API out of date or no internet connection.");
        }
        return null;
    }

    private String getYoutubeImageFilePath(String videoID) {
        return Global.YOUTUBE_IMAGE_FOLDER + File.separator + videoID + ".jpg";
    }

    private void saveYoutubeInformationToCache(String url, String imagePath, HashMap<String, String> youtubeCache) {
        youtubeCache.put(url, imagePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String artistsJson = gson.toJson(youtubeCache);
        try {
            FileWriter writer = new FileWriter(new File(Global.YOUTUBE_JSON));
            writer.write(artistsJson);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveArtistInformationToCache(String key, ArtistCacheInformation cachableArtist, HashMap<String, ArtistCacheInformation> informationCache) {
        informationCache.put(key, cachableArtist);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String artistsJson = gson.toJson(informationCache);
        try {
            FileWriter writer = new FileWriter(new File(Global.ARTISTS_JSON));
            writer.write(artistsJson);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFolderIfNotExists(String folder) {
        File imgFolder = new File(folder);
        if (!imgFolder.exists()) {
            imgFolder.mkdir();
        }
    }

    private Artist getMostSimilarArtistBetterAndThenFiftyPercentScore(String name, Collection<Artist> artists) {
        double score = 0;
        Artist artist = null;

        for (Artist a : artists) {
            double currentScore = stringSimilarityService.score(name, a.getName());
            if (currentScore > score) {
                score = currentScore;
                artist = a;
            }
        }
        if (score <= 0.5d) {
            return null;
        }
        return artist;
    }

    private Artist searchArtistOnLastFMInfo(String artistQuery) {
        Artist foundArtist = Artist.getInfo(artistQuery, Global.LASTFM_API_KEY);

        if (foundArtist == null) {
            return null;
        }

        if (foundArtist.getName().trim().toLowerCase().equals(artistQuery.trim().toLowerCase())) {
            Logger.get().info("LastFM.information.getInfo('" + artistQuery + "')" + ": '" + foundArtist.getName() + "'.");
            return foundArtist;
        } else {
            Logger.get().info("LastFM.information.getInfo('" + artistQuery + "')" + ": No match found.");
            return null;
        }
    }

    private Artist searchArtistOnLastFMSearch(String artistQuery) {
        try {
            Collection<Artist> artists = Artist.search(artistQuery, Global.LASTFM_API_KEY);

            if (artists.size() == 0) {
                return null;
            }

            Artist mostSimilarArtist = getMostSimilarArtistBetterAndThenFiftyPercentScore(artistQuery, artists);

            if (mostSimilarArtist == null) {
                return null;
            }

            Logger.get().info("LastFM.information.search('" + artistQuery + "')" + ": Most similar information '" + mostSimilarArtist.getName() + "'.");

            Artist foundArtist = Artist.getInfo(mostSimilarArtist.getName(), Global.LASTFM_API_KEY);

            if (foundArtist == null) {
                return null;
            }

            Logger.get().info("LastFM.information.getInfo('" + mostSimilarArtist.getName() + "')" + ": '" + foundArtist.getName() + "'.");

            return foundArtist;
        } catch (Exception e) {
            Logger.get().info("Error during LastFM.information.search('" + artistQuery + "'), maybe API out of date or no internet connection.");
        }
        return null;
    }

    private Artist searchArtistOnMusicbrainz(String artistQuery, HttpClient client) {
        MusicbrainzSearchResult searchResult = Musicbrainz.findMbidByArtist(artistQuery, client);

        if (searchResult == null) {
            return null;
        }

        Logger.get().info("Musicbrainz.org findMbidByArtist('" + artistQuery + "'): " + searchResult.getMbid());
        Artist foundArtist = Artist.getInfo(searchResult.getMbid(), Global.LASTFM_API_KEY);

        if (foundArtist == null) {
            return null;
        }
        Logger.get().info("LastFM.information.getInfo('" + searchResult.getMbid() + "')" + ": '" + foundArtist.getName() + "'.");

        return foundArtist;
    }

    private Artist searchArtistWithLastFMCorrection(String artistQuery, HttpClient client) {
        Artist foundArtist = Artist.getCorrection(artistQuery, Global.LASTFM_API_KEY);

        if (foundArtist == null) {
            Logger.get().info("No information found on last.fm and musicbrainz.org for '" + artistQuery + "'.");
            return null;
        }

        Logger.get().info("LastFM.information.getCorrection('" + artistQuery + "'): " + foundArtist.getName());

        return foundArtist;
    }

    private String getBiggestImageUrl(Artist artist) {
        if (artist.getImageURL(ImageSize.ORIGINAL) != null) {
            return artist.getImageURL(ImageSize.ORIGINAL);
        } else if (artist.getImageURL(ImageSize.MEGA) != null) {
            return artist.getImageURL(ImageSize.MEGA);
        } else if (artist.getImageURL(ImageSize.HUGE) != null) {
            return artist.getImageURL(ImageSize.HUGE);
        } else if (artist.getImageURL(ImageSize.EXTRALARGE) != null) {
            return artist.getImageURL(ImageSize.EXTRALARGE);
        } else if (artist.getImageURL(ImageSize.LARGE) != null) {
            return artist.getImageURL(ImageSize.LARGE);
        } else if (artist.getImageURL(ImageSize.LARGESQUARE) != null) {
            return artist.getImageURL(ImageSize.LARGESQUARE);
        } else if (artist.getImageURL(ImageSize.MEDIUM) != null) {
            return artist.getImageURL(ImageSize.MEDIUM);
        } else if (artist.getImageURL(ImageSize.SMALL) != null) {
            return artist.getImageURL(ImageSize.SMALL);
        }
        return null;
    }
}