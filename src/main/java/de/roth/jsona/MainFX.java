package de.roth.jsona;

import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;
import de.roth.jsona.logic.LogicManagerFX;
import de.roth.jsona.util.Logger;
import de.roth.jsona.view.ViewManagerFX;
import javafx.application.Application;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;

public class MainFX extends Application {

    private Stage stage;
    public static final String VERSION = "1.0.7";

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;

        init();
        bindVLC();

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

        // Configuration
        Config.load(Global.CONFIG);

        // Language
        Locale.setDefault(Locale.ENGLISH);
    }

    public void bindVLC() {

        String vlcNative = new String();
        String libvlccore = new String();
        String libvlc = new String();

        if (RuntimeUtil.isWindows()) {
            vlcNative = "vlc-win-32";
            libvlccore = "libvlccore.dll";
            libvlc = "libvlc.dll";
        }

        try {
            File currentFolder = Paths.get("").toFile();
            File vlcTargetDirectory = new File(currentFolder.getAbsolutePath() + System.getProperty("file.separator") + vlcNative);
            if(vlcTargetDirectory.exists()){
                return;
            }

            URL vlcNativeZipInJar = getClass().getClassLoader().getResource("vlc/" + vlcNative + ".zip");
            File vlcNativeZip = new File(currentFolder.getAbsolutePath() + System.getProperty("file.separator") + vlcNative + ".zip");

            // copy to current directory
            FileUtils.copyURLToFile(vlcNativeZipInJar, vlcNativeZip);

            // unzip to vlc-natives
            unzip(vlcNativeZip, currentFolder);

            // load vlc
            System.load(vlcTargetDirectory.getAbsolutePath() + System.getProperty("file.separator") + libvlccore);
            System.load(vlcTargetDirectory.getAbsolutePath() + System.getProperty("file.separator") + libvlc);
        } catch (IOException e) {
            Logger.get().error("Could not load vlc-win-32", e);
            e.printStackTrace();
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

    public static void unzip(File source, File destination) {
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination.getAbsolutePath());
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}