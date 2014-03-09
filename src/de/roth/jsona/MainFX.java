package de.roth.jsona;

import java.util.Locale;
import java.util.logging.Level;

import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;
import de.roth.jsona.javafx.ViewManagerFX;
import de.roth.jsona.logic.LogicManagerFX;
import de.roth.jsona.util.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This is where all begins...
 * 
 * @author Frank Roth
 * 
 */
public class MainFX extends Application {

	@Override
	public void start(final Stage stage) throws Exception {
		// Set default language
		Locale.setDefault(Locale.ENGLISH);

		// Set log level
		Logger.get().setLevel(Level.ALL);
		
		// Print out the logo
		Logger.get().log(Level.INFO, "Starting jSona..." + System.lineSeparator() + getLogo());

		// Load configuration
		Config.load(Global.CONFIG_JSON);

		// Set vlcj path
		System.setProperty("jna.library.path", Config.getInstance().PATH_TO_VLCJ);

		// Create Logic
		LogicManagerFX logic = new LogicManagerFX();

		// Create view
		ViewManagerFX.getInstance().init(stage, logic, Config.getInstance().WINDOW_UNDECORATED);

		// Go for it...
		logic.start();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static String getLogo() {
		return System.lineSeparator() + System.lineSeparator() +
			"   _  _____                   " + System.lineSeparator() +
			"  (_)/ ____|                  " + System.lineSeparator() +
			"   _| (___   ___  _ __   __ _ " + System.lineSeparator() +
			"  | |\\___ \\ / _ \\| '_ \\ / _` |" + System.lineSeparator() +
			"  | |____) | (_) | | | | (_| |" + System.lineSeparator() +
			"  | |_____/ \\___/|_| |_|\\__,_|" + System.lineSeparator() +
			" _/ |                   " + System.lineSeparator() +
			"|__/    1.0.0           " + System.lineSeparator() + System.lineSeparator() +
			"vlcj version: 3.0.1" + System.lineSeparator() +
			"lucene version: 4.7.0" + System.lineSeparator() +
			"javafx: " + com.sun.javafx.runtime.VersionInfo.getRuntimeVersion() + 
			System.lineSeparator();
	}
}