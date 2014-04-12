/*
 * Copyright (c) 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tulskiy.keymaster.windows;

import static com.tulskiy.keymaster.windows.User32.MOD_ALT;
import static com.tulskiy.keymaster.windows.User32.MOD_CONTROL;
import static com.tulskiy.keymaster.windows.User32.MOD_NOREPEAT;
import static com.tulskiy.keymaster.windows.User32.MOD_SHIFT;
import static com.tulskiy.keymaster.windows.User32.MOD_WIN;
import static com.tulskiy.keymaster.windows.User32.VK_MEDIA_NEXT_TRACK;
import static com.tulskiy.keymaster.windows.User32.VK_MEDIA_PLAY_PAUSE;
import static com.tulskiy.keymaster.windows.User32.VK_MEDIA_PREV_TRACK;
import static com.tulskiy.keymaster.windows.User32.VK_MEDIA_STOP;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_INSERT;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_PERIOD;
import static java.awt.event.KeyEvent.VK_PLUS;
import static java.awt.event.KeyEvent.VK_PRINTSCREEN;
import static java.awt.event.KeyEvent.VK_SEMICOLON;
import static java.awt.event.KeyEvent.VK_SLASH;

import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.KeyStroke;

import com.tulskiy.keymaster.common.HotKey;

/**
 * Author: Denis Tulskiy
 * Date: 6/20/11
 */
public class KeyMap {
    private static final Map<Integer, Integer> codeExceptions = new HashMap<Integer, Integer>() {{
        put(VK_INSERT, 0x2D);
        put(VK_DELETE, 0x2E);
        put(VK_ENTER, 0x0D);
        put(VK_COMMA, 0xBC);
        put(VK_PERIOD, 0xBE);
        put(VK_PLUS, 0xBB);
        put(VK_MINUS, 0xBD);
        put(VK_SLASH, 0xBF);
        put(VK_SEMICOLON, 0xBA);
        put(VK_PRINTSCREEN, 0x2C);
    }};

    public static int getCode(HotKey hotKey) {
        if (hotKey.isMedia()) {
            int code = 0;
            switch (hotKey.mediaKey) {
                case MEDIA_NEXT_TRACK:
                    code = VK_MEDIA_NEXT_TRACK;
                    break;
                case MEDIA_PLAY_PAUSE:
                    code = VK_MEDIA_PLAY_PAUSE;
                    break;
                case MEDIA_PREV_TRACK:
                    code = VK_MEDIA_PREV_TRACK;
                    break;
                case MEDIA_STOP:
                    code = VK_MEDIA_STOP;
                    break;
            }

            return code;
        } else {
            KeyStroke keyStroke = hotKey.keyStroke;
            Integer code = codeExceptions.get(keyStroke.getKeyCode());
            if (code != null) {
                return code;
            } else
                return keyStroke.getKeyCode();
        }
    }

    public static int getModifiers(KeyStroke keyCode) {
        int modifiers = 0;
        if (keyCode != null) {
            if ((keyCode.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                modifiers |= MOD_SHIFT;
            }
            if ((keyCode.getModifiers() & InputEvent.CTRL_DOWN_MASK) != 0) {
                modifiers |= MOD_CONTROL;
            }
            if ((keyCode.getModifiers() & InputEvent.META_DOWN_MASK) != 0) {
                modifiers |= MOD_WIN;
            }
            if ((keyCode.getModifiers() & InputEvent.ALT_DOWN_MASK) != 0) {
                modifiers |= MOD_ALT;
            }
        }

        if (System.getProperty("os.name", "").startsWith("Windows 7")) {
            modifiers |= MOD_NOREPEAT;
        }
        return modifiers;
    }
}
