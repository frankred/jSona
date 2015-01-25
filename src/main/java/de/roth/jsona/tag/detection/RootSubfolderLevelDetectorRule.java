package de.roth.jsona.tag.detection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is able to detect music information with the help of the music
 * files root folder and sub folders. E.g.: file is: 'C:\music\Backstreet
 * Boys\This Is Us\02 Bigger.mp3' and root is: 'C:\music'. a) If you choose
 * 'folder level 1' and pattern '%ARTIST%' 'Backstreet Boys' as the first sub
 * folder of the root folder will be interpreted as the artist. b) If you choose
 * 'folder level 2' and pattern '%ALBUM%' then 'This Is Us' the seconds
 * sub folder is interpreted as the album name.
 *
 * @author Frank Roth
 *
 */
public class RootSubfolderLevelDetectorRule extends DetectorRule {

	private int folderLevel;

	public RootSubfolderLevelDetectorRule(int folderLevel, String pattern, boolean ignoreFileEnding, boolean replaceUnderscoresWithSpaces) {
		this.folderLevel = folderLevel;
		this.setPattern(pattern);
		this.setIgnoreFileEnding(ignoreFileEnding);
		this.setIgnoreFileEnding(replaceUnderscoresWithSpaces);
	}

	public int getFolderLevel() {
		return folderLevel;
	}

	public void setFolderLevel(int folderLevel) {
		this.folderLevel = folderLevel;
	}

	/**
	 *
	 */
	public ArrayList<FieldResult> detect(File rootFolder, File file) {
		boolean notSameFolder = true;
		int levelsDiff = 0;
		File currentFolder = new File(file.getAbsolutePath());

		// Find the folder
		while (notSameFolder) {
			try {
				// on the first run this should never be happen
				if (rootFolder.getCanonicalPath().equals(currentFolder.getCanonicalPath())) {
					notSameFolder = false;
				} else {
					levelsDiff++;
					currentFolder = currentFolder.getParentFile();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// know we know that we have to traverse 'levelsDiff' times up to get to
		// our folder, the root folder...
		// this means that if we traverse 'levelsDiff' - 'folderLevel' times
		// from our file up we come to the specific folder on level
		// 'folderLevel'
		currentFolder = new File(file.getAbsolutePath());
		int traverseXTimesUp = levelsDiff - folderLevel;
		for (int i = 0; i < traverseXTimesUp; i++) {
			currentFolder = currentFolder.getParentFile();
		}

		ArrayList<FieldResult> results = match(this.getPattern(), currentFolder.getName());

		if (results.size() == 0) {
			return null;
		}

		return results;
	}
}