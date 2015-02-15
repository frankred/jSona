package de.roth.jsona.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import de.roth.jsona.MainFX;
import de.roth.jsona.keyevent.HotkeyConfig;
import de.roth.jsona.tag.detection.DetectorRuleConfig;
import de.roth.jsona.vlc.mediaplayer.PlayBackMode;

import java.io.*;
import java.util.ArrayList;

public class Config {

    private static Config instance;

    public static Config getInstance() {
        return instance;
    }

    @Expose
    public boolean ALLOW_JSONA_TO_OVERWRITE_ME;

    @Expose
    public String PATH_TO_VLC;

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
        this.THEME = "grey";
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

    public static void load(File file) {
        instance = fromFile(file);

        // no config file found
        if (instance == null) {
            instance = new Config();
        }
    }

    public static void load(String file) {
        load(new File(file));
    }

    public void toFile(String file) {
        toFile(new File(file));
    }

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