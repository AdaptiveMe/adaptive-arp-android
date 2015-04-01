package me.adaptive.arp.common.webview;

import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Utils class for webView management
 */
public class Utils {

    /**
     * This method sets the default settings for a common webview inside the adaptive application
     *
     * @param webView Webview reference
     */
    public static void setWebViewSettings(WebView webView) {

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSaveFormData(false);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setGeolocationEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(false);
    }
}
