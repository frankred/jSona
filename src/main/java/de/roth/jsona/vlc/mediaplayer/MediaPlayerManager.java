package de.roth.jsona.vlc.mediaplayer;

import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;
import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.util.FileUtil;
import de.roth.jsona.vlc.mediaplayer.job.MediaPlayerManagerPlayer;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.*;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MediaPlayerManager controls the vlcj mediaplayer
 *
 * @author Frank Roth
 */
public class MediaPlayerManager {

    private MediaPlayerFactory factory;
    private List<AudioOutput> audioOutputs;
    private EmbeddedMediaPlayer mediaPlayer;
    private Map<String, Equalizer> allEqualizer;
    private List<ModuleDescription> audioFilters;
    private MusicListItem currentItem;
    private BlockingQueue<Runnable> worksQueue;
    private ThreadPoolExecutor executor;

    private PlayerState state;

    public MediaPlayerManager() {
        this.factory = new MediaPlayerFactory("--no-video-title-show");
        this.mediaPlayer = factory.newEmbeddedMediaPlayer();
        this.worksQueue = new ArrayBlockingQueue<Runnable>(64);
        this.executor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, worksQueue);
        this.executor.allowCoreThreadTimeOut(false);
        this.state = PlayerState.PAUSED;
        this.mediaPlayer = factory.newEmbeddedMediaPlayer();

        // Equalizer
        if (this.factory.isEqualizerAvailable()) {
            this.allEqualizer = this.factory.getAllPresetEqualizers();

            if (Config.getInstance().EQUALIZER_ACTIVE) {
                this.mediaPlayer.setEqualizer(this.factory.newEqualizer());
            }
        }

        // Vlcj bug workarround - volume is accepted only if a media file
        // played, so add a listener wait till empty.wav is play then set volume
        // and remove listener
        this.mediaPlayer.addMediaPlayerEventListener(new VlcjVolumeBugfixListener());

