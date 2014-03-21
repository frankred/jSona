package de.roth.jsona.external;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

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

/**
 * Abstraction class to fetch external informations (e.g. last.fm or
 * musicbrainz.org).
 * 
 * @author Frank Roth
 * 
 */
public class ExternalInformations {

	private static ExternalInformations instance; // Artist, Track, User
	private StringSimilarityService stringSimilarityService;

	public ExternalInformations() {
		this.stringSimilarityService = new StringSimilarityServiceImpl(new JaroWinklerStrategy());
	}

	public static ExternalInformations getInstance() {
		if (instance == null) {
			instance = new ExternalInformations();
		}
		return (instance);
	}

	/**
	 * Fetch all informations (e.g. artist, image, top tracks) from external
	 * services.
	 * 
	 * @param item
	 *            - For what item or artist do you want to collect these
	 *            informations
	 * @param client
	 *            - HttpClient to perform the requests.
	 * @param type
	 *            - ImageType ? (artist / album)
	 * @param l
	 *            - Listener that is called if everything is fetched
	 * @param cachedArtists
	 *            - cached artists
	 */
	public void collectArtistInformations(MusicListItem item, HttpClient client, ImageType type, ExternalInformationsListener l, HashMap<String, JSonaArtist> cachedArtists) {
		String key = item.getArtist();

		if (key == null) {
			return;
		}

		Artist artist = null;
		String infoKey = null;

		Logger.get().log(Level.INFO, "Collection informations for '" + key + "'.");

		// get music information via last.fm artist name
		artist = Artist.getInfo(infoKey, Global.LASTFM_API_KEY);
		if (artist.getName().trim().toLowerCase().equals(item.getArtist().trim().toLowerCase())) {
			Logger.get().log(Level.INFO, "LastFM.artist.getInfo('" + item.getArtist() + "')" + ": '" + artist.getName() + "'.");
			infoKey = artist.getName();
		} else {
			Logger.get().log(Level.INFO, "LastFM.artist.getInfo('" + item.getArtist() + "')" + ": No match found.");
			artist = null;
		}

		// no artist found
		if (artist == null) {
			// search via last.fm
			Collection<Artist> artists;
			try {
				artists = Artist.search(key, Global.LASTFM_API_KEY);
				artist = getMostSimilarArtist(item.getArtist(), artists);
				Logger.get().log(Level.INFO, "LastFM.artist.search('" + item.getArtist() + "')" + ": Most similar artist '" + artist.getName() + "'.");
				String name = artist.getName();
				artist = Artist.getInfo(name, Global.LASTFM_API_KEY);
				Logger.get().log(Level.INFO, "LastFM.artist.getInfo('" + name + "')" + ": '" + artist.getName() + "'.");

				if (artist != null) {
					infoKey = artist.getName();
				}
			} catch (Exception e) {
				Logger.get().log(Level.INFO, "Error during LastFM.artist.search('" + key + "'), maybe API out of date or no internet connection.");
			}

			if (artist == null) {
				// search via musicbrainz.org
				MusicbrainzSearchResult s = Musicbrainz.findMbidByArtist(key, client);

				// set info
				if (s != null) {
					Logger.get().log(Level.INFO, "Musicbrainz.org findMbidByArtist('" + item.getArtist() + "'): " + s.getMbid());
					infoKey = s.getMbid();
					artist = Artist.getInfo(infoKey, Global.LASTFM_API_KEY);
					Logger.get().log(Level.INFO, "LastFM.artist.getInfo('" + infoKey + "')" + ": '" + artist.getName() + "'.");
				}

				if (s == null) {
					// search via last.fm - correction
					artist = Artist.getCorrection(key, Global.LASTFM_API_KEY);

					// set info
					if (artist != null) {
						Logger.get().log(Level.INFO, "LastFM.artist.getCorrection('" + key + "'): " + artist.getName());
						infoKey = artist.getName();
					} else {
						Logger.get().log(Level.INFO, "No artist found on last.fm and musicbrainz.org for '" + key + "'.");
						return;
					}
				}
			}
		}

		// Check if folder exists
		File imgFolder = new File(Global.ARTIST_IMAGE_FOLDER);
		if (!imgFolder.exists()) {
			imgFolder.mkdir();
		}

		try {
			// Save image
			String savePath = Global.ARTIST_IMAGE_FOLDER + File.separator + artist.getName();
			JSonaArtist jsonaArtist = new JSonaArtist();
			jsonaArtist.setArtist(artist);
			jsonaArtist.setImageFilesystemPath(savePath);

			String url = getBiggestImageUrl(jsonaArtist.getArtist());

			// download image to json.artists
			if (url != null && !url.equals("")) {
				if (HttpClientHelper.downloadFile(url, jsonaArtist.getImageFilesystemPath(), client)) {
					Logger.get().log(Level.INFO, "Image downloaded from last.fm for artist '" + jsonaArtist.getArtist().getName() + "' to the file '" + jsonaArtist.getImageFilesystemPath() + "'.");
					l.artistInformationsReady(item, jsonaArtist);
				} else {
					Logger.get().log(Level.INFO, "Image downloaded from last.fm for artist '" + jsonaArtist.getArtist().getName() + "' failed.");
				}
			}

			// load top tracks
			Collection<Track> topTracks = Artist.getTopTracks(jsonaArtist.getArtist().getName(), Global.LASTFM_API_KEY);
			jsonaArtist.setTopTracks(topTracks);
			l.artistInformationsReady(item, jsonaArtist);

			// Save cache to file
			cachedArtists.put(key, jsonaArtist);
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
		} catch (Exception e) {
			e.printStackTrace();
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
	private Artist getMostSimilarArtist(String name, Collection<Artist> artists) {
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