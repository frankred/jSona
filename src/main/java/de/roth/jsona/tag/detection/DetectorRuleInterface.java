package de.roth.jsona.tag.detection;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for detection
 *
 * @author Frank Roth
 */
public interface DetectorRuleInterface {
    public ArrayList<FieldResult> detect(File rootFolder, File file);
}