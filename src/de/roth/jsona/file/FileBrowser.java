package de.roth.jsona.file;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 * Util class for os file browser actions
 * 
 * @author Frank Roth
 * 
 */
public class FileBrowser {

	/**
	 * Open file browser with the over given file
	 * 
	 * @param file
	 */
	public static void open(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}