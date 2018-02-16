package de.roth.jsona.keyevent;

import com.google.gson.annotations.Expose;

import javax.swing.*;

/**
 * This class represents a hotkey for the application with an keyEvent
 * (http://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html) one
 * ore more
 * modifier(http://docs.oracle.com/javase/7/docs/api/java/awt/event/InputEvent
 * .html) and a global property.
 *
 * @author Frank Roth
 */
public class HotkeyConfig {

    @Expose
    private int key;
    private int allModifiers;
    @Expose
    private int[] modifiers;
    @Expose
    private ApplicationEvent event;
    @Expose
    private boolean global;
    private KeyStroke stroke;

    public HotkeyConfig(int key, int[] modifiers, ApplicationEvent applicationEvent, boolean global) {
        this.key = key;
        this.modifiers = modifiers;
        this.event = applicationEvent;
        this.global = global;
    }

    public int getAllModifiers() {
        return this.allModifiers;
    }

    public KeyStroke getKeyStroke() {
        // Initial create a key stroke for the hotkey config. KeyStroke is
        // needed to register the hotkeys.
        if (this.stroke == null) {
            this.allModifiers = 0;
            if (modifiers != null && modifiers.length > 0) {
                this.allModifiers = modifiers[0];
                for (int i = 1; i < modifiers.length; i++) {
                    allModifiers = allModifiers | modifiers[i];
                }
            }
            this.stroke = KeyStroke.getKeyStroke(key, getAllModifiers());
            this.allModifiers = stroke.getModifiers();
        }
        return stroke;
    }

    public int getKey() {
        return key;
    }

    public ApplicationEvent getApplicationEvent() {
        return event;
    }

    public boolean isGlobal() {
        return global;
    }

    public int[] getModifiers() {
        return modifiers;
    }

    public ApplicationEvent getEvent() {
        return event;
    }

    public void setEvent(ApplicationEvent event) {
        this.event = event;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setAllModifiers(int allModifiers) {
        this.allModifiers = allModifiers;
    }

    public void setModifiers(int[] modifiers) {
        this.modifiers = modifiers;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }
}
