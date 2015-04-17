package me.adaptive.arp.impl.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Util class for unit testing
 */
public class Utils {

    // Simple Date format
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

    /**
     * Prints to the standard output messages. NOTE: in gradle test, the only output visible is the standart output
     *
     * @param level    Log level
     * @param category Category of the message
     * @param message  Message
     */
    public static void log(ILoggingLogLevel level, String category, String message) {
        System.out.println(sdf.format(new Date().getTime()) + " [" + level.toString() + " - " + category + "] " + message);
    }
}
