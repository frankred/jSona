package de.roth.jsona.external;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import de.roth.jsona.api.google.images.GoogleImageAPI;
import de.roth.jsona.information.ArtistCacheInformation;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

import org.apache.commons.httpclient.HttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.roth.jsona.api.musicbrainz.Musicbrainz;
import de.roth.jsona.api.musicbrainz.MusicbrainzSearchResult;
import de.roth.jsona.config.Global;
import de.roth.jsona.http.HttpClientHelper;
import de.roth.jsona.http.ImageType;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.util.Logger;
import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;

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

    private void downloadAndShowArtistInformation(MusicListItem item, Artist artist, String artistImageUrl, String artistImageFilePath, HttpClient client, ExternalArtistInformationListener externalArtistInformationListener) {
        boolean downloadSucceed = HttpClientHelper.downloadFile(artistImageUrl, artistImageFilePath, client);

        if (downloadSucceed == false) {
            Logger.get().log(Level.INFO, "Image downloaded from last.fm for information '" + artist.getName() + "' failed.");
            return;
        }

        Logger.get().log(Level.INFO, "Image downloaded from last.fm for information '" + artist.getName() + "' to the file '" + artistImageFilePath + "'.");
        externalArtistInformationListener.ready(item, new ArtistCacheInformation(artist, artistImageFilePath, null));
    }

    private Collection<Track> getLastFMTopTracks(String artistQuery) {
        try {
            Collection<Track> tracks = Artist.getTopTracks(artistQuery, Global.LASTFM_API_KEY);
            return tracks;
        } catch (Exception e) {
            Logger.get().log(Level.INFO, "Error during LastFM.information.getTopTracks('" + artistQuery + "'), maybe API out of date or no internet connection.");
        }
        return null;
    }

    private String getArtistImageFilePath(Artist artist) {
        return Global.ARTIST_IMAGE_FOLDER + File.separator + Base64.getEncoder().encodeToString(artist.getName().getBytes());
    }

    public void collectArtistInformations(MusicListItem item, HttpClient client, ImageType type, ExternalArtistInformationListener externalArtistInformationListener, HashMap<String, ArtistCacheInformation> artistsCache) {

        if (item.getArtist() == null) {
            saveInformationToCache(item, new ArtistCacheInformation(), artistsCache);
            return;
        }

        Logger.get().log(Level.INFO, "Collection informations for '" + item.getArtist() + "'.");

        Artist artist = this.searchArtistOnLastFMInfo(item.getArtist());

        if (artist == null) {
            artist = this.searchArtistOnLastFMSearch(item.getArtist());
        }

        if (artist == null) {
            artist = searchArtistOnMusicbrainz(item.getArtist(), client);
        }

        if (artist == null) {
            artist = searchArtistWithLastFMCorrection(item.getArtist(), client);
        }

        ArtistCacheInformation artistCacheInformation = null;

        // No information found...exit
        if (artist == null) {
            artistCacheInformation = new ArtistCacheInformation();
            saveInformationToCache(item, artistCacheInformation, artistsCache);
            return;
        }

        String artistImageUrl = getBiggestImageUrl(artist);
        String artistImageFilePath = null;

        // Image found -> Download image and refresh view
        if (StringUtils.isNotBlank(artistImageUrl)) {
            createFolderIfNotExists(Global.ARTIST_IMAGE_FOLDER);
            artistImageFilePath = getArtistImageFilePath(artist);
            downloadAndShowArtistInformation(item, artist, artistImageUrl, artistImageFilePath, client, externalArtistInformationListener);
            artistCacheInformation = new ArtistCacheInformation(artist, artistImageFilePath);
            saveInformationToCache(item, artistCacheInformation , artistsCache);
        }

        // Top Tracks
        Collection<Track> artistTopTracks = getLastFMTopTracks(artist.getName());
        if (artistTopTracks != null && artistTopTracks.size() > 0) {
            artistCacheInformation = new ArtistCacheInformation(artist, artistImageFilePath, artistTopTracks);
            externalArtistInformationListener.ready(item, artistCacheInformation);
            saveInformationToCache(item, artistCacheInformation, artistsCache);
        }
    }

    private void saveInformationToCache(MusicListItem item, ArtistCacheInformation cachableArtist, HashMap<String, ArtistCacheInformation> informationCache) {
        informationCache.put(item.getArtist(), cachableArtist);
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


    private String fetchGoogleImage(String query, HttpClient client) {
        try {
            return GoogleImageAPI.getFirstImageURL(query, client);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Artist searchArtistOnLastFMInfo(String artistQuery) {
        Artist foundArtist = Artist.getInfo(artistQuery, Global.LASTFM_API_KEY);

        if (foundArtist == null) {
            return null;
        }

        if (foundArtist.getName().trim().toLowerCase().equals(artistQuery.trim().toLowerCase())) {
            Logger.get().log(Level.INFO, "LastFM.information.getInfo('" + artistQuery + "')" + ": '" + foundArtist.getName() + "'.");
            return foundArtist;
        } else {
            Logger.get().log(Level.INFO, "LastFM.information.getInfo('" + artistQuery + "')" + ": No match found.");
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

            Logger.get().log(Level.INFO, "LastFM.information.search('" + artistQuery + "')" + ": Most similar information '" + mostSimilarArtist.getName() + "'.");

            Artist foundArtist = Artist.getInfo(mostSimilarArtist.getName(), Global.LASTFM_API_KEY);

            if (foundArtist == null) {
                return null;
            }

            Logger.get().log(Level.INFO, "LastFM.information.getInfo('" + mostSimilarArtist.getName() + "')" + ": '" + foundArtist.getName() + "'.");

            return foundArtist;
        } catch (Exception e) {
            Logger.get().log(Level.INFO, "Error during LastFM.information.search('" + artistQuery + "'), maybe API out of date or no internet connection.");
        }
        return null;
    }

    private Artist searchArtistOnMusicbrainz(String artistQuery, HttpClient client) {
        MusicbrainzSearchResult searchResult = Musicbrainz.findMbidByArtist(artistQuery, client);

        if (searchResult == null) {
            return null;
        }

        Logger.get().log(Level.INFO, "Musicbrainz.org findMbidByArtist('" + artistQuery + "'): " + searchResult.getMbid());
        Artist foundArtist = Artist.getInfo(searchResult.getMbid(), Global.LASTFM_API_KEY);

        if (foundArtist == null) {
            return null;
        }
        Logger.get().log(Level.INFO, "LastFM.information.getInfo('" + searchResult.getMbid() + "')" + ": '" + foundArtist.getName() + "'.");

        return foundArtist;
    }

    private Artist searchArtistWithLastFMCorrection(String artistQuery, HttpClient client) {
        Artist foundArtist = Artist.getCorrection(artistQuery, Global.LASTFM_API_KEY);

        if (foundArtist == null) {
            Logger.get().log(Level.INFO, "No information found on last.fm and musicbrainz.org for '" + artistQuery + "'.");
            return null;
        }

        Logger.get().log(Level.INFO, "LastFM.information.getCorrection('" + artistQuery + "'): " + foundArtist.getName());

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