package de.roth.jsona.tag.detection;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is able to detect music informations with the help of the filename
 * (only the filename, not the filepath). A pattern has to be over given in the
 * constructor. With the help of this pattern(e.g.: '%ARTIST%-%TITLE%') it is
 * possible to match the artist, title, genre or track number.
 *
 * @author Frank Roth
 */
public class FilenameDetectorRule extends DetectorRule {

    public FilenameDetectorRule(String pattern, boolean ignoreFileEnding, boolean replaceUnderscoresWithSpaces) {
        setIgnoreFileEnding(ignoreFileEnding);
        setReplaceUnderscoresWithSpaceInResults(replaceUnderscoresWithSpaces);
        this.setPattern(pattern);
    }

    public ArrayList<FieldResult> detect(File rootFolder, File file) {
        String filename = file.getName();

        if (isIgnoreFileEnding()) {
            filename = removeFileExtension(filename);
        }

        ArrayList<FieldResult> results = match(this.getPattern(), filename);

        if (results.size() == 0) {
            return null;
        }

        return results;
    }

    private String removeFileExtension(String name) {
        if (name.indexOf(".") > 0) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        return name;
    }
}