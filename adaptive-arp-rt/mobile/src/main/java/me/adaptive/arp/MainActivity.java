package me.adaptive.arp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.webkit.WebView;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Hashtable;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.LifecycleState;
import me.adaptive.arp.common.webview.Utils;
import me.adaptive.arp.core.net.CustomStreamHandler;
import me.adaptive.arp.core.net.WebChromeClient;
import me.adaptive.arp.core.net.WebViewClient;
import me.adaptive.arp.impl.AccelerationDelegate;
import me.adaptive.arp.impl.AppContextDelegate;
import me.adaptive.arp.impl.AppContextWebviewDelegate;
import me.adaptive.arp.impl.BrowserDelegate;
import me.adaptive.arp.impl.CapabilitiesDelegate;
import me.adaptive.arp.impl.ContactDelegate;
import me.adaptive.arp.impl.DatabaseDelegate;
import me.adaptive.arp.impl.DeviceDelegate;
import me.adaptive.arp.impl.DisplayDelegate;
import me.adaptive.arp.impl.FileDelegate;
import me.adaptive.arp.impl.FileSystemDelegate;
import me.adaptive.arp.impl.GeolocationDelegate;
import me.adaptive.arp.impl.GlobalizationDelegate;
import me.adaptive.arp.impl.LifecycleDelegate;
import me.adaptive.arp.impl.LoggingDelegate;
import me.adaptive.arp.impl.MailDelegate;
import me.adaptive.arp.impl.MessagingDelegate;
import me.adaptive.arp.impl.NetworkReachabilityDelegate;
import me.adaptive.arp.impl.NetworkStatusDelegate;
import me.adaptive.arp.impl.OSDelegate;
import me.adaptive.arp.impl.RuntimeDelegate;
import me.adaptive.arp.impl.SecurityDelegate;
import me.adaptive.arp.impl.ServiceDelegate;
import me.adaptive.arp.impl.TelephonyDelegate;
import me.adaptive.arp.impl.VideoDelegate;


public class MainActivity extends Activity {

    // Logger
    private static final String LOG_TAG = "MainActivity";
    private ILogging logger;

    // Orientation listener
    private OrientationEventListener orientationEventListener;

    // Webview
    private WebView webView;

    // context
    private Context context;

    static {
        // URLStreamHandler substitution: Lazy loading of the stream handlers
        URLStreamHandler httpHandler = null;
        URLStreamHandler httpsHandler = null;
        try {
            httpHandler = (URLStreamHandler) Class.forName("com.android.okhttp.HttpHandler").newInstance();
            httpsHandler = (URLStreamHandler) Class.forName("com.android.okhttp.HttpsHandler").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Hashtable<String, URLStreamHandler> handlers = Utils.getURLStreamHandlers();

        // Setting of the custom stream handlers
        handlers.put("http", new CustomStreamHandler(httpHandler));
        handlers.put("https", new CustomStreamHandler(httpsHandler));
    }


    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register Logging delegate
        AppRegistryBridge.getInstance().getLoggingBridge().setDelegate(new LoggingDelegate());
        logger = AppRegistryBridge.getInstance().getLoggingBridge();

        // Register the application delegates
        AppRegistryBridge.getInstance().getPlatformContext().setDelegate(new AppContextDelegate(this));
        AppRegistryBridge.getInstance().getPlatformContextWeb().setDelegate(new AppContextWebviewDelegate());
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();

        // Register all the delegates
        AppRegistryBridge.getInstance().getAccelerationBridge().setDelegate(new AccelerationDelegate());
        AppRegistryBridge.getInstance().getBrowserBridge().setDelegate(new BrowserDelegate());
        AppRegistryBridge.getInstance().getCapabilitiesBridge().setDelegate(new CapabilitiesDelegate());
        AppRegistryBridge.getInstance().getContactBridge().setDelegate(new ContactDelegate());
        AppRegistryBridge.getInstance().getDatabaseBridge().setDelegate(new DatabaseDelegate());
        AppRegistryBridge.getInstance().getDeviceBridge().setDelegate(new DeviceDelegate());
        AppRegistryBridge.getInstance().getDisplayBridge().setDelegate(new DisplayDelegate());
        AppRegistryBridge.getInstance().getFileBridge().setDelegate(new FileDelegate());
        AppRegistryBridge.getInstance().getFileSystemBridge().setDelegate(new FileSystemDelegate());
        AppRegistryBridge.getInstance().getGeolocationBridge().setDelegate(new GeolocationDelegate());
        AppRegistryBridge.getInstance().getGlobalizationBridge().setDelegate(new GlobalizationDelegate());
        AppRegistryBridge.getInstance().getLifecycleBridge().setDelegate(new LifecycleDelegate());
        AppRegistryBridge.getInstance().getMailBridge().setDelegate(new MailDelegate());
        AppRegistryBridge.getInstance().getMessagingBridge().setDelegate(new MessagingDelegate());
        AppRegistryBridge.getInstance().getNetworkReachabilityBridge().setDelegate(new NetworkReachabilityDelegate());
        AppRegistryBridge.getInstance().getNetworkStatusBridge().setDelegate(new NetworkStatusDelegate());
        AppRegistryBridge.getInstance().getOSBridge().setDelegate(new OSDelegate());
        AppRegistryBridge.getInstance().getRuntimeBridge().setDelegate(new RuntimeDelegate());
        AppRegistryBridge.getInstance().getSecurityBridge().setDelegate(new SecurityDelegate());
        AppRegistryBridge.getInstance().getServiceBridge().setDelegate(new ServiceDelegate());
        AppRegistryBridge.getInstance().getTelephonyBridge().setDelegate(new TelephonyDelegate());
        AppRegistryBridge.getInstance().getVideoBridge().setDelegate(new VideoDelegate());

        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onCreate()");
        ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).updateLifecycleListeners(LifecycleState.Starting);