        File emptyWav = new File(Global.CACHE_FOLDER + File.separator + "sound/empty.wav");
        FileUtil.copyFile(getClass().getClassLoader().getResource("sound/empty.wav"), emptyWav);
        this.play(new MusicListItem(emptyWav, null, null));
    }

    /**
     * Register media player event listener
     *
     * @param listener
     */
    public void addActionListener(uk.co.caprica.vlcj.player.MediaPlayerEventListener listener) {
        this.mediaPlayer.addMediaPlayerEventListener(listener);
    }

    /**
     * Remove the media player event listener
     *
     * @param listener
     */
    public void removeActionListener(uk.co.caprica.vlcj.player.MediaPlayerEventListener listener) {
        this.mediaPlayer.removeMediaPlayerEventListener(listener);
    }

    /**
     * Plays a music item in a new thread
     *
     * @param item
     */
    public void play(MusicListItem item) {
        currentItem = item;
        if (currentItem != null) {
            try {
                this.state = PlayerState.PLAYING;

                String mediaToPlay = new String();
                if (item.getFile() != null) {
                    mediaToPlay = new String(item.getFile().getAbsolutePath().getBytes("UTF-8"));
                } else if (item.getUrl() != null) {
                    mediaToPlay = item.getUrl();
                }

                executor.execute(new MediaPlayerManagerPlayer(mediaPlayer, mediaToPlay));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void load(MusicListItem item) {
        currentItem = item;
        mediaPlayer.prepareMedia(item.getFile().getAbsolutePath());
        this.state = PlayerState.PAUSED;
    }

    /**
     * Continue playing the item, (when it once was paused)
     */
    public void play() {
        if (currentItem != null) {
            this.state = PlayerState.PLAYING;
            mediaPlayer.play();
        }
    }

    /**
     * Pause the current item.
     */
    public void pause() {
        if (currentItem != null) {
            this.state = PlayerState.PAUSED;
            mediaPlayer.pause();
        }
    }

    /**
     * Set the volume of the media player
     *
     * @param volume
     */
    public void setVolume(int volume) {
        mediaPlayer.setVolume(volume);
    }

    /**
     * Set the current play back time of the media player in ms.
     *
     * @param time
     */
    public void setTime(int time) {
        mediaPlayer.setTime(time);
    }

    public long getTime() {
        return mediaPlayer.getTime();
    }

    public long getLength() {
        return mediaPlayer.getLength();
    }

    /**
     * Set audio output
     *
     * @param audioOutputName
     * @param audioDeviceId
     */
    public void setAudioOutput(String audioOutputName, String audioDeviceId) {
        mediaPlayer.setAudioOutput(audioOutputName);
        if (audioDeviceId != null) {
            mediaPlayer.setAudioOutputDevice(audioOutputName, audioDeviceId);
        }
    }

    public List<AudioOutput> getAudioOutputs() {
        return audioOutputs;
    }

    public List<ModuleDescription> getAudioFilters() {
        return audioFilters;
    }

    /**
     * Get the current item of the media player that is played pause or stopped
     *
     * @return
     */
    public MusicListItem getItem() {
        return currentItem;
    }

    /**
     * Get current state of the media player
     *
     * @return
     */
    public PlayerState getState() {
        return state;
    }

    /**
     * Return all vlc predefined equalizer
     *
     * @return
     */
    public List<String> getEqualizerPresetNames() {
        if (this.factory.isEqualizerAvailable()) {
            return this.factory.getEqualizerPresetNames();
        }
        return null;
    }

    public float getEqualizerMaxGain() {
        return LibVlcConst.MAX_GAIN;
    }

    public float getEqualizerMinGain() {
        return LibVlcConst.MIN_GAIN;
    }

    public int getEqualizerAmpsAmount() {
        if (this.factory.isEqualizerAvailable()) {
            if (this.mediaPlayer.getEqualizer() == null) {
                return this.factory.newEqualizer().getAmps().length;
            }
            return this.mediaPlayer.getEqualizer().getAmps().length;
        }
        return 0;
    }

    public float[] getEqualizerPreset(String name) {
        if (this.factory.isEqualizerAvailable()) {
            if (this.allEqualizer.get(name) == null) {
                // No preset found for that name -> create one
                this.allEqualizer.put(name, this.factory.newEqualizer());
            }
            return this.allEqualizer.get(name).getAmps();
        }
        return null;
    }

    public void setEqualizerAmps(float[] amps) {
        if (this.factory.isEqualizerAvailable()) {
            if (this.mediaPlayer.getEqualizer() == null) {
                this.mediaPlayer.setEqualizer(this.factory.newEqualizer());
            }
            this.mediaPlayer.getEqualizer().setAmps(amps);
        }
    }

    public void setEqualizerAmp(int index, float newAmp) {
        if (this.factory.isEqualizerAvailable()) {
            if (this.mediaPlayer.getEqualizer() != null) {
                this.mediaPlayer.getEqualizer().setAmp(index, newAmp);
            }
        }
    }

    public boolean isEqualizerAvailable() {
        return this.factory.isEqualizerAvailable();
    }

    public void disableEqualizer() {
        this.mediaPlayer.setEqualizer(null);
    }

    /**
     * Bugfix media player listener to play the empty.wav file at startup, this
     * will workaround the volume bug from the vlc core.
     *
     * @author Frank Roth
     */
    private class VlcjVolumeBugfixListener implements uk.co.caprica.vlcj.player.MediaPlayerEventListener {
        public VlcjVolumeBugfixListener() {

        }

        public void backward(MediaPlayer arg0) {
        }

        public void buffering(MediaPlayer arg0, float arg1) {
        }

        public void endOfSubItems(MediaPlayer arg0) {
        }

        public void error(MediaPlayer arg0) {
        }

        /**
         * If empty.wav is player set volume to config volume value an remove
         * the listener.
         */
        public void finished(MediaPlayer arg0) {
            arg0.setVolume(Config.getInstance().VOLUME);
            removeActionListener(this);
        }

        public void forward(MediaPlayer arg0) {
        }

        public void lengthChanged(MediaPlayer arg0, long arg1) {

        }

        public void mediaChanged(MediaPlayer arg0, libvlc_media_t arg1, String arg2) {

        }

        public void mediaDurationChanged(MediaPlayer arg0, long arg1) {

        }

        public void mediaFreed(MediaPlayer arg0) {

        }

        public void mediaMetaChanged(MediaPlayer arg0, int arg1) {

        }

        public void mediaParsedChanged(MediaPlayer arg0, int arg1) {
        }

        public void mediaStateChanged(MediaPlayer arg0, int arg1) {

        }

        @Override
        public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {

        }

        public void mediaSubItemAdded(MediaPlayer arg0, libvlc_media_t arg1) {

        }

        public void newMedia(MediaPlayer arg0) {

        }

        public void opening(MediaPlayer arg0) {

        }

        public void pausableChanged(MediaPlayer arg0, int arg1) {

        }

        public void paused(MediaPlayer arg0) {

        }

        public void playing(MediaPlayer arg0) {

        }

        public void positionChanged(MediaPlayer arg0, float arg1) {

        }

        public void seekableChanged(MediaPlayer arg0, int arg1) {

        }

        public void snapshotTaken(MediaPlayer arg0, String arg1) {

        }

        public void stopped(MediaPlayer arg0) {

        }

        public void subItemFinished(MediaPlayer arg0, int arg1) {

        }

        public void subItemPlayed(MediaPlayer arg0, int arg1) {

        }

        /**
         * Setting the volume to workaround the vlcj bug.
         */
        public void timeChanged(MediaPlayer arg0, long arg1) {
            arg0.setVolume(Config.getInstance().VOLUME);
        }

        public void titleChanged(MediaPlayer arg0, int arg1) {
        }

        public void videoOutput(MediaPlayer arg0, int arg1) {

        }

        @Override
        public void scrambledChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void elementaryStreamAdded(MediaPlayer mediaPlayer, int i, int i1) {

        }

        @Override
        public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int i, int i1) {

        }

        @Override
        public void elementaryStreamSelected(MediaPlayer mediaPlayer, int i, int i1) {

        }

        @Override
        public void corked(MediaPlayer mediaPlayer, boolean b) {

        }

        @Override
        public void muted(MediaPlayer mediaPlayer, boolean b) {

        }

        @Override
        public void volumeChanged(MediaPlayer mediaPlayer, float v) {

        }

        @Override
        public void audioDeviceChanged(MediaPlayer mediaPlayer, String s) {

        }
    }
}