package de.roth.jsona.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Core music item of jsona.
 * 
 * @author Frank Roth
 * 
 */
public class MusicListItem implements Serializable, Observable {

	private static final long serialVersionUID = -3582887378286675733L;
	private String id;
	private File file;
	private File rootFolder;
	private String duration;
	private String artist;
	private String title;
	private String album;
	private String trackNo;
	private String year;
	private int genre;
	private String summary;
	private Date creationDate;
	private long lastFileModification;
	private int colorClass;
	private ArrayList<InvalidationListener> listeners;

	public enum Status {
		SET_NONE, SET_PLAYING, SET_PAUSED
	}

	private Status tmp_status; // this breaks MVC, but fuck my life, JavaFX does
								// not support the access to the cell in the
								// listView, so this property is set if the
								// listCell changes it state and has to be
								// repainted.
	private boolean tmp_view_insertBeforeMe = false;
	private boolean tmp_keep_in_cache = false;

	public MusicListItem(File file, File rootFolder, String id) {
		this.id = id;
		this.genre = -1;
		this.lastFileModification = file.lastModified();
		this.file = file;
		this.rootFolder = rootFolder;
		this.tmp_status = Status.SET_NONE;
		this.listeners = new ArrayList<InvalidationListener>();
	}

	public MusicListItem() {
		this.tmp_status = Status.SET_NONE;
		this.genre = -1;
		this.listeners = new ArrayList<InvalidationListener>();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
		invalidate();
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String titel) {
		this.title = titel;
	}

	public String getTrackNo() {
		return trackNo;
	}

	public void setTrackNo(String trackNo) {
		this.trackNo = trackNo;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public MusicListItem clone() {
		MusicListItem item = new MusicListItem();
		item.setLastFileModification(lastFileModification);
		item.setGenre(genre);

		if (this.id != null) {
			item.setId(new String(this.id));
		}

		if (this.creationDate != null) {
			item.setCreationDate((Date) this.creationDate.clone());
		}

		if (duration != null) {
			item.setDuration(new String(duration));
		}
		if (file.getAbsolutePath() != null) {
			item.setFile(new File(new String(file.getAbsolutePath())));
		}
		if (artist != null) {
			item.setArtist(new String(artist));
		}
		if (title != null) {
			item.setTitle(new String(title));
		}
		if (trackNo != null) {
			item.setTrackNo(new String(trackNo));
		}
		if (album != null) {
			item.setAlbum(new String(album));
		}
		if (year != null) {
			item.setYear(new String(year));
		}

		if (summary != null) {
			item.setSummary(new String(summary));
		}
		return item;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return this.getFile().getAbsolutePath();
	}

	public Status getTmp_status() {
		return tmp_status;
	}

	public void setTmp_status(Status tmp_status) {
		this.tmp_status = tmp_status;
	}

	public long getLastFileModification() {
		return lastFileModification;
	}

	public void setLastFileModification(long lastFileModification) {
		this.lastFileModification = lastFileModification;
	}

	public boolean isTmp_keep_in_cache() {
		return tmp_keep_in_cache;
	}

	public void setTmp_keep_in_cache(boolean tmp_keep_in_cache) {
		this.tmp_keep_in_cache = tmp_keep_in_cache;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isTmp_view_insertBeforeMe() {
		return tmp_view_insertBeforeMe;
	}

	public void setTmp_view_insertBeforeMe(boolean tmp_view_insertBeforeMe) {
		this.tmp_view_insertBeforeMe = tmp_view_insertBeforeMe;
	}

	public int getColorClass() {
		return colorClass;
	}

	public void setColorClass(int colorClass) {
		this.colorClass = colorClass;
	}

	public int getGenre() {
		return genre;
	}

	public void setGenre(int genre) {
		this.genre = genre;
	}

	public File getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(File rootFolder) {
		this.rootFolder = rootFolder;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		this.listeners.remove(listener);
	}

	private void invalidate() {
		for (InvalidationListener l : this.listeners) {
			l.invalidated(this);
		}
	}
}