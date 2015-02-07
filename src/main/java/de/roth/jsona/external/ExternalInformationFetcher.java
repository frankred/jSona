package de.roth.jsona.external;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

import de.roth.jsona.api.google.images.GoogleImageAPI;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

import org.apache.commons.httpclient.HttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.roth.jsona.api.musicbrainz.Musicbrainz;
import de.roth.jsona.api.musicbrainz.MusicbrainzSearchResult;
import de.roth.jsona.artist.JSonaArtist;
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

    private void downloadAndShowArtistInformation(MusicListItem item, Artist artist, String artistImageUrl, String artistImageFilePath, HttpClient client, ExternalInformationsListener externalInformationsListener) {
        boolean downloadSucceed = HttpClientHelper.downloadFile(artistImageUrl, artistImageFilePath, client);

        if (downloadSucceed == false) {
            Logger.get().log(Level.INFO, "Image downloaded from last.fm for artist '" + artist.getName() + "' failed.");
            return;
        }

        Logger.get().log(Level.INFO, "Image downloaded from last.fm for artist '" + artist.getName() + "' to the file '" + artistImageFilePath + "'.");
        externalInformationsListener.artistInformationsReady(item, artistImageFilePath, artist.getWikiSummary(), null);
    }

    private Collection<Track> getLastFMTopTracks(String artistQuery) {
        try {
            return Artist.getTopTracks(artistQuery, Global.LASTFM_API_KEY);
        } catch (Exception e) {
            Logger.get().log(Level.INFO, "Error during LastFM.artist.getTopTracks('" + artistQuery + "'), maybe API out of date or no internet connection.");
        }
        return null;
    }

    public void collectArtistInformations(MusicListItem item, HttpClient client, ImageType type, ExternalInformationsListener externalInformationsListener, HashMap<String, JSonaArtist> cachedArtists) {

        if (item.getArtist() == null) {
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

        // No artist found...exit
        if (artist == null) {
            return;
        }

        String artistImageUrl = getBiggestImageUrl(artist);

        if (StringUtils.isNotBlank(artistImageUrl)) {
            String artistImageFilePath = null;
            if (StringUtils.isNotEmpty(artistImageUrl)) {
                createFolderIfNotExists(Global.ARTIST_IMAGE_FOLDER);
                artistImageFilePath = Global.ARTIST_IMAGE_FOLDER + File.separator + Base64.getEncoder().encodeToString(artist.getName().getBytes());
                downloadAndShowArtistInformation(item, artist, artistImageUrl, artistImageFilePath, client, externalInformationsListener);
            }

            // Top Tracks
            Collection<Track> artistTopTracks = getLastFMTopTracks(artist.getName());
            if (artistTopTracks != null && artistTopTracks.size() > 0) {
                externalInformationsListener.artistInformationsReady(item, artistImageFilePath, artist.getWikiSummary(), artistTopTracks);
            }

            JSonaArtist jSonaArtist = new JSonaArtist();
            jSonaArtist.setArtist(artist);
            jSonaArtist.setImageFilesystemPath(artistImageFilePath);
            jSonaArtist.setTopTracks(artistTopTracks);

            // Save cache to file
            cachedArtists.put(item.getArtist(), jSonaArtist);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String artistsJson = gson.toJson(cachedArtists);
            try {
                FileWriter writer = new FileWriter(new File(Global.ARTISTS_JSON));
                writer.write(artistsJson);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createFolderIfNotExists(String folder) {
        File imgFolder = new File(folder);
        if (!imgFolder.exists()) {
            imgFolder.mkdir();
        }
    }

    /**
     * Check the similarity of all over given artists and the base artist name.
     * (JaroWinklerStrategy)
     *
     * @param name
     * @param artists
     * @return The artist with the highest similarity score.
     */
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
        if (foundArtist.getName().trim().toLowerCase().equals(artistQuery.trim().toLowerCase())) {
            Logger.get().log(Level.INFO, "LastFM.artist.getInfo('" + artistQuery + "')" + ": '" + foundArtist.getName() + "'.");
            return foundArtist;
        } else {
            Logger.get().log(Level.INFO, "LastFM.artist.getInfo('" + artistQuery + "')" + ": No match found.");
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

            Logger.get().log(Level.INFO, "LastFM.artist.search('" + artistQuery + "')" + ": Most similar artist '" + mostSimilarArtist.getName() + "'.");

            Artist foundArtist = Artist.getInfo(mostSimilarArtist.getName(), Global.LASTFM_API_KEY);

            if (foundArtist == null) {
                return null;
            }

            Logger.get().log(Level.INFO, "LastFM.artist.getInfo('" + mostSimilarArtist.getName() + "')" + ": '" + foundArtist.getName() + "'.");

            return foundArtist;
        } catch (Exception e) {
            Logger.get().log(Level.INFO, "Error during LastFM.artist.search('" + artistQuery + "'), maybe API out of date or no internet connection.");
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
        Logger.get().log(Level.INFO, "LastFM.artist.getInfo('" + searchResult.getMbid() + "')" + ": '" + foundArtist.getName() + "'.");

        return foundArtist;
    }

    private Artist searchArtistWithLastFMCorrection(String artistQuery, HttpClient client) {
        Artist foundArtist = Artist.getCorrection(artistQuery, Global.LASTFM_API_KEY);

        if (foundArtist == null) {
            Logger.get().log(Level.INFO, "No artist found on last.fm and musicbrainz.org for '" + artistQuery + "'.");
            return null;
        }

        Logger.get().log(Level.INFO, "LastFM.artist.getCorrection('" + artistQuery + "'): " + foundArtist.getName());

        return foundArtist;
    }


    /**
     * Find the biggest image.
     *
     * @param artist
     * @return Biggest image url
     */
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