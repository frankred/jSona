package de.roth.jsona;

import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;
import de.roth.jsona.config.Validator;
import de.roth.jsona.javafx.ViewManagerFX;
import de.roth.jsona.logic.LogicManagerFX;
import de.roth.jsona.theme.ThemeUtils;
import de.roth.jsona.util.Logger;
import de.roth.jsona.vlc.VLCUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;

import java.util.Locale;
import java.util.logging.Level;

/**
 * This is where all begins...
 *
 * @author Frank Roth
 */
public class MainFX extends Application {

    public static final String VERSION = "1.0.6";

    @Override
    public void start(final Stage stage) throws Exception {
        init();
        checks();

        // JavaFX 2.0 default theme, it's much faster then the JavaFX 8.0 default theme
        System.setProperty("javafx.userAgentStylesheetUrl", "caspian");

        // Logo
        Logger.get().info("Starting jSona..." + System.lineSeparator() + getLogo(VERSION));

        // Create Logic
        LogicManagerFX logic = new LogicManagerFX();

        // Create view
        ViewManagerFX.getInstance().init(stage, logic);

        // Go for it...
        logic.start();
    }

    public static void main(String[] args) {
        launch(args);
    }


    public void init() {
        // Logging
        BasicConfigurator.configure();
        Logger.get().setLevel(Level.ALL);

        // Configuration
        Config.load(Global.CONFIG_JSON);

        // Language
        Locale.setDefault(Locale.ENGLISH);
    }

    public void checks() {

        // Check if theme was found, otherwise take default one
        Config.getInstance().THEME = ThemeUtils.themeFromConfigOrFallback();

        // If its windows then the vlc path is required
        if (VLCUtils.vlcPathRequired()) {
            if (Config.getInstance().PATH_TO_VLCJ == null) {
                Logger.get().info("PATH_TO_VLCJ in '" + Global.CONFIG_JSON + "' was not defined");

                Logger.get().info("Try to find VLC path");

                VLCUtils.autoSetupVLCPath();
                return;
            }

            if (Validator.isInvalidAbsolutePath(Config.getInstance().PATH_TO_VLCJ)) {
                Logger.get().info("PATH_TO_VLCJ '" + Config.getInstance().PATH_TO_VLCJ + "' is invalid");

                Logger.get().info("Try to find VLC path");
                VLCUtils.autoSetupVLCPath();

                return;
            }

            System.setProperty("jna.library.path", Config.getInstance().PATH_TO_VLCJ);
        }
    }


    public static String getLogo(String version) {
        return System.lineSeparator() + System.lineSeparator() +
                "   _  _____                   " + System.lineSeparator() +
                "  (_)/ ____|                  " + System.lineSeparator() +
                "   _| (___   ___  _ __   __ _ " + System.lineSeparator() +
                "  | |\\___ \\ / _ \\| '_ \\ / _` |" + System.lineSeparator() +
                "  | |____) | (_) | | | | (_| |" + System.lineSeparator() +
                "  | |_____/ \\___/|_| |_|\\__,_|" + System.lineSeparator() +
                " _/ |                   " + System.lineSeparator() +
                "|__/    " + version + "           " + System.lineSeparator() + System.lineSeparator() +
                "vlcj version: 3.0.1" + System.lineSeparator() +
                "lucene version: 4.7.0" + System.lineSeparator() +
                "java version: " + System.getProperty("java.version") +
                " (" + System.getProperty("os.arch") + ")" + System.lineSeparator() +
                "javafx: " + com.sun.javafx.runtime.VersionInfo.getRuntimeVersion() +
                System.lineSeparator();
    }
}