package de.roth.jsona.logic;

import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.model.Playlist;

import java.util.List;

public interface LogicInterfaceFX {

    public void start();

    public void close();

    public void event_playlist_changed(Playlist playlist);

    public void event_playlist_namechanged(String atomicId, String oldname, String newname);

    public void event_playlist_new();

    public void event_playlist_remove(String atomicId);

    public void event_ui_exit();

    public void event_ui_hide();

    public void event_player_next(MusicListItem item, MusicListItem nextItem);

    public void event_player_previous(MusicListItem item, MusicListItem prevItem);

    public void event_player_play_skipto(double value);

    public void event_player_volume(int newValue, int oldValue);

    public void event_player_play_pause();

    public void event_playbackmode_normal();

    public void event_playbackmode_shuffle();

    public void event_playbackmode_repeat();

    public void event_play_url(String url);

    public MusicListItem event_playlist_url_dropped(String url, Playlist playlist);

    public void event_search_music(String query);

    public void action_player_volume_up();

    public void action_player_volume_up(int stepsize);

    public void action_player_volume_down();

    public void action_player_volume_down(int stepsize);

    public void action_volume_mute_unmute();

    public void action_player_play_pause();

    public void action_toggle_view();

    public List<String> equalizer_presets();

    public float equalizer_max_gain();

    public float equalizer_min_gain();

    public int equalizer_amps_amount();

    public float[] equalizer_amps(String preset);

    public void equalizer_set_amps(float[] amps);

    public void equalizer_set_amp(int index, float value);

    public void equalizer_disable();

    public boolean equalizer_available();
}
