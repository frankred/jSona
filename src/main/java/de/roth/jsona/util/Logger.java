package de.roth.jsona.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;

/**
 * Logger abstraction class that holds one java util logger
 *
 * @author Frank Roth
 */
public class Logger {

    private static org.apache.log4j.Logger logger;

    /**
     * Return logger, if logger is null then he will be created for the context
     * "jsona" and only prints out the message and log level (without time and
     * date).
     *
     * @return
     */
    public static org.apache.log4j.Logger get() {

        if (logger == null) {
            logger = org.apache.log4j.Logger.getRootLogger();

            try {
                SimpleLayout layout = new SimpleLayout();
                ConsoleAppender consoleAppender = new ConsoleAppender(layout);
                logger.addAppender(consoleAppender);
                FileAppender fileAppender = new FileAppender(layout, "log/jsona.log", false);
                logger.addAppender(fileAppender);
                // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
                logger.setLevel(Level.INFO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return logger;
    }
}
