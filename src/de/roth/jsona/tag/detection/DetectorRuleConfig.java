package de.roth.jsona.tag.detection;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

/**
 * This class is only for saving the detector rules for jsona in the config.
 *
 * @author Frank Roth
 *
 */
public class DetectorRuleConfig {

	/**
	 * Currently there are two detection rules
	 *
	 * @author Frank Roth
	 *
	 */
	public enum DetectorRuleEnum {
		ROOT_SUBFOLDER_LEVEL_RULE, FILENAME_RULE
	}

	@Expose private DetectorRuleEnum rule;
	@Expose private Map<String, Object> params;

	public DetectorRuleConfig(DetectorRuleEnum rule) {
		this.rule = rule;
		this.params = new HashMap<String, Object>();
	}

	public DetectorRuleEnum getRule() {
		return rule;
	}

	public void setRule(DetectorRuleEnum rule) {
		this.rule = rule;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
}
