package de.roth.jsona.artist;

import java.util.Collection;

import de.umass.lastfm.Artist;
import de.umass.lastfm.Track;

/**
 * Artist abstraction class for API calls. Holds the artist, image path from the
 * artist and the top tracks of the artist.
 * 
 * @author Frank Roth
 * 
 */
public class JSonaArtist {

	private Artist artist;
	private String imageFilesystemPath;
	private Collection<Track> topTracks;

	/**
	 * @return the imageFilesystemPath
	 */
	public String getImageFilesystemPath() {
		return imageFilesystemPath;
	}

	/**
	 * @param imageFilesystemPath
	 *            the imageFilesystemPath to set
	 */
	public void setImageFilesystemPath(String imageFilesystemPath) {
		this.imageFilesystemPath = imageFilesystemPath;
	}

	/**
	 * @return the artist
	 */
	public Artist getArtist() {
		return artist;
	}

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public Collection<Track> getTopTracks() {
		return topTracks;
	}

	public void setTopTracks(Collection<Track> topTracks) {
		this.topTracks = topTracks;
	}
}