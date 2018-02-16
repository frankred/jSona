package de.roth.jsona.keyevent;

import com.google.gson.annotations.Expose;

/**
 * All possbile events that can be called with the help of a hotkey combination
 *
 * @author Frank Roth
 */
public enum ApplicationEvent {
    @Expose PLAYER_NEXT, @Expose PLAYER_PREVIOUS, @Expose PLAYER_PLAY_PAUSE, @Expose PLAYER_VOLUME_UP, @Expose PLAYER_VOLUME_DOWN, @Expose PLAYER_MUTE_UNMUTE,
    @Expose PLAYER_TIME_UP, @Expose PLAYER_TIME_DOWN,
    @Expose VIEW_SEARCH_FOR, @Expose VIEW_HIDE_SHOW
}