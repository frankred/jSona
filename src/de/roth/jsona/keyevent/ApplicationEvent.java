package de.roth.jsona.keyevent;

import com.google.gson.annotations.Expose;

public enum ApplicationEvent {
	@Expose PLAYER_NEXT, @Expose PLAYER_PREVIOUS, @Expose PLAYER_PLAY_PAUSE, @Expose PLAYER_VOLUME_UP, @Expose PLAYER_VOLUME_DOWN, @Expose PLAYER_MUTE_UNMUTE, 
	@Expose VIEW_SEARCH_FOR, @Expose VIEW_HIDE_SHOW
}