package de.roth.jsona.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javafx.concurrent.Task;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.ScoreDoc;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import de.roth.jsona.artist.JSonaArtist;
import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;
import de.roth.jsona.database.DataManager;
import de.roth.jsona.database.LuceneManager;
import de.roth.jsona.external.ExternalInformationsListener;
import de.roth.jsona.external.ExternalInformationsThread;
import de.roth.jsona.file.FileScanner;
import de.roth.jsona.file.FileScannerListener;
import de.roth.jsona.file.FileScannerTask;
import de.roth.jsona.file.FileTaggerListener;
import de.roth.jsona.folderwatch.DirWatcher;
import de.roth.jsona.folderwatch.WatchDirListener;
import de.roth.jsona.http.ImageType;
import de.roth.jsona.javafx.ViewManagerFX;
import de.roth.jsona.keyevent.HotkeyConfig;
import de.roth.jsona.mediaplayer.MediaPlayerManager;
import de.roth.jsona.mediaplayer.PlayBackMode;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.model.MusicListItem.Status;
import de.roth.jsona.model.PlayList;
import de.roth.jsona.tag.detection.DetectorRulesManager;
import de.roth.jsona.tag.detection.FieldResult;
import de.roth.jsona.util.Logger;
import de.roth.jsona.util.SerializeManager;
import de.roth.jsona.util.TimeFormatter;

/**
 * Core class of the jSona, where everything comes together. This class
 * implements the main logic of the application
 * 
 * @author Frank Roth
 * 
 */
public class LogicManagerFX implements LogicInterfaceFX, MediaPlayerEventListener, FileScannerListener, FileTaggerListener, WatchDirListener, ExternalInformationsListener, HotKeyListener {

	// Model
	private ArrayList<MusicListItem> newList;
	private ArrayList<PlayList> playLists;

	// Util
	private HttpClient httpClient;
	private DirWatcher folderWatcher;
	private AtomicInteger atomicInt;

	// Media
	private MediaPlayerManager mediaPlayerManager;
	private BlockingQueue<Runnable> importTaggingWorksQueue;
	private ThreadPoolExecutor importTaggingExecutor;

	// Hotkeys
	private Provider hotkeysProvider;

	// Caches
	private HashMap<String, JSonaArtist> artistsCache;

	// Tmp
	private int folderTaggedAmount;
	private int searchResultCounter;

	public LogicManagerFX() {
		// Model
		this.newList = new ArrayList<MusicListItem>();

		// Util
		MultiThreadedHttpConnectionManager mgr = new MultiThreadedHttpConnectionManager();
		mgr.getParams().setDefaultMaxConnectionsPerHost(4);
		this.httpClient = new HttpClient(mgr);
		this.atomicInt = new AtomicInteger();
		this.folderWatcher = new DirWatcher();

		// Media
		this.mediaPlayerManager = new MediaPlayerManager();
		this.mediaPlayerManager.addActionListener(this);
		this.importTaggingWorksQueue = new ArrayBlockingQueue<Runnable>(128);
		this.importTaggingExecutor = new ThreadPoolExecutor(1, 1, Integer.MAX_VALUE, TimeUnit.SECONDS, importTaggingWorksQueue);
		this.importTaggingExecutor.allowCoreThreadTimeOut(false);

		// Hotkeys
		this.hotkeysProvider = Provider.getCurrentProvider(false);
		this.hotkeysProvider.reset();

		// Caches
		this.artistsCache = new HashMap<String, JSonaArtist>();

		// Tmp
		this.folderTaggedAmount = 0;
		this.searchResultCounter = 0;
	}

