package me.adaptive.arp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ICapabilitiesButton;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.LifecycleState;
import me.adaptive.arp.common.webview.Utils;
import me.adaptive.arp.core.WebChromeClient;
import me.adaptive.arp.core.WebViewClient;
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


    // Webview
    private WebView webView;

    // context
    private Context context;

    private Dialog splashDialog;

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
        buildDialog();
        showSplashDialog();
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


        // Webview initialization
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Save the primary Webview reference
        ((AppContextWebviewDelegate) AppRegistryBridge.getInstance().getPlatformContextWeb().getDelegate()).setPrimaryView(webView);

        // webView settings
        Utils.setWebViewSettings(webView);

        LifecycleDelegate lifecycleDelegate = ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate());
        lifecycleDelegate.updateLifecycleListeners(LifecycleState.Starting);
        lifecycleDelegate.updateBackground(false);

        // Load main page
        webView.loadUrl(context.getString(R.string.arp_url) + context.getString(R.string.arp_page));


        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onCreate()");


    }

    /**
     * Called after onCreate(Bundle) — or after onRestart() when the activity had been stopped, but
     * is now again being displayed to the user. It will be followed by onResume().
     */
    @Override
    protected void onStart() {
        super.onStart();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onStart()");
        LifecycleDelegate lifecycleDelegate = ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate());
        lifecycleDelegate.updateLifecycleListeners(LifecycleState.Started);
        lifecycleDelegate.updateLifecycleListeners(LifecycleState.Running);
        lifecycleDelegate.updateBackground(false);


    }

    /**
     * Called after onStop() when the current activity is being re-displayed to the user (the user
     * has navigated back to it). It will be followed by onStart() and then onResume().
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onRestart()");
        LifecycleDelegate lifecycleDelegate = ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate());
        lifecycleDelegate.updateBackground(false);
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
        LifecycleDelegate lifecycleDelegate = ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate());
        lifecycleDelegate.updateLifecycleListeners(LifecycleState.Resuming);
        lifecycleDelegate.updateBackground(false);


    }

    /**
     * Called as part of the activity lifecycle when an activity is going into the background, but
     * has not (yet) been killed. The counterpart to onResume().
     */
    @Override
    public void onPause() {
        super.onPause();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "onPause()");
        LifecycleDelegate lifecycleDelegate = ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate());
        lifecycleDelegate.updateLifecycleListeners(LifecycleState.Pausing);
        lifecycleDelegate.updateBackground(true);


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
        LifecycleDelegate lifecycleDelegate = ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate());
        lifecycleDelegate.updateBackground(true);


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
        LifecycleDelegate lifecycleDelegate = ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate());
        lifecycleDelegate.updateLifecycleListeners(LifecycleState.Stopping);
        lifecycleDelegate.updateBackground(true);


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
        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        switch (display.getRotation()){
            case Surface.ROTATION_0:
                logger.log(ILoggingLogLevel.Debug,LOG_TAG, "ROTATION 0");
                break;
            case Surface.ROTATION_90:
                logger.log(ILoggingLogLevel.Debug,LOG_TAG, "ROTATION 90");
                break;
            case Surface.ROTATION_180:
                logger.log(ILoggingLogLevel.Debug,LOG_TAG, "ROTATION 180");
                break;
            case Surface.ROTATION_270:
                logger.log(ILoggingLogLevel.Debug,LOG_TAG, "ROTATION 270");
        }


        // Device orientation listeners
        ((DeviceDelegate) AppRegistryBridge.getInstance().getDeviceBridge().getDelegate()).updateDeviceOrientationListeners();
        // Display orientation listeners
        ((DisplayDelegate) AppRegistryBridge.getInstance().getDisplayBridge().getDelegate()).updateDisplayOrientationListeners();
    }

    /**
     * Called when a key was pressed down and not handled by any of the views
     * inside of the activity. So, for example, key presses while the cursor
     * is inside a TextView will not trigger the event (unless it is a navigation
     * to another object) because TextView handles its own key presses.
     * @param keyCode
     * @param event
     * @return Return <code>true</code> to prevent this event from being propagated
     * further, or <code>false</code> to indicate that you have not handled
     * this event and it should continue to be propagated.
     * @see #onKeyUp
     * @see android.view.KeyEvent
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode){
            case KeyEvent.KEYCODE_HOME:
                ((DeviceDelegate) AppRegistryBridge.getInstance().getDeviceBridge().getDelegate()).fireButtonsListeners(ICapabilitiesButton.HomeButton);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Called when a context menu for the {@code view} is about to be shown.
     * <p/>
     * Use {@link #onContextItemSelected(android.view.MenuItem)} to know when an
     * item has been selected.
     * <p/>
     * It is not safe to hold onto the context menu after this method returns.
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //super.onCreateContextMenu(menu, v, menuInfo);
        ((DeviceDelegate) AppRegistryBridge.getInstance().getDeviceBridge().getDelegate()).fireButtonsListeners(ICapabilitiesButton.OptionButton);
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ((DeviceDelegate) AppRegistryBridge.getInstance().getDeviceBridge().getDelegate()).fireButtonsListeners(ICapabilitiesButton.BackButton);
    }


    /**
     * Create the splash Dialog
     */
    private void buildDialog(){
        splashDialog = new Dialog(this,android.R.style.Theme_NoTitleBar_Fullscreen);
        splashDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        splashDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        splashDialog.setContentView(R.layout.splash_layout);
        splashDialog.setCancelable(false);
    }

    /**
     * Shows the spash splashDialog
     */
    private void showSplashDialog(){
        splashDialog.show();
    }

    /**
     * Returns the SplashDialog
     * @return SplashDialog
     */
    public Dialog getSplashDialog() {
        return splashDialog;
    }

}
