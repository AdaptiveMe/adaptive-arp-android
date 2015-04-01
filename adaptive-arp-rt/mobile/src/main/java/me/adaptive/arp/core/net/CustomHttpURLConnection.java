package me.adaptive.arp.core.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILogging;

/**
 * Custom URL Connection to wrap the intercepted http and https requests for the application
 */
public class CustomHttpURLConnection extends HttpURLConnection {

    /**
     * Default Constructor
     *
     * @param url Url of the connection
     */
    public CustomHttpURLConnection(URL url) {
        super(url);
    }

    /**
     * Releases this connection so that its resources may be either reused or closed.
     */
    @Override
    public void disconnect() {
    }

    /**
     * Returns whether this connection uses a proxy server or not.
     *
     * @return true if this connection passes a proxy server, false otherwise.
     */
    @Override
    public boolean usingProxy() {
        return false;
    }

    /**
     * Opens a connection to the resource. This method will not reconnect to a resource after the
     * initial connection has been closed.
     *
     * @throws IOException if an error occurs while connecting to the resource.
     */
    @Override
    public void connect() throws IOException {
    }
}
