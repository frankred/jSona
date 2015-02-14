package de.roth.jsona.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Logger abstraction class that holds one java util logger
 *
 * @author Frank Roth
 */
public class Logger {

    private static java.util.logging.Logger l;

    /**
     * Return logger, if logger is null then he will be created for the context
     * "jsona" and only prints out the message and log level (without time and
     * date).
     *
     * @return
     */
    public static java.util.logging.Logger get() {
        if (l == null) {
            l = java.util.logging.Logger.getLogger("jsona");
            Handler[] handlers = l.getHandlers();
            for (Handler h : handlers) {
                l.removeHandler(h);
            }
            l.setUseParentHandlers(false);

            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.FINEST);
            handler.setFormatter(new SingleLineLogger());
            l.addHandler(handler);
        }
        return l;
    }
}
