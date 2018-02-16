package de.roth.jsona.util;

/**
 * Class to filter a set of special characters in Strings
 *
 * @author Frank Roth
 */
public class ASCIIFilter {

    /**
     * Filter a set of special characters in Strings
     *
     * @param chars String[] of special characters
     * @param Text  that has to be filtered
     * @return Filtered result
     */
    public static String filter(String[] chars, String text) {
        for (String s : chars) {
            text = text.replaceAll(s, "");
        }
        return text;
    }
}
