package de.roth.jsona.keyevent;

import java.io.Serializable;

public class KeyEventAction implements Serializable {

	private static final long serialVersionUID = 3578592023740069773L;
	private int uniqueId;
	private boolean isGlobal;
	private boolean strgPressed;
	private boolean altPressed;
	private boolean shiftPressed;
	private int key;

	public enum EventAction {
		NEXT, PREVIOUS, PLAY, PAUSE, STOP, VOLUME_UP, VOLUME_DOWN, SEARCH_TOGGLE
	}

	private EventAction eventAction;

	public KeyEventAction(int uniqueId) {
		this.uniqueId = uniqueId;
	}

	public boolean isStrgPressed() {
		return strgPressed;
	}

	public void setStrgPressed(boolean strgPressed) {
		this.strgPressed = strgPressed;
	}

	public boolean isAltPressed() {
		return altPressed;
	}

	public void setAltPressed(boolean altPressed) {
		this.altPressed = altPressed;
	}

	public boolean isShiftPressed() {
		return shiftPressed;
	}

	public void setShiftPressed(boolean shiftPressed) {
		this.shiftPressed = shiftPressed;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public EventAction getEventAction() {
		return eventAction;
	}

	public void setEventAction(EventAction eventAction) {
		this.eventAction = eventAction;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	public String getText() {
		StringBuffer text = new StringBuffer();
		if (strgPressed) {
			text.append("STRG + ");
		}
		if (altPressed) {
			text.append("ALT + ");
		}
		if (shiftPressed) {
			text.append("SHIFT + ");
		}

		text.append("(Code: " + key + ")");
		return text.toString();
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	public String toString() {
		return getText() + " = " + eventAction;
	}
}