        // Webview initialization
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Save the primary Webview reference
        ((AppContextWebviewDelegate) AppRegistryBridge.getInstance().getPlatformContextWeb().getDelegate()).setPrimaryView(webView);

        // webView settings
        Utils.setWebViewSettings(webView);

        // Load main page
        webView.loadUrl(context.getString(R.string.arp_url) + context.getString(R.string.arp_page));

        // Orientation listener
        orientationEventListener = new OrientationEventListener(getApplicationContext(), SensorManager.SENSOR_DELAY_UI) {

            @Override
            public void onOrientationChanged(int orientation) {

                // Device orientation listeners
                ((DeviceDelegate) AppRegistryBridge.getInstance().getDeviceBridge().getDelegate()).updateDeviceOrientationListeners();
                // Display orientation listeners
                ((DisplayDelegate) AppRegistryBridge.getInstance().getDisplayBridge().getDelegate()).updateDisplayOrientationListeners();
            }
        };
    }

    /**
     * Called after onCreate(Bundle) — or after onRestart() when the activity had been stopped, but
     * is now again being displayed to the user. It will be followed by onResume().
     */
    @Override
    protected void onStart() {
        super.onStart();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onStart()");
        ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).updateLifecycleListeners(LifecycleState.Started);
        ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).updateLifecycleListeners(LifecycleState.Running);

        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            logger.log(ILoggingLogLevel.Warn, LOG_TAG, "It's not possible to detect the device orientation changes");
        }
    }

    /**
     * Called after onStop() when the current activity is being re-displayed to the user (the user
     * has navigated back to it). It will be followed by onStart() and then onResume().
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onRestart()");
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to
     * start interacting with the user. This is a good place to begin animations, open
     * exclusive-access devices (such as the camera), etc.
     */
    @Override
    public void onResume() {
        super.onResume();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onResume()");
        ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).updateLifecycleListeners(LifecycleState.Resuming);

        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            logger.log(ILoggingLogLevel.Warn, LOG_TAG, "It's not possible to detect the device orientation changes");
        }
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into the background, but
     * has not (yet) been killed. The counterpart to onResume().
     */
    @Override
    public void onPause() {
        super.onPause();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onPause()");
        ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).updateLifecycleListeners(LifecycleState.Pausing);

        orientationEventListener.disable();
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state
     * can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle) (the Bundle populated
     * by this method will be passed to both).
     *
     * @param savedInstanceState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onSaveInstanceState()");

        // Save the state of the WebView
        webView.saveState(savedInstanceState);
    }

    /**
     * This method is called after onStart() when the activity is being re-initialized from a
     * previously saved state, given here in savedInstanceState.
     *
     * @param savedInstanceState the data most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onRestoreInstanceState()");

        // Restore the state of the WebView
        webView.restoreState(savedInstanceState);
    }

    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity. Note that this method may never be
     * called, in low memory situations where the system does not have enough memory to keep your
     * activity's process running after its onPause() method is called.
     */
    @Override
    public void onStop() {
        super.onStop();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onStop()");

        orientationEventListener.disable();
    }

    /**
     * Perform any final cleanup before an activity is destroyed. This can happen either because the
     * activity is finishing (someone called finish() on it, or because the system is temporarily
     * destroying this instance of the activity to save space.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onDestroy()");
        ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).updateLifecycleListeners(LifecycleState.Stopping);

        orientationEventListener.disable();
    }


    /**
     * Called by the system when the device configuration changes while your activity is running.
     * Note that this will only be called if you have selected configurations you would like to
     * handle with the configChanges attribute in your manifest.
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
