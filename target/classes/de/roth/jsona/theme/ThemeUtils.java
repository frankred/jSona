package de.roth.jsona.theme;

import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;

public class ThemeUtils {

    public static String getThemePath() {
        return Global.THEMES_FOLDER + "/" + Config.getInstance().THEME;
    }
}