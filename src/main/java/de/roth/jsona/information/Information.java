package de.roth.jsona.information;

import java.util.List;

import de.umass.lastfm.Artist;

/**
 * Artist abstraction class for API calls. Holds the information, image path from the
 * information and the top tracks of the information.
 *
 * @author Frank Roth
 */
public class Information {

    private Artist artist;
    private String imagePath;
    private List<Link> links;

    public Information(Artist artist, String imagePath, List<Link> links) {
        this.artist = artist;
        this.imagePath = imagePath;
        this.links = links;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}