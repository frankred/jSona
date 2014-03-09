package de.roth.jsona.tag.detection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.roth.jsona.config.Config;

/**
 * Detector manager, converts the detector rule config saved in the config file
 * in rule objects and runs the detection if somebody calls the detect method.
 *
 * @author Frank
 *
 */
public class DetectorRulesManager {

	private static DetectorRulesManager instance = new DetectorRulesManager();

	private ArrayList<DetectorRule> rules;

	public static DetectorRulesManager getInstance() {
		return instance;
	}

	public DetectorRulesManager() {
		this.rules = new ArrayList<DetectorRule>();

		// Read the detector rules out from config file and create detector
		// classes
		this.convertConfigDetectorRulesToDetectorRuleList();
	}

	public ArrayList<DetectorRule> getRules() {
		return rules;
	}

	public void setRules(ArrayList<DetectorRule> rules) {
		this.rules = rules;
	}

	/**
	 * Run all detection rules and return the results.
	 *
	 * @param rootFolder
	 * @param file
	 * @return
	 */
	public ArrayList<FieldResult> detect(File rootFolder, File file) {
		ArrayList<FieldResult> results = new ArrayList<FieldResult>();
		for (DetectorRule r : this.rules) {
			ArrayList<FieldResult> fr = r.detect(rootFolder, file);

			if (fr == null) {
				continue;
			}
			results.addAll(fr);
		}

		return results;
	}

	/**
	 * Converts the detector configuration in detector rule instances.
	 */
	private void convertConfigDetectorRulesToDetectorRuleList() {
		this.rules.clear();

		List<DetectorRuleConfig> configs = Config.getInstance().FILEPATH_BASED_MUSIC_INFORMATIONS;

		for (DetectorRuleConfig c : configs) {

			// default booleans
			boolean ignoreFileEnding = true;
			boolean replaceUnderscoresWithSpaces = false;

			String pattern = null;

			// Pattern always required
			if (c.getParams().get("PATTERN") == null) {
				continue;
			}

			// Setting parameters for all DetectorRules
			if (c.getParams().get("PATTERN") != null) {
				pattern = (String) c.getParams().get("PATTERN");
			}

			if (c.getParams().get("IGNORE_FILE_ENDING") != null) {
				ignoreFileEnding = (boolean) c.getParams().get("IGNORE_FILE_ENDING");
			}

			if (c.getParams().get("REPLACE_UNDERSCORES_WITH_SPACES") != null) {
				replaceUnderscoresWithSpaces = (boolean) c.getParams().get("REPLACE_UNDERSCORES_WITH_SPACES");
			}

			switch (c.getRule()) {
			case FILENAME_RULE:
				this.rules.add(new FilenameDetectorRule(pattern, ignoreFileEnding, replaceUnderscoresWithSpaces));
				break;
			case ROOT_SUBFOLDER_LEVEL_RULE:
				if (c.getParams().get("FOLDER_LEVEL") != null) {
					double level = (double) c.getParams().get("FOLDER_LEVEL");
					this.rules.add(new RootSubfolderLevelDetectorRule((int) level, pattern, ignoreFileEnding, replaceUnderscoresWithSpaces));
				}
				break;
			}
		}
	}
}
