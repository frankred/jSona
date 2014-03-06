package de.roth.jsona.folderwatch.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.roth.jsona.folderwatch.DirWatcher;
import de.roth.jsona.folderwatch.WatchDirListener;

public class DirWatcherTest {

	private DirWatcher folderWatcher;

	@Before
	public void setUp() throws Exception {
		folderWatcher = new DirWatcher();
	}

	@Test
	public void test() {
		String folder = "D:\\media\\musik";

		File f = new File(folder);

		WatchDirListener fwl = new WatchDirListener() {

			@Override
			public void fileModified(Path path, Path path2) {
				System.out.println("Modified: " + path);
				Assert.assertEquals("junittestfile", path.toString());
			}

			@Override
			public void fileDeleted(Path path, Path path2) {
				System.out.println("Deleted: " + path);
				Assert.assertEquals("junittestfile", path.toString());
			}

			@Override
			public void fileCreated(Path path, Path path2) {
				System.out.println("Created: " + path);
				Assert.assertEquals("junittestfile", path.toString());
			}
		};

		try {
			folderWatcher.watch(f, fwl);

			try {
				Thread.sleep(1000);

				File test = new File(folder + "\\" + "junittestfile");
				touch(test);

				Thread.sleep(1000);

				test.delete();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a file, or modify last modified date. Like the linux command
	 * touch
	 * 
	 * @param file
	 */
	private void touch(File file) {
		try {
			if (!file.exists())
				new FileOutputStream(file).close();
			file.setLastModified((new Date()).getTime());
		} catch (IOException e) {
			// do nothing
		}
	}
}
