package de.roth.jsona.model;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Playlist implements Serializable {

    private String id;
    private String name;
    private ArrayList<MusicListItem> items;

    public Playlist(String id, String name) {
        this.id = id;
        this.name = name;
        this.items = new ArrayList<MusicListItem>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MusicListItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<MusicListItem> items) {
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}