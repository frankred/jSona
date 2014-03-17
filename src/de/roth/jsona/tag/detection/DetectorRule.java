package de.roth.jsona.tag.detection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.annotations.Expose;

import de.roth.jsona.tag.detection.FieldResult.Field;

/**
 * Root class for tag detection wihout id3 with the help of the file path.
 *
 * @author Frank Roth
 *
 */
public abstract class DetectorRule implements DetectorRuleInterface {

	@Expose private String pattern;
	@Expose private boolean replaceUnderscoresWithSpaceInResults;
	@Expose private boolean ignoreFileEnding;

	public boolean isReplaceUnderscoresWithSpaceInResults() {
		return replaceUnderscoresWithSpaceInResults;
	}

	public void setReplaceUnderscoresWithSpaceInResults(boolean replaceUnderscoresWithSpaceInResults) {
		this.replaceUnderscoresWithSpaceInResults = replaceUnderscoresWithSpaceInResults;
	}

	/**
	 * Find all matches for a given pattern. E.g. the pattern '%ARTIST% -
	 * %TITLE%' for the String 'Madonna - Frozen' will return a map with the
	 * fields 'ARTIST':'Madonna' and 'TITLE':'Frozen'. Method is from: http
	 * ://stackoverflow.com/questions/22246012/parsing-text-with-variables- like
	 * -name-in-it. Thank you to Florent Bayle.
	 *
	 * @param pattern
	 * @param text
	 * @return All matches for the variables
	 */
	public ArrayList<FieldResult> match(final String pattern, final String text) {
		final StringBuilder regexp = new StringBuilder("^");

		final List<String> varNames = new LinkedList<String>();
		int i = 0;
		for (final String subPart : pattern.split("%", -1)) {
			if (i++ % 2 != 0) {
				regexp.append("(.*)");
				varNames.add(subPart);
			} else {
				regexp.append(Pattern.quote(subPart));
			}
		}
		regexp.append("$");

		final Pattern p = Pattern.compile(regexp.toString());
		final Matcher m = p.matcher(text);

		final ArrayList<FieldResult> fields = new ArrayList<FieldResult>();
		if (m.matches()) {
			int j = 1;
			for (final String varName : varNames) {
				Field field = FieldResult.getFieldByString(varName);

				// Invalid field, just ignore it
				if (field == null) {
					m.group(j++);
					continue;
				}

				FieldResult result = new FieldResult();
				result.setField(field);
				String resultValue = m.group(j++).trim();

				if (replaceUnderscoresWithSpaceInResults) {
					resultValue = resultValue.replace("_", " ");
				}

				result.setResult(resultValue);
				fields.add(result);
			}
		}
		return fields;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean isIgnoreFileEnding() {
		return ignoreFileEnding;
	}

	public void setIgnoreFileEnding(boolean ignoreFileEnding) {
		this.ignoreFileEnding = ignoreFileEnding;
	}
}