package me.adaptive.arp.impl.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.adaptive.arp.api.ILoggingLogLevel;

public class Utils {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

    public static void log(ILoggingLogLevel level, String category, String message) {
        System.out.println(sdf.format(new Date().getTime()) + " [" + level.toString() + " - " + category + "] " + message);
    }
}
