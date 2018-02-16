package de.roth.jsona.view;

import de.roth.jsona.logic.LogicInterfaceFX;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.model.Playlist;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public interface ViewInterface {

    public void initialize(URL location, ResourceBundle resources);

    public void initPlaylists(LogicInterfaceFX logic, ArrayList<Playlist> playlists, int activeIndex);

    public void newPlaylist(LogicInterfaceFX logic, Playlist p);

    public void setMusicFolder(LogicInterfaceFX logic, String name, String tabid, int pos, ArrayList<MusicListItem> items);

    public void showSearchResults(ArrayList<MusicListItem> searchResult, int searchResultCounter);

}
