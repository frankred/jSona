package de.roth.jsona.view;

import de.roth.jsona.logic.LogicInterfaceFX;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.model.PlayList;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public interface ViewInterface {

    public void initialize(URL location, ResourceBundle resources);

    public void initPlaylists(LogicInterfaceFX logic, ArrayList<PlayList> playlists, int activeIndex);

    public void newPlaylist(LogicInterfaceFX logic, PlayList p);

    public void setMusicFolder(LogicInterfaceFX logic, String name, String tabid, int pos, ArrayList<MusicListItem> items);

    public void showSearchResults(ArrayList<MusicListItem> searchResult, int searchResultCounter);

    public void showSearchResultError(String message);

}
