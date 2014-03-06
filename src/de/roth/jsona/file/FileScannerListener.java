package de.roth.jsona.file;

import java.io.File;
import java.util.LinkedList;

/**
 * File scanner listener interface
 * 
 * @author Frank Roth
 * 
 */
public interface FileScannerListener {

	public void scannerStart();

	public void scannerRootFileRead(File f, int fileNumber);

	public void scannerFileRead(File f);

	public void scannerFinished(String target, LinkedList<File> allFiles);

}
