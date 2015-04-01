package me.adaptive.arp.core.net;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Custom Stream Handler for managing the http & https request inside the Adaptive Application.
 */
public class CustomStreamHandler extends URLStreamHandler {

    // Logger
    private static final String LOG_TAG = "CustomStreamHandler";
    private ILogging logger;

    // Original Stream Handler
    private URLStreamHandler original;

    /**
     * Default Constructor.
     */
    public CustomStreamHandler() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
    }

    /**
     * Overloaded Contructor with the original Stream Handler.
     *
     * @param original Original Stream Handler
     */
    public CustomStreamHandler(URLStreamHandler original) {
        this();
        this.original = original;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {

        // Obtain the original connection
        URLConnection connection = getOriginalOpenConnection(u);
        HttpURLConnection httpConnection = (HttpURLConnection) connection;

        String method = httpConnection.getRequestMethod();
        String url = u.toExternalForm();

        logger.log(ILoggingLogLevel.Error, LOG_TAG, method + " " + url);

        return connection;
    }

    @Override protected int getDefaultPort() {
        return 443;
    }

    private URLConnection getOriginalOpenConnection(URL u) {
        try {
            Method method = original.getClass().getDeclaredMethod("openConnection", URL.class);
            method.setAccessible(true);
            return (URLConnection) method.invoke(original, u);
        } catch (Exception e) {
            return null;
        }
    }
}
