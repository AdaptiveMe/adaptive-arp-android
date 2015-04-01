package me.adaptive.arp.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils class for public static methods
 */
public class Utils {

    /**
     * Validates a string with a regular expression passed by parameter
     *
     * @param s String to validate
     * @param r Regular expression
     */
    public static boolean validateRegexp(String s, String r) {
        try {
            Pattern pattern = Pattern.compile(r);
            Matcher matcher = pattern.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * This method returns a byte Array from an input stream
     *
     * @param is Input Stream to read
     * @return Array of bytes
     */
    public static byte[] getBytesFromInputStream(InputStream is) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1; )
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Remove file extension from path
     *
     * @param filePath File path
     * @return File without extension
     */
    public static String removeExtension(String filePath) {

        File f = new File(filePath);
        if (f.isDirectory()) return filePath;

        String name = f.getName();

        final int lastPeriodPos = name.lastIndexOf('.');
        if (lastPeriodPos <= 0) {
            return filePath;
        } else {
            File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
            return renamed.getPath();
        }
    }
}
