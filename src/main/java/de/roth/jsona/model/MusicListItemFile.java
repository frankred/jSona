package de.roth.jsona.model;

import javafx.scene.image.Image;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class MusicListItemFile extends MusicListItem implements VLCPlayable {

    private File file;
    private File rootFolder;
    private long lastFileModification;

    public MusicListItemFile(File file, File rootFolder) {
        super();
        this.file = file;
        this.rootFolder = rootFolder;
        this.lastFileModification = file.lastModified();
    }

    @Override
    public String getMediaURL() {
        if (this.getFile() == null) {
            return null;
        }

        try {
            return new String(this.getFile().getAbsolutePath().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return StringUtils.EMPTY;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getRootFolder() {
        return rootFolder;
    }

    public long getLastFileModification() {
        return lastFileModification;
    }

    public void setLastFileModification(long lastFileModification) {
        this.lastFileModification = lastFileModification;
    }

    @Override
    public String toString() {
        return this.getFile().getAbsolutePath();
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public String getTextForArtistLabel() {
        return this.getArtist();
    }

    @Override
    public String getTextForTitleLabel() {

        if(this.hasTitle() && !this.hasArtist()){
            return this.getTitle() + "  (" + this.getFile().getName() + ")";
        }

        if(this.hasTitle() && this.hasArtist()){
            return this.getTitle()  + (this.getAlbum() != null && !this.getAlbum().equals("") ? "  (" + this.getAlbum() + ")" : "");
        }

        return this.getFile().getName();
    }
}
