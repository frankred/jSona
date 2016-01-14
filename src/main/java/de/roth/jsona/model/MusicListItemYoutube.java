package de.roth.jsona.model;

import de.roth.jsona.api.youtube.YoutubeVideoStreamURL;
import de.roth.jsona.information.Link;
import de.roth.jsona.theme.ThemeUtils;
import javafx.scene.image.Image;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicListItemYoutube extends MusicListItem {

    private String url;
    private String title;
    private ArrayList<YoutubeVideoStreamURL> streams;
    private YoutubeVideoStreamURL preferedVideoStream;

    public MusicListItemYoutube(String url) {
        super();
        this.url = url;
        this.setIcon(new Image(getClass().getClassLoader().getResourceAsStream(ThemeUtils.getThemePath() + "/icon_youtube.png")));

        ArrayList<Link> links = new ArrayList<Link>(1);
        links.add(new Link(url, url));
        this.setLinks(links);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.invalidate();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getTextForArtistLabel() {
        return null;
    }

    @Override
    public String getTextForTitleLabel() {
        return this.title;
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public String getMediaURL() {
        if (isProcessing() == true) {
            return null;
        }

        return getPreferedVideoStream().getUrl();
    }

    public ArrayList<YoutubeVideoStreamURL> getStreams() {
        return streams;
    }

    public void setStreams(ArrayList<YoutubeVideoStreamURL> streams) {
        this.streams = streams;
        this.setPreferedVideoStream();
        this.invalidate();
    }

    public YoutubeVideoStreamURL getPreferedVideoStream() {
        return preferedVideoStream;
    }

    private void setPreferedVideoStream() {
        int highestItag = -1;
        for (YoutubeVideoStreamURL youtubeStream : this.getStreams()) {
            if (youtubeStream.getStreamType() == YoutubeVideoStreamURL.StreamType.AUDIO && youtubeStream.getItag() > highestItag) {
                this.preferedVideoStream = youtubeStream;
                highestItag = this.preferedVideoStream.getItag();
            }
        }

        this.setTitle(StringEscapeUtils.unescapeHtml4(this.preferedVideoStream.getTitle().replaceAll(" - Youtube", "")));
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public String getMainImage() {
        return this.getImagePath();
    }

    @Override
    public String getHeading() {
        return this.getTitle();
    }

    @Override
    public String getSubheading() {
        return this.getUrl();
    }

    @Override
    public String getText() {
        return this.getPreferedVideoStream().getFormat() + " " + this.getPreferedVideoStream().getSizeRate() + (this.getPreferedVideoStream().getResolution() != null ? " " + this.getPreferedVideoStream().getResolution() : "");
    }

    @Override
    public List<Link> getSubLinks() {
        return null;
    }
}
