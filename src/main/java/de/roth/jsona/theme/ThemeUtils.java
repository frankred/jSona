package de.roth.jsona.theme;

import de.roth.jsona.config.Config;
import de.roth.jsona.config.Global;
import de.roth.jsona.file.FileUtils;
import de.roth.jsona.util.Logger;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ThemeUtils {

    public static String getThemePath() {
        return getThemeFolder() + "/" + Config.getInstance().THEME;
    }

    public static String getThemeFolder() {
        return Global.THEMES_FOLDER;
    }
}