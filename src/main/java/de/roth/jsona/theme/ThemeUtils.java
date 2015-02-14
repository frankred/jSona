package de.roth.jsona.theme;

import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;
import de.roth.jsona.file.FileUtils;
import de.roth.jsona.util.Logger;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ThemeUtils {

    public static String themeFromConfigOrFallback() {
        return findValidTheme(Config.getInstance().THEME);
    }

    public static String getThemePath() {
        return getThemeFolder() + File.separator + Config.getInstance().THEME;
    }

    public static String getThemeFolder() {
        return Global.THEMES_FOLDER;
    }

    private static String findValidTheme(String theme) {
        if (themeExists(theme)) {
            return theme;
        }

        Logger.get().info(StringUtils.join("Theme '", theme, "' not found"));
        String alternativeTheme = getAvailableThemes().get(0);

        Logger.get().info(StringUtils.join("Alternative theme '", alternativeTheme, "' chosen"));
        Config.getInstance().THEME = alternativeTheme;

        return alternativeTheme;
    }

    private static boolean themeExists(String themeName) {
        List<String> themes = getAvailableThemes();
        for (String theme : themes) {
            if (theme.equals(themeName)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> getAvailableThemes() {
        File themeFolder = new File(ThemeUtils.class.getClassLoader().getResource(getThemeFolder()).getPath());
        File[] themes = FileUtils.listSubDirectories(themeFolder);

        List<String> themeNames = new LinkedList<String>();

        for (File theme : themes) {
            themeNames.add(theme.getName());
        }

        return themeNames;
    }
}