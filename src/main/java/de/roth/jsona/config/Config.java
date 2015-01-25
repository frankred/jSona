package de.roth.jsona.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import de.roth.jsona.MainFX;
import de.roth.jsona.keyevent.HotkeyConfig;
import de.roth.jsona.mediaplayer.PlayBackMode;
import de.roth.jsona.tag.detection.DetectorRuleConfig;

/**
 * Singleton that represents the configuration of the application with all its
 * attributes.
 *
 * @author Frank Roth
 */
public class Config {

    private static Config instance;

    /**
     * Return a configuration instance
     *
     * @return Config
     */
    public static Config getInstance() {
        if (instance == null) {
            instance = fromDefaults();
        }
        return instance;
    }

    @Expose
    public boolean ALLOW_JSONA_TO_OVERWRITE_ME;

    @Expose
    public String PATH_TO_VLCJ;
    @Expose
    public int MAX_SEARCH_RESULT_AMOUNT;
    @Expose
    public int VOLUME;
    @Expose
    public ArrayList<String> FOLDERS;
    @Expose
    public ArrayList<String> INCLUDE_EXTENSIONS;
    @Expose
    public PlayBackMode PLAYBACK_MODE;
    @Expose
    public String SENT_TO_PATH;
    @Expose
    public int RECENTLY_ADDED_UNITL_TIME_IN_DAYS;
    @Expose
    public String THEME;
    @Expose
    public String TITLE;
    @Expose
    public int HEIGHT;
    @Expose
    public int WIDTH;
    @Expose
    public int MIN_HEIGHT;
    @Expose
    public int MIN_WIDTH;
    @Expose
    public boolean COLORIZE_ITEMS;
    @Expose
    public ArrayList<DetectorRuleConfig> FILEPATH_BASED_MUSIC_INFORMATIONS;
    @Expose
    public int SCANNER_AND_TAGGER_LOGGING_GRANULARITY;
    @Expose
    public ArrayList<HotkeyConfig> HOTKEYS;
    @Expose
    public boolean WINDOW_OS_DECORATION;

    @Expose
    public int VOLUME_UP_DOWN_AMOUNT;
    @Expose
    public int VOLUME_SCROLL_UP_DOWN_AMOUNT;
    @Expose
    public int DURATION_ARROW_KEYS_SKIP_TIME;
    @Expose
    public int DURATION_SCROLL_SKIP_TIME;

    @Expose
    public boolean EQUALIZER_ACTIVE;

    public Config() {
        // default values
        this.TITLE = "jSona " + MainFX.VERSION + " | OpenSource Hell Ya!";
        this.THEME = "themes/grey";
        this.VOLUME = 80;
        this.FOLDERS = new ArrayList<String>();
        this.HOTKEYS = new ArrayList<HotkeyConfig>();
        this.MAX_SEARCH_RESULT_AMOUNT = 512;
        this.PLAYBACK_MODE = PlayBackMode.NORMAL;
        this.RECENTLY_ADDED_UNITL_TIME_IN_DAYS = -7;
        this.MIN_HEIGHT = 400;
        this.MIN_WIDTH = 600;
        this.HEIGHT = 600;
        this.WIDTH = 860;
        this.COLORIZE_ITEMS = true;
        this.SCANNER_AND_TAGGER_LOGGING_GRANULARITY = 1;
        this.VOLUME_UP_DOWN_AMOUNT = 20;
        this.VOLUME_SCROLL_UP_DOWN_AMOUNT = 5;
        this.DURATION_ARROW_KEYS_SKIP_TIME = 10;
        this.DURATION_SCROLL_SKIP_TIME = 10;
        this.EQUALIZER_ACTIVE = false;
        this.WINDOW_OS_DECORATION = false;
        this.ALLOW_JSONA_TO_OVERWRITE_ME = false;
    }

    /**
     * Load the configuration from the over given file
     *
     * @param file for the configuration
     */
    public static void load(File file) {
        instance = fromFile(file);

        // no config file found
        if (instance == null) {
            instance = fromDefaults();
        }
    }

    /**
     * Load the configuration from the over given path
     *
     * @param file for the configuration
     */
    public static void load(String file) {
        load(new File(file));
    }

    /**
     * Load the configuration from the default values, no file required.
     *
     * @return
     */
    private static Config fromDefaults() {
        Config config = new Config();
        return config;
    }

    /**
     * Save the configuration to the over given file path.
     *
     * @param file
     */
    public void toFile(String file) {
        toFile(new File(file));
    }

    /**
     * Save the configuration to the over given file
     *
     * @param file
     */
    public void toFile(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String jsonConfig = gson.toJson(this);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(jsonConfig);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private static Config fromFile(String path) {
        return fromFile(new File(path));
    }

    /**
     * Converts a string array list with file paths in a file array list
     *
     * @param filepaths - String array with file paths
     * @return ArrayList<File> files
     */
    public static ArrayList<File> asFileArrayList(ArrayList<String> filepaths) {
        ArrayList<File> files = new ArrayList<File>();
        for (String f : filepaths) {
            files.add(new File(f));
        }
        return files;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    private static Config fromFile(File configFile) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
            return gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
