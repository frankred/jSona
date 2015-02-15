package de.roth.jsona.information;

import de.umass.lastfm.Artist;
import de.umass.lastfm.Track;

import java.util.Collection;
import java.util.Date;

/**
 * Artist abstraction class for API calls. Holds the information, image path from the
 * information and the top tracks of the information.
 *
 * @author Frank Roth
 */
public class ArtistCacheInformation {

    private Artist artist;
    private Collection<Track> topTracks;
    private String imagePath;
    private Date date;

    public ArtistCacheInformation() {
        this.date = new Date();
    }

    public ArtistCacheInformation(String imagePath) {
        this(null, imagePath);
    }

    public ArtistCacheInformation(Artist artist, String imagePath) {
        this(artist, imagePath, null);
    }

    public ArtistCacheInformation(Artist artist, String imagePath, Collection<Track> topTracks) {
        this.artist = artist;
        this.imagePath = imagePath;
        this.topTracks = topTracks;
        this.date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Collection<Track> getTopTracks() {
        return topTracks;
    }
}