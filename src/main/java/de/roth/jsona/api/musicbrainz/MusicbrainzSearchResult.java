package de.roth.jsona.api.musicbrainz;

/**
 * Represents an musicbrainz search result.
 *
 * @author 933
 */
public class MusicbrainzSearchResult {

    private int score;
    private String mbid;

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return the mbid
     */
    public String getMbid() {
        return mbid;
    }

    /**
     * @param mbid the mbid to set
     */
    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    @Override
    public String toString() {
        return this.mbid + " => " + this.score;
    }
}
