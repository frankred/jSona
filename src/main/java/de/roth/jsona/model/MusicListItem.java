package de.roth.jsona.model;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.image.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public abstract class MusicListItem implements Serializable, Observable, MusicListItemViewable, VLCPlayable {

    private transient ArrayList<InvalidationListener> listeners;
    private transient Image icon;
    private transient int colorClass;

    private String id;
    private Date creationDate;
    private String duration;

    private String artist;
    private String title;
    private String album;
    private String trackNo;
    private String year;
    private String genre;
    private String summary;

    private boolean processing;
    private transient boolean keepInCache = false;
    private transient PlaybackStatus status;


    public enum PlaybackStatus {
        SET_NONE, SET_PLAYING, SET_PAUSED
    }

    public MusicListItem() {
        this.id = UUID.randomUUID().toString();
        this.status = PlaybackStatus.SET_NONE;
        this.listeners = new ArrayList<InvalidationListener>();
    }

    public void postSerialisationProcess() {
        this.status = PlaybackStatus.SET_NONE;

        if (this.listeners == null) {
            this.listeners = new ArrayList<InvalidationListener>();
        }
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
        invalidate();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titel) {
        this.title = titel;
        invalidate();
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public PlaybackStatus getStatus() {
        return status;
    }

    public void setStatus(PlaybackStatus status) {
        this.status = status;
        invalidate();
    }

    public boolean keepInCache() {
        return keepInCache;
    }

    public void setKeepInCache(boolean keepInCache) {
        this.keepInCache = keepInCache;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getColorClass() {
        return colorClass;
    }

    public void setColorClass(int colorClass) {
        this.colorClass = colorClass;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String  genre) {
        this.genre = genre;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (this.listeners == null) {
            postSerialisationProcess();
        }
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        this.listeners.remove(listener);
    }

    private void invalidate() {
        final MusicListItem item = this;
        if (this.listeners != null && this.listeners.size() > 0) {
            // Need to be run
            Platform.runLater(new Runnable() {
                public void run() {
                    for (InvalidationListener l : listeners) {
                        l.invalidated(item);
                    }
                }
            });
        }
    }

    @Override
    public String getTextForArtistLabel() {
        return null;
    }

    @Override
    public String getTextForTitleLabel() {
        return null;
    }

    @Override
    public String getMediaURL() {
        return null;
    }

    public boolean hasTitle(){
        return this.getTitle() != null && !this.getTitle().equals("");
    }

    public boolean hasArtist(){
        return this.getArtist() != null && !this.getArtist().equals("");
    }
}