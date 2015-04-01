package me.adaptive.arp.core;

import android.content.Context;
import android.webkit.MimeTypeMap;

import java.io.IOException;
import java.io.InputStream;

import me.adaptive.arp.R;
import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.AppResourceData;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.common.Utils;

/**
 * Application Resource Manager
 */
public class AppResourceManager {

    // Logger
    private static final String LOG_TAG = "WebViewClient";
    private static ILogging logger;

    // Context
    private static Context context;

    // Singleton instance
    private static AppResourceManager instance = null;

    public static AppResourceManager getInstance() {
        if (instance == null) {
            instance = new AppResourceManager();
            logger = AppRegistryBridge.getInstance().getLoggingBridge();
            context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
        }
        return instance;
    }

    /**
     * Retrieve a web resource stored inside the ARP packer by url
     *
     * @param url Url of the resource
     * @return Application Resource Data
     */
    public AppResourceData retrieveWebResource(String url) {

        // TODO: Implement AppPacker Reader for Android
        // TODO: remove this code (LEGACY)

        String file = url.replaceFirst(String.valueOf(R.string.arp_url), "www/");
        return this.retriveResource(file);
    }

    /**
     * Retrieve a config resource stored inside the ARP packer by url
     *
     * @param url Url of the resource
     * @return Application Resource Data
     */
    public AppResourceData retrieveConfigResource(String url) {

        // TODO: Implement AppPacker Reader for Android
        // TODO: remove this code (LEGACY)

        String file = url.replaceFirst(String.valueOf(R.string.arp_url), "config/");
        return this.retriveResource(file);
    }

    /**
     * REtrieve a common resource for the application
     *
     * @param url Url of the resource
     * @return Application Resource Data
     */
    private AppResourceData retriveResource(String url) {

        // TODO: Implement AppPacker Reader for Android
        // TODO: remove this code (LEGACY)

        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "Retrieveing resource: " + url);

        AppResourceData resource = new AppResourceData();
        InputStream is;
        try {
            is = context.getAssets().open(url);
            resource.setData(Utils.getBytesFromInputStream(is));
        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, e.getMessage());
            resource.setData("<html><body><h1>404</h1></body></html>".getBytes());
        }

        resource.setId("1");
        resource.setRawType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("file:///android_asset/" + url));
        resource.setRawLength(0);
        resource.setCooked(false);
        resource.setCookedType("");
        resource.setCookedLength(0);

        return resource;
    }
}
