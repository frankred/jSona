package de.roth.jsona.model;

import de.roth.jsona.api.youtube.YoutubeVideoStreamURL;
import de.roth.jsona.theme.ThemeUtils;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class MusicListItemYoutube extends MusicListItem implements VLCPlayable {

    private String url;
    private String videoTitle;
    private ArrayList<YoutubeVideoStreamURL> streams;
    private YoutubeVideoStreamURL preferedVideoStream;

    public MusicListItemYoutube(String url) {
        super();
        this.url = url;
        this.setIcon(new Image(getClass().getClassLoader().getResourceAsStream(ThemeUtils.getThemePath() + "/icon_youtube.png")));
    }

    @Override
    public String getTextForArtistLabel() {
        return this.videoTitle;
    }

    @Override
    public String getTextForTitleLabel() {
        return this.url;
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public String getMediaURL() {
        if(isProcessing() == true){
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
    }

    public YoutubeVideoStreamURL getPreferedVideoStream() {
        return preferedVideoStream;
    }

    private void setPreferedVideoStream() {
        int highestItag = -1;
        for (YoutubeVideoStreamURL youtubeStream : this.getStreams()) {
            if (youtubeStream.getStreamType() == YoutubeVideoStreamURL.StreamType.VIDEO_AUDIO && youtubeStream.getFormat().equals("MP4") && youtubeStream.getItag() > highestItag) {
                this.preferedVideoStream = youtubeStream;
                highestItag = this.preferedVideoStream.getItag();
            }
        }
    }

    @Override
    public String toString() {
        return url;
    }
}