	/**
	 * Start jSona, scan the folder, start watching the folder for changes and
	 * loading the playlists.
	 */
	@SuppressWarnings("unchecked")
	public void start() {
		// Init
		ViewManagerFX.getInstance().getController().init(this, Config.getInstance().THEME);
		ViewManagerFX.getInstance().getController().setVolume(Config.getInstance().VOLUME);
		ViewManagerFX.getInstance().getController().setPlaybackMode(Config.getInstance().PLAYBACK_MODE);
		checkInitFolder();

		// Register global hotkeys
		for (HotkeyConfig c : Config.getInstance().HOTKEYS) {
			this.hotkeysProvider.register(c.getKeyStroke(), this);
		}

		// Music folders
		ArrayList<File> folders = Config.asFileArrayList(Config.getInstance().FOLDERS);

		// Create view music folders
		for (int i = folders.size() - 1; i >= 0; i--) {
			ViewManagerFX.getInstance().getController().createLoadingMusicFolder(folders.get(i).getAbsolutePath(), folders.get(i).getAbsolutePath(), 0);
		}

		this.folderTaggedAmount = 0;
		for (File f : folders) {
			// add cache
			Logger.get().log(Level.INFO, "Folder '" + f.getAbsolutePath() + "' declared.");

			// watch folder changes
			try {
				Logger.get().log(Level.INFO, "Start watching '" + f.getAbsolutePath() + "'.");
				this.folderWatcher.watch(f, this);
			} catch (IOException e) {
				e.printStackTrace();
			}

			ViewManagerFX.getInstance().getController().updateMusicFolderLoading(-1, 0, null, f.getAbsolutePath());

			// tag entries
			Task<Void> fileScannerTask = new FileScannerTask(f, 0, this, this, f.getAbsolutePath());
			importTaggingExecutor.execute(fileScannerTask);
		}

		// loading artists informations
		File artistsJson = new File(Global.ARTISTS_JSON);
		if (artistsJson.exists()) {
			Gson gson = new Gson();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(artistsJson)));
				this.artistsCache = gson.fromJson(reader, new TypeToken<HashMap<String, JSonaArtist>>() {
				}.getType());
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} else {
			// no jsona cache file found, create new cache
			this.artistsCache = new HashMap<String, JSonaArtist>();
		}

		// loading playlists
		this.playLists = (ArrayList<PlayList>) SerializeManager.load(Global.PLAYLIST_LIST_DATA);
		int activeIndex = 0;
		if (this.playLists != null) {
			int i = 0;

			// find active playlist
			for (PlayList p : this.playLists) {

				// start from 0 again, every start
				p.setAtomicId("paylist_" + atomicInt.incrementAndGet());

				// was active
				for (MusicListItem item : p.getItems()) {
					if (item.getTmp_status() == Status.SET_PAUSED || item.getTmp_status() == Status.SET_PLAYING) {
						item.setTmp_status(Status.SET_NONE);
						activeIndex = i;
					}
				}
				i++;
			}
		} else {
			// create one default playlist
			PlayList p = new PlayList("paylist_" + atomicInt.incrementAndGet(), Global.DEFAULT_PLAYLIST_NAME);
			this.playLists = new ArrayList<PlayList>(1);
			this.playLists.add(p);
		}

		ViewManagerFX.getInstance().getController().initPlaylists(this, this.playLists, activeIndex);
	}

	/**
	 * Close jSona and write config file.
	 */
	public void close() {
		// Closing takes some time, so first stop playing music then hide the
		// view...

		// Stop playing
		if (mediaPlayerManager.getState() == PlayerState.PLAYING) {
			mediaPlayerManager.pause();
		}

		// Hide view
		ViewManagerFX.getInstance().getController().hide();

		// Save cache
		DataManager.getInstance().commit();

		// Stop hotkeys
		this.hotkeysProvider.reset();
		this.hotkeysProvider.stop();

		Logger.get().log(Level.INFO, "Saving current configuration to '" + Global.CONFIG_JSON + "'...");
		Config.getInstance().toFile(Global.CONFIG_JSON);

		Logger.get().log(Level.INFO, "Close jSona now! Bye Bye thank you for using jSona =) - by Frank Roth");
		System.exit(0);
	}

	/**
	 * Check if all needed folders are available.
	 */
	private void checkInitFolder() {
		for (String path : Global.CHECK_FOLDER_EXISTS) {
			File f = new File(path);
			if (!f.exists()) {
				f.mkdir();
			}
		}
	}

	@Override
	public void taggerStart(int filesAmount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void taggerProgress(int current, int total, MusicListItem item, boolean addedToCache, boolean addedToLucene) {
		// only log every 'SCANNER_AND_TAGGER_LOGGING_GRANULARITY'-th file,
		// because logging of many files is very time expensive
		if (current % Config.getInstance().SCANNER_AND_TAGGER_LOGGING_GRANULARITY == 0 || total == current) {
			if (addedToCache && addedToLucene) {
				Logger.get().log(Level.FINE, "+cache +lucene '" + item.getFile().getAbsolutePath() + "'.");
				return;
			}

			if (addedToLucene) {
				Logger.get().log(Level.FINE, "+lucene '" + item.getFile().getAbsolutePath() + "'.");
			}
		}

		// Update view
		ViewManagerFX.getInstance().getController().updateMusicFolderLoading(current, total, item, item.getRootFolder().getAbsolutePath());
	}

	/**
	 * A music folder in the configuration file was completly tagged. Show the
	 * results on the UI
	 */
	@Override
	public synchronized void taggingFinished(File rootFile, boolean somethingChanged, ArrayList<MusicListItem> items, ArrayList<MusicListItem> recentlyAddedItems) {
		// Show folder
		ViewManagerFX.getInstance().getController().setMusicFolder(this, rootFile.getAbsolutePath(), rootFile.getAbsolutePath(), 0, items);

		// If all are new, dont add them to the "new" tab
		if (items.size() != recentlyAddedItems.size()) {
			// Add new
			this.newList.addAll(recentlyAddedItems);
		}

		// All folders tagged?
		this.folderTaggedAmount++;
		if (this.folderTaggedAmount == Config.getInstance().FOLDERS.size()) {
			Logger.get().log(Level.INFO, "Indexing and tagging done...");
			DataManager.getInstance().cleanup();
			ViewManagerFX.getInstance().getController().createMusicFolder(this, Global.NEW_FOLDER_NAME, Global.NEW_FOLDER_NAME, Config.getInstance().FOLDERS.size(), this.newList);
		}
	}

	@Override
	public void scannerStart() {
	}

	@Override
	public void scannerRootFileRead(File f, int fileNumber) {
		Logger.get().log(Level.INFO, "Folder '" + f.getAbsolutePath() + "' completly scanned.");
	}

	private int scannerFileCounter = 0;

	@Override
	public void scannerFileRead(File f) {
		if (scannerFileCounter % Config.getInstance().SCANNER_AND_TAGGER_LOGGING_GRANULARITY == 0) {
			Logger.get().log(Level.FINE, "File '" + f.getAbsolutePath() + "' found.");
		}
		++scannerFileCounter;
	}

	@Override
	public void scannerFinished(String target, LinkedList<File> allFiles) {
		Logger.get().log(Level.FINE, "File '" + allFiles.getLast().getAbsolutePath() + "' found.");
	}

	@Override
	public void timeChanged(MediaPlayer mediaPlayer, long newTimeInMs) {
		ViewManagerFX.getInstance().getController().setCurrentDuration(newTimeInMs);
	}

	@Override
	public void lengthChanged(MediaPlayer mediaPlayer, long newLengthInMs) {
		if (this.mediaPlayerManager.getItem().getDuration() == null || this.mediaPlayerManager.getItem().getDuration().equals("")) {
			this.mediaPlayerManager.getItem().setDuration(TimeFormatter.formatMilliseconds(newLengthInMs));
		}
		ViewManagerFX.getInstance().getController().setCurrentTotalDuration(newLengthInMs);
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
		ViewManagerFX.getInstance().getController().next(this);
	}

	@Override
	public void backward(MediaPlayer mediaPlayer) {
	}

	@Override
	public void endOfSubItems(MediaPlayer mediaPlayer) {
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
	}

	@Override
	public void forward(MediaPlayer mediaPlayer) {
	}

	@Override
	public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {

	}

	@Override
	public void mediaFreed(MediaPlayer mediaPlayer) {
	}

	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
	}

	@Override
	public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
	}

	@Override
	public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
	}

	@Override
	public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
	}

	@Override
	public void newMedia(MediaPlayer mediaPlayer) {

	}

	@Override
	public void opening(MediaPlayer mediaPlayer) {
	}

	@Override
	public void pausableChanged(MediaPlayer mediaPlayer, int newSeekable) {
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {

	}

	@Override
	public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
	}

	@Override
	public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
	}

	@Override
	public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
	}

	@Override
	public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
	}

	@Override
	public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
	}

	@Override
	public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
	}

	@Override
	public void videoOutput(MediaPlayer arg0, int arg1) {
	}

	@Override
	public void buffering(MediaPlayer arg0, float arg1) {

	}

	@Override
	public void mediaChanged(MediaPlayer arg0, libvlc_media_t arg1, String arg2) {

	}

	@Override
	public void fileCreated(Path pathCreated, Path pathToWatch) {
		File f = pathCreated.toFile();

		if (f.isDirectory()) {
			// Do nothing
			return;
		}

		// Find root folder
		ArrayList<File> folders = Config.asFileArrayList(Config.getInstance().FOLDERS);
		File rootFolder = null;
		for (File dir : folders) {
			if (f.getAbsolutePath().startsWith(dir.getAbsolutePath())) {
				rootFolder = dir;
			}
		}

		// Add to cache and lucene
		if (!FileScanner.isValid(f.getName())) {
			return;
		}

		MusicListItem item = DataManager.getInstance().add(f, pathToWatch.toFile());
		DataManager.getInstance().commit();

		// Add to folder tab
		ViewManagerFX.getInstance().getController().addMusicListItem(rootFolder.getAbsolutePath(), rootFolder.getAbsolutePath(), item);
	}

	@Override
	public void fileDeleted(Path pathDeleted, Path pathToWatch) {

		// Read from cache
		MusicListItem item = DataManager.getInstance().get(pathDeleted.toFile());

		if (item == null) {
			return;
		}

		// Remove item from view
		ViewManagerFX.getInstance().getController().removeItem(item);

		// Remove item from db
		DataManager.getInstance().delete(item);
		LuceneManager.getInstance().commit();
	}

	@Override
	public void fileModified(Path pathModified, Path pathToWatch) {
	}

	@Override
	public void event_ui_exit() {
	}

	@Override
	public void event_ui_hide() {
	}

	@Override
	public void event_player_play_pause() {
		if (mediaPlayerManager.getState() == PlayerState.PAUSED) {
			Logger.get().log(Level.INFO, "Player play now.");
			mediaPlayerManager.play();
			ViewManagerFX.getInstance().getController().setPlayerState(PlayerState.PLAYING);
		} else if (mediaPlayerManager.getState() == PlayerState.PLAYING) {
			Logger.get().log(Level.INFO, "Player pause now.");
			mediaPlayerManager.pause();
			ViewManagerFX.getInstance().getController().setPlayerState(PlayerState.PAUSED);
		}
	}

	@Override
	public void event_player_next(MusicListItem item, MusicListItem nextItem) {
		Logger.get().log(Level.INFO, "Play next file '" + nextItem.getFile().getAbsolutePath() + "'.");
		this.mediaPlayerManager.play(nextItem);
		ViewManagerFX.getInstance().getController().setPlayerState(PlayerState.PLAYING);
		loadExternalInformation(nextItem);
	}

	@Override
	public void event_player_previous(MusicListItem item, MusicListItem prevItem) {
		Logger.get().log(Level.INFO, "Play previous file '" + prevItem.getFile().getAbsolutePath() + "'.");
		this.mediaPlayerManager.play(prevItem);
		ViewManagerFX.getInstance().getController().setPlayerState(PlayerState.PLAYING);
		loadExternalInformation(prevItem);
	}

	private void loadExternalInformation(MusicListItem item) {
		// if no id3 tagged on this file!
		if (item.getArtist() == null || item.getTitle() == null) {
			ArrayList<FieldResult> results = DetectorRulesManager.getInstance().detect(item.getRootFolder(), item.getFile());

			// if there are any solutions for filepath tagging
			if (results.size() > 0) {
				StringBuffer buffer = new StringBuffer();
				for (FieldResult f : results) {
					switch (f.getField()) {
					case ARTIST:
						item.setArtist(f.getResult().trim());
						break;
					case TITLE:
						item.setTitle(f.getResult().trim());
						break;
					case ALBUM:
						item.setAlbum(f.getResult().trim());
						break;
					case TRACK_NO:
						item.setTrackNo(f.getResult().trim());
						break;
					default:
						break;
					}
					buffer.append(f.toString());
				}
				Logger.get().log(Level.INFO, "Filepath detection results '" + buffer.toString() + "'.");
				DataManager.getInstance().updateCache(item);
				DataManager.getInstance().commit();
			}
		}

		// Load artist informations from artist cache
		JSonaArtist artist = this.artistsCache.get(item.getArtist());

		// Show informations immediately
		ViewManagerFX.getInstance().getController().showInformations(null, item);

		// Load external informations for this artist
		if (artist == null) {
			// load external informations
			new Thread(new ExternalInformationsThread(httpClient, item, ImageType.ARTIST, this, artistsCache)).start();
		} else {
			// check if image still exists
			File f = new File(artist.getImageFilesystemPath());
			if (f.exists()) {
				ViewManagerFX.getInstance().getController().showInformations(artist, item);
			} else {
				new Thread(new ExternalInformationsThread(httpClient, item, ImageType.ARTIST, this, artistsCache)).start();
			}
		}
	}

	@Override
	public void event_playbackmode_normal() {
		Config.getInstance().PLAYBACK_MODE = PlayBackMode.NORMAL;
		Logger.get().log(Level.INFO, "Playback mode set to normal.");
	}

	@Override
	public void event_playbackmode_shuffle() {
		Config.getInstance().PLAYBACK_MODE = PlayBackMode.SHUFFLE;
		Logger.get().log(Level.INFO, "Playback mode set to random.");
	}

	@Override
	public void event_search_music(final String query) {
		this.searchResultCounter++;

		if (query.length() < 2) {
			ViewManagerFX.getInstance().getController().showSearchResults(null, searchResultCounter);
			return;
		}

		final int counter = this.searchResultCounter;

		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					Logger.get().log(Level.INFO, "Search for '" + QueryParser.escape(query) + "'.");

					ArrayList<MusicListItem> result = null;

					if (!query.equals("")) {
						ScoreDoc[] hits = LuceneManager.getInstance().search(query.trim());

						if (hits != null) {
							result = new ArrayList<MusicListItem>(hits.length);

							for (ScoreDoc s : hits) {
								try {
									Document doc = LuceneManager.getInstance().getSearcher().doc(s.doc);
									if (doc != null) {
										// If search list is selected and item
										// is played then in the music tab the
										// item always gets a played symbol,
										// to avoid this side-effect you could
										// also use clone the returned
										// MusicListItem.
										MusicListItem resultItem = DataManager.getInstance().get(doc.getField("file").stringValue());
										if (resultItem == null) {
											continue;
										}

										result.add(resultItem);
									}
								} catch (CorruptIndexException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
					ViewManagerFX.getInstance().getController().showSearchResults(result, counter);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		(new Thread(r)).start();
	}

	@Override
	public void event_playlist_namechanged(String atomicId, String oldname, String newname) {
		PlayList playlist = findPlayListById(atomicId);
		if (playlist != null) {
			Logger.get().log(Level.INFO, "Playlist '" + oldname + "' renamed to '" + newname + "'.");
			playlist.setName(newname);
			final ArrayList<PlayList> p = this.playLists;
			(new Thread(new Runnable() {
				public void run() {
					SerializeManager.save(Global.PLAYLIST_LIST_DATA, p);
				}
			})).start();
		}
	}

	private PlayList findPlayListById(String atomicId) {
		for (PlayList p : this.playLists) {
			if (p.getAtomicId().equals(atomicId)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public void event_playlist_changed(String atomicId, List<MusicListItem> items) {
		Logger.get().log(Level.INFO, "Saving playlists to '" + Global.PLAYLIST_LIST_DATA + "'.");
		final ArrayList<PlayList> p = this.playLists;
		(new Thread(new Runnable() {
			public void run() {
				SerializeManager.save(Global.PLAYLIST_LIST_DATA, p);
			}
		})).start();

	}

	@Override
	public void event_player_volume(int newValue, int oldValue) {
		mediaPlayerManager.setVolume(newValue);
		Config.getInstance().VOLUME = newValue;
	}

	@Override
	public void artistInformationsReady(MusicListItem item, JSonaArtist artist) {
		// download is ready but is the song still the same?
		if (this.mediaPlayerManager.getItem() == item) {
			ViewManagerFX.getInstance().getController().showInformations(artist, item);
		}
	}

	@Override
	public void event_player_play_skipto(double value) {
		this.mediaPlayerManager.setTime((int) value);
	}

	@Override
	public void event_playlist_new() {
		Logger.get().log(Level.INFO, "Add new playlist.");
		String atomicId = "paylist_" + atomicInt.incrementAndGet();
		PlayList p = new PlayList(atomicId, Global.DEFAULT_PLAYLIST_NAME);
		this.playLists.add(p);
		ViewManagerFX.getInstance().getController().newPlaylist(this, p);
		this.event_playlist_changed(p.getAtomicId(), p.getItems());
	}

	@Override
	public void event_playlist_remove(String atomicId) {
		PlayList p = findPlayListById(atomicId);
		Logger.get().log(Level.INFO, "Remove playlist with the name '" + p.getName() + "' including " + p.getItems().size() + " items.");
		this.playLists.remove(p);
		this.event_playlist_changed(p.getAtomicId(), p.getItems());
	}

	@Override
	public void event_play_url(String url) {

	}

	@Override
	public void onHotKey(HotKey hotKey) {
		for (HotkeyConfig c : Config.getInstance().HOTKEYS) {
			// Check hotkey and modifiers
			if (c.getKey() == hotKey.keyStroke.getKeyCode() && c.getAllModifiers() == hotKey.keyStroke.getModifiers()) {

				switch (c.getApplicationEvent()) {
				case PLAYER_VOLUME_UP:
					this.action_player_volume_up();
					break;
				case PLAYER_VOLUME_DOWN:
					this.action_player_volume_down();
					break;
				case PLAYER_PLAY_PAUSE:
					this.event_player_play_pause();
					break;
				case VIEW_HIDE_SHOW:
					this.action_toggle_view();
					break;
				case PLAYER_NEXT:
					ViewManagerFX.getInstance().getController().next(this);
					break;
				case PLAYER_PREVIOUS:
					ViewManagerFX.getInstance().getController().prev(this);
					break;
				case PLAYER_TIME_UP:
					long newTime_up = this.mediaPlayerManager.getTime() + Config.getInstance().DURATION_ARROW_KEYS_SKIP_TIME * 1000;
					if (newTime_up > this.mediaPlayerManager.getLength()) {
						newTime_up = this.mediaPlayerManager.getLength();
					}
					event_player_play_skipto(newTime_up);
					break;
				case PLAYER_TIME_DOWN:
					long newTime_down = this.mediaPlayerManager.getTime() - Config.getInstance().DURATION_ARROW_KEYS_SKIP_TIME * 1000;
					if (newTime_down < 0) {
						newTime_down = 0;
					}
					event_player_play_skipto(newTime_down);
					break;
				default:
					break;
				}
			}
		}
	}

	public void action_player_volume_up(int stepSize) {
		Config.getInstance().VOLUME += stepSize;
		if (Config.getInstance().VOLUME >= 100) {
			Config.getInstance().VOLUME = 100;
		} else if (Config.getInstance().VOLUME < 0) {
			Config.getInstance().VOLUME = 0;
		}
		Logger.get().log(Level.INFO, "Set volume to '" + Config.getInstance().VOLUME + "'.");
		ViewManagerFX.getInstance().getController().setVolume(Config.getInstance().VOLUME);
		mediaPlayerManager.setVolume(Config.getInstance().VOLUME);
	}

	@Override
	public void action_player_volume_up() {
		action_player_volume_up(Config.getInstance().VOLUME_UP_DOWN_AMOUNT);
	}

	public void action_player_volume_down(int stepSize) {
		Config.getInstance().VOLUME -= stepSize;
		if (Config.getInstance().VOLUME >= 100) {
			Config.getInstance().VOLUME = 100;
		} else if (Config.getInstance().VOLUME < 0) {
			Config.getInstance().VOLUME = 0;
		}
		Logger.get().log(Level.INFO, "Set volume to '" + Config.getInstance().VOLUME + "'.");
		ViewManagerFX.getInstance().getController().setVolume(Config.getInstance().VOLUME);
		mediaPlayerManager.setVolume(Config.getInstance().VOLUME);
	}

	@Override
	public void action_player_volume_down() {
		action_player_volume_down(Config.getInstance().VOLUME_UP_DOWN_AMOUNT);
	}

	@Override
	public void action_volume_mute_unmute() {
		// TODO Auto-generated method stub
	}

	@Override
	public void action_player_play_pause() {
		event_player_play_pause();
	}

	@Override
	public void action_toggle_view() {
		ViewManagerFX.getInstance().getController().toggleView();
	}

	@Override
	public List<String> equalizer_presets() {
		return this.mediaPlayerManager.getEqualizerPresetNames();
	}

	@Override
	public float equalizer_max_gain() {
		return this.mediaPlayerManager.getEqualizerMaxGain();
	}

	@Override
	public float equalizer_min_gain() {
		return this.mediaPlayerManager.getEqualizerMinGain();
	}

	@Override
	public int equalizer_amps_amount() {
		return this.mediaPlayerManager.getEqualizerAmpsAmount();
	}

	@Override
	public float[] equalizer_amps(String preset) {
		return this.mediaPlayerManager.getEqualizerPreset(preset);
	}

	@Override
	public void equalizer_set_amps(float[] amps) {
		StringBuffer ampsOut = new StringBuffer();
		
		ampsOut.append("{");
		
		for(int i = 0; i < amps.length;i++){
			ampsOut.append(i);
			ampsOut.append(": ");
			ampsOut.append(amps[i]);
			ampsOut.append(", ");
		}
		
		ampsOut.append("{");
		
		Logger.get().log(Level.FINE, "Equalizer change to " + ampsOut + ".");
		this.mediaPlayerManager.setEqualizerAmps(amps);
	}

	@Override
	public void equalizer_set_amp(int index, int value) {
		this.mediaPlayerManager.setEqualizerAmp(index, value);
	}
}