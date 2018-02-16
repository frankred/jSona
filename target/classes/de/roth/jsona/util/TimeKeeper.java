package de.roth.jsona.util;

import java.util.logging.Level;

/**
 * Class to stop time.
 *
 * @author Frank Roth
 */
public class TimeKeeper {

    private static TimeKeeper i = new TimeKeeper();
    private long start;
    private long time;
    private String name;

    /**
     * Get time keeper
     *
     * @return
     */
    public static TimeKeeper get() {
        return i;
    }

    /**
     * Start to measure the time
     *
     * @param name - output message name
     */
    public void start(String name) {
        this.name = name;
        this.start = System.currentTimeMillis();
    }

    /**
     * Stop the time and log it
     */
    public void stop() {
        this.time = System.currentTimeMillis() - this.start;
        Logger.get().info("Duration for process '" + this.name + "' was " + getTime() + "ns.");
    }


    public long getTime() {
        return time;
    }
}
