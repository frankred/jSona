package de.roth.jsona.model.playlist;

import de.roth.jsona.util.Serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaylistManager {

    String playlistFile;
    Map<String, Playlist> playlists;

    public PlaylistManager() {
        this.playlists = new HashMap();
    }

    public PlaylistManager(String playlistFile) {
        this.playlistFile = playlistFile;
        loadPlaylists();
    }

    public Playlist getPlaylist(String id) {
        return playlists.get(id);
    }

    public String addPlaylist(Playlist playlist) {
        String id = UUID.randomUUID().toString();
        playlists.put(id, playlist);
        return id;
    }

    public void renamePlaylist(String id, String newName) {
        playlists.get(id).setName(newName);
    }

    private void loadPlaylists() {
        Object playlistsObject = Serializer.load(playlistFile);

        if (playlistsObject == null) {
            this.playlists = new HashMap();
        }

        if (playlistsObject instanceof ArrayList) {
            this.playlists = new HashMap();
            for (Playlist p : (ArrayList<Playlist>) playlistsObject) {
                addPlaylist(p);
            }
        }

        if (playlistsObject instanceof Map) {
            this.playlists = (Map<String, Playlist>) playlistsObject;
        }
    }
}
