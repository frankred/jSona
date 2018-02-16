package de.roth.jsona.vlc.mediaplayer.job;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * Thread to play music oder media file.
 *
 * @author Frank Roth
 */
public class MediaPlayerManagerPlayer implements Runnable {

    private EmbeddedMediaPlayer mediaPlayer;
    private String url;

    /**
     * Play music or media file url in the over given media player.
     *
     * @param mediaPlayer
     * @param url
     */
    public MediaPlayerManagerPlayer(EmbeddedMediaPlayer mediaPlayer, String url) {
        this.mediaPlayer = mediaPlayer;
        this.url = url;
    }

    @Override
    public void run() {
        mediaPlayer.playMedia(url);
    }
}