package de.roth.jsona.mediaplayer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.AudioOutput;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.ModuleDescription;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;

import de.roth.jsona.config.Config;
import de.roth.jsona.mediaplayer.job.MediaPlayerManagerPlayer;
import de.roth.jsona.model.MusicListItem;

/**
 * MediaPlayerManager controls the vlcj mediaplayer
 * 
 * @author Frank Roth
 * 
 */
public class MediaPlayerManager {

	private MediaPlayerFactory factory;
	private List<AudioOutput> audioOutputs;
	private EmbeddedMediaPlayer mediaPlayer;
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

		// Vlcj bug workarround - volume is accepted only if a media file
		// played, so add a listener wait till empty.wav is play then set volume
		// and remove listener
		this.mediaPlayer.addMediaPlayerEventListener(new VlcjVolumeBugfixListener());

		try {
			this.play(new MusicListItem(new File(getClass().getResource("/de/roth/jsona/sound/empty.wav").toURI()), null));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
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
				executor.execute(new MediaPlayerManagerPlayer(mediaPlayer, new String(item.getFile().getAbsolutePath().getBytes("UTF-8"))));
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

	/**
	 * Set audio output
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

	public PlayerState getState() {
		return state;
	}

	/**
	 * Bugfix media player listener to play the empty.wav file at startup, this
	 * will workaround the volume bug from the vlc core.
	 * 
	 * @author Frank Roth
	 * 
	 */
	public class VlcjVolumeBugfixListener implements uk.co.caprica.vlcj.player.MediaPlayerEventListener {
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
	}
}