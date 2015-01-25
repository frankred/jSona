package de.roth.jsona.config;

import java.io.File;

/**
 * Global constants class.
 * 
 * @author Frank Roth
 * 
 */
public class Global {

	// Defaults
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");

	// Golden Cut
	public static final double GOLDEN_CUT = 1.618;

	// API KEYS
	public static final String LASTFM_API_KEY = "80fd8a5ed6b945de8782d6466cc674fd";

	// Data
	public static final int SCROLL_VOLOME_ADDITION = 5;
	public static final String PLAYLIST_LIST_DATA = "playlists.data";
	public static final String OPTIONS_HOTKEY = "hotkeys.data";
	public static final int MAX_SEARCH_VALUES_DEFAULT = 1024;
	public static final String CONFIG_JSON = "config.json";

	// Caches
	public static final String CACHE_FOLDER = "cache";
	public static final String FILES_CACHE = CACHE_FOLDER + File.separator + "files.cache";
	public static final String ARTISTS_JSON = CACHE_FOLDER + File.separator + "artists.cache";
	public static final String FOLDER_CACHE = CACHE_FOLDER + File.separator + "folder.cache";
	public static final String IMAGE_FOLDER = CACHE_FOLDER + File.separator + "img";
	public static final String ARTIST_IMAGE_FOLDER = IMAGE_FOLDER + File.separator + "artists";

	// Folders that has to exists
	public static final String[] CHECK_FOLDER_EXISTS = { CACHE_FOLDER, IMAGE_FOLDER, ARTIST_IMAGE_FOLDER };

	public static final int EFFECT_WIDTH = 120;
	public static final int EFFECT_HEIGHT = 90;
	public static final int DOUBLE_CLICK = 2;
	public static final int VOLUME_PERCANTAGE_INT = 100;
	public static final double VOLUME_PERCANTAGE = 100d;

	// Ascii Filter
	public static final String[] ASCII_FILTER = { "þ", "ÿ" };

	// HTTP
	public static final int MAXIMUM_DOWNLOAD_TRIES = 12;

	// View
	public static final int VIEW_NOTIFICATION_LABEL_MARGIN_LEFT = 2;
	public static final double VIEW_SPLITPANE_DIVIDER = 0.6;
	public static final double RESIZE_FACTOR_MUSIC_ICON_LIST_ITEM = 0.7;
	public static final String DEFAULT_PLAYLIST_NAME = "Playlist";
	public static final String NEW_FOLDER_NAME = "New";

	// Text
	public static final String NOTIFICATION_TEXT_PLAY = "Playing: ";
	public static final String NOTIFICATION_TEXT_STOP = "Stopped: ";
	public static final String NOTIFICATION_TEXT_PAUSED = "Paused: ";
	public static final String NOTIFICATION_TEXT_PROGRESSBAR_TAGGING_PRE = "Tagging (";
	public static final String NOTIFICATION_TEXT_PROGRESSBAR_TAGGING_POST = ")";
	public static final String NOTIFICATION_TEXT_PROGRESSBAR_IMPORT_PRE = "Import (";
	public static final String NOTIFICATION_TEXT_PROGRESSBAR_IMPORT_POST = ")";

	// Playback status
	public static final String PLAYBACK_STATUS_SPACE = " ";
	public static final String PLAYBACK_STATUS_STOPPED = "Stopped";
	public static final String PLAYBACK_STATUS_PLAYING = "Playing:";
	public static final String PLAYBACK_STATUS_PAUSED = "Paused:";
	
	// Equalizer
	public static final String EQUALIZER_ON = "ON";
	public static final String EQUALIZER_OFF = "OFF";

	// SysTray
	public static final String SYSTRAY_TOOLTIP = "jSona";
	public static final String SYSTRAY_ITEM_SHOW_HIDE = "Show/Hide";
	public static final String SYSTRAY_ITEM_EXIT = "Exit";
	public static final String SYSTRAY_ITEM_ABOUT = "About";

	// FileFilter
	public static final String[] IMPORT_FILE_FILTER = { "desktop.ini", "Thumbs.db", " Thumbs.db", ".DS_Store", ".png", ".txt", ".jpg", ".sfv", ".nfo", ".pdf", ".doc", ".docx", ".m3u", ".dll", ".rar", ".zip", ".exe", ".glade", ".css", ".TMP" };

	// Property names
	public static final String PROPERTOES_ARTIST = "Artist";
	public static final String PROPERTOES_TITLE = "Title";
	public static final String PROPERTOES_ALBUM = "Album";
	public static final String PROPERTOES_COMMENT = "Comment";
	public static final String PROPERTOES_LYRICS = "Lyrics";

	// Error Text
	public static final String ERROR_FILE_NOT_FOUND = "File <b>{0}</b> does not exists!";

	// Genre List
	public static final String[] ID3_GENRE_LIST = { "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "Rhythm and Blues", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska",
			"Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz&Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "Alternative Rock", "Bass", "Soul", "Punk", "Space",
			"Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native US",
			"Cabaret", "New Wave", "Psychedelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival",
			"Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass",
			"Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore",
			"Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "Jpop", "Synthpop" };
}
