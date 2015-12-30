package de.roth.jsona.config;

import java.io.File;

/**
 * Global constants class.
 *
 * @author Frank Roth
 */
public class Global {

    // API KEYS
    public static final String LASTFM_API_KEY = "80fd8a5ed6b945de8782d6466cc674fd";
    public static final String SOUNDCLOUD_API_CLIENT_ID = "e719e8c19ab08a571c4f85d63e055b5b";
    public static final String SOUNDCLOUD_API_CLIENT_SECRET = "4c9c2fb3044797d32d03e2c2098956ce";

    // Themes
    public static final String THEMES_FOLDER = "themes";

    // Language
    public static final String LANGUAGE_FOLDER = "lang";

    // Data
    public static final String PLAYLIST_LIST_DATA = "playlists.data";
    public static final String CONFIG = "config.json";

    // Caches
    public static final String CACHE_FOLDER = "cache";
    public static final String FILES_CACHE = CACHE_FOLDER + File.separator + "files.cache";
    public static final String ARTISTS_JSON = CACHE_FOLDER + File.separator + "artists.cache";
    public static final String FOLDER_CACHE = CACHE_FOLDER + File.separator + "folder.cache";
    public static final String IMAGE_FOLDER = CACHE_FOLDER + File.separator + "img";
    public static final String ARTIST_IMAGE_FOLDER = IMAGE_FOLDER + File.separator + "artists";

    // Folders that has to exists
    public static final String[] CHECK_FOLDER_EXISTS = {CACHE_FOLDER, IMAGE_FOLDER, ARTIST_IMAGE_FOLDER};

    // View
    public static final String DEFAULT_PLAYLIST_NAME = "Playlist";
    public static final String NEW_FOLDER_NAME = "New";

    // Equalizer
    public static final String EQUALIZER_ON = "ON";
    public static final String EQUALIZER_OFF = "OFF";

    // FileFilter
    public static final String[] IMPORT_FILE_FILTER = {"desktop.ini", "Thumbs.db", " Thumbs.db", ".DS_Store", ".png", ".txt", ".jpg", ".sfv", ".nfo", ".pdf", ".doc", ".docx", ".m3u", ".dll", ".rar", ".zip", ".exe", ".glade", ".css", ".TMP"};

    // Genre List
    public static final String[] ID3_GENRE_LIST = {"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "Rhythm and Blues", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska",
            "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz&Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "Alternative Rock", "Bass", "Soul", "Punk", "Space",
            "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native US",
            "Cabaret", "New Wave", "Psychedelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival",
            "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass",
            "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore",
            "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "Jpop", "Synthpop"};
}
