package de.roth.jsona.keyevent;

import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

public class KeyStrokeBug {

	public static void main(String[] args) {
		// Prints out 128
		System.out.println(InputEvent.CTRL_DOWN_MASK);
		KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_MASK);
		
		// Prints out 130
		System.out.println(k.getModifiers());
		
		KeyStroke x = KeyStroke.getKeyStroke("control 0");
		System.out.println(x.getModifiers());
	}
}
