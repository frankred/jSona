package de.roth.jsona;

import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;
import de.roth.jsona.config.Validator;
import de.roth.jsona.logic.LogicManagerFX;
import de.roth.jsona.util.Logger;
import de.roth.jsona.view.ViewManagerFX;
import de.roth.jsona.view.util.DialogUtil;
import de.roth.jsona.vlc.VLCUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

/**
 * This is where all begins...
 *
 * @author Frank Roth
 */
public class MainFX extends Application {

    private Stage stage;
    public static final String VERSION = "1.0.6";

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;

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
        Config.load(Global.CONFIG);

        // Language
        Locale.setDefault(Locale.ENGLISH);
    }

    public void checks() throws IOException {

        // If its windows then the vlc path is required
        if (VLCUtils.vlcPathRequired()) {
            if (Config.getInstance().PATH_TO_VLC == null) {
                Logger.get().info("PATH_TO_VLC in '" + Global.CONFIG + "' was not defined");

                searchVLCPath();
                return;
            }

            if (Validator.isInvalidAbsolutePath(Config.getInstance().PATH_TO_VLC)) {
                Logger.get().info("PATH_TO_VLC '" + Config.getInstance().PATH_TO_VLC + "' is invalid");

                searchVLCPath();
                return;
            }

            System.setProperty("jna.library.path", Config.getInstance().PATH_TO_VLC);
        }
    }

    private void searchVLCPath() {
        Logger.get().info("Try to automatically find the VLC path");

        if (VLCUtils.autoSetupVLCPath()) {
            Logger.get().info("VLC path found");
        } else {
            Logger.get().warning("VLC not path found");

            String message = "VLC path was not found! \n \nPlease setup up 'PATH_TO_VLC' in " + new File(Global.CONFIG).getAbsolutePath();
            Config.getInstance().PATH_TO_VLC = "";
            Config.getInstance().toFile(Global.CONFIG);
            DialogUtil.showErrorDialog(this.stage, "VLC path was not found", message);
            System.exit(0);
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