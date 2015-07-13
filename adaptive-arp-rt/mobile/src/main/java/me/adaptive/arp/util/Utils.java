package me.adaptive.arp.util;

import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utils class for webView management
 */
public class Utils {

    /**
     * This method sets the default settings for a common webview inside the adaptive application
     *
     * @param webView Webview reference
     */
    public static void setWebViewSettings(WebView webView, boolean debug) {

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSaveFormData(false);
        // TODO: uncomment this line when Robolectric 3.0 released
        //webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setGeolocationEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        if (debug) {
            WebView.setWebContentsDebuggingEnabled(true);
        }


    }

    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }


    /**
     * Method for obtaining a URLStreamHandler by the protocol
     *
     * @param protocol String identifying the protocol
     * @return URLStreamHandler
     */
    /*public static URLStreamHandler getURLStreamHandler(String protocol) {
        try {
            Method method = URL.class.getDeclaredMethod("getURLStreamHandler", String.class);
            method.setAccessible(true);
            return (URLStreamHandler) method.invoke(null, protocol);
        } catch (Exception e) {
            return null;
        }
    }*/

    /**
     * Returns all the stream handlers defined in the JRE
     *
     * @return Table with all the stream handlers
     */
    /*public static Hashtable<String, URLStreamHandler> getURLStreamHandlers() {
        try {

            Field field = URL.class.getDeclaredField("streamHandlers");
            field.setAccessible(true);
            Hashtable hashtable = (Hashtable) field.get(null);
            field.setAccessible(false);
            return hashtable;
        } catch (Exception e) {
            return null;
        }
    }*/
}
