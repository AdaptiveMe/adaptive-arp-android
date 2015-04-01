package me.adaptive.arp;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebView;

import me.adaptive.arp.api.AppRegistryBridge;
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

    /*OrientationEventListener orientationEventListener;
    LoggingBridge Logger = AppRegistryBridge.getInstance().getLoggingBridge();
    LifecycleBridge lifecycle = AppRegistryBridge.getInstance().getLifecycleBridge();*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register Logging delegate
        AppRegistryBridge.getInstance().getLoggingBridge().setDelegate(new LoggingDelegate());

        // Register the application delegates
        AppRegistryBridge.getInstance().getPlatformContext().setDelegate(new AppContextDelegate(this));
        AppRegistryBridge.getInstance().getPlatformContextWeb().setDelegate(new AppContextWebviewDelegate());

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
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // webView settings
        Utils.setWebViewSettings(webView);
        webView.loadUrl("https://adaptiveapp/index.html");


        //AppRegistryBridge.getInstance().getBrowserBridge().openInternalBrowser("http://www.google.com", "Google", "Adaptive.me!");

        /*orientationEventListener = new OrientationEventListener(getApplicationContext(), SensorManager.SENSOR_DELAY_UI) {
            public void onOrientationChanged(int orientation) {

                if (AppRegistryBridge.getInstance().getDisplayBridge().getDelegate() != null) {
                    for (IDisplayOrientationListener listener : ((DisplayDelegate) AppRegistryBridge
                            .getInstance().getDisplayBridge().getDelegate()).listeners) {
                        listener.onResult(new RotationEvent(ICapabilitiesOrientation.Unknown, getOrientation(orientation), RotationEventState.Unknown, new Date().getTime()));
                    }
                }

                ICapabilitiesOrientation state;
                final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
                switch (rotation) {
                    case Surface.ROTATION_0:
                        state = ICapabilitiesOrientation.PortraitUp;
                    case Surface.ROTATION_90:
                        state = ICapabilitiesOrientation.LandscapeRight;
                    case Surface.ROTATION_180:
                        state = ICapabilitiesOrientation.PortraitDown;
                    case Surface.ROTATION_270:
                        state = ICapabilitiesOrientation.LandscapeLeft;
                    default:
                        state = ICapabilitiesOrientation.Unknown;
                }
                if (AppRegistryBridge.getInstance().getDeviceBridge().getDelegate() != null) {
                    for (IDeviceOrientationListener listener : ((DeviceDelegate) AppRegistryBridge.getInstance().getDeviceBridge().getDelegate()).listenersOrientation) {
                        listener.onResult(new RotationEvent(null, state, RotationEventState.DidFinishRotation, new Date().getTime()));
                    }
                }
            }
        };
        if (orientationEventListener.canDetectOrientation()) orientationEventListener.enable();

        LifecicleUpdate(LifecycleState.Started);
        LifecicleUpdate(LifecycleState.Running);*/
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Started);
    }

    private void LifecicleUpdate(LifecycleState state) {
        /*if (lifecycle.getDelegate() != null) {
            for (ILifecycleListener listener : ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).listeners) {
                listener.onResult(new Lifecycle(state, System.currentTimeMillis()));
            }
        }*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        //LifecicleUpdate(LifecycleState.Running);


    }

    @Override
    public void onResume() {
        //((LifecycleDelegate) lifecycle.getDelegate()).setBackground(false);
        super.onResume();
        /*if (orientationEventListener.canDetectOrientation()) orientationEventListener.enable();
        LifecicleUpdate(LifecycleState.Resuming);*/
    }

    @Override
    public void onPause() {
        //((LifecycleDelegate) lifecycle.getDelegate()).setBackground(true);
        super.onPause();  // Always call the superclass method first

        /*LifecicleUpdate(LifecycleState.Pausing);

        orientationEventListener.disable();*/
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        /* Save the user's current game state
        savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);*/

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        /* Restore state members from saved instance
        mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
        mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);*/
    }

    @Override
    public void onStop() {
        super.onPause();  // Always call the superclass method first
        /*LifecicleUpdate(LifecycleState.Stopping);
        orientationEventListener.disable();*/
    }

    @Override
    public void onDestroy() {
        super.onPause();  // Always call the superclass method first

        /*LifecicleUpdate(LifecycleState.Stopping);
        orientationEventListener.disable();*/
    }

    /*private ICapabilitiesOrientation getOrientation(int orientation) {
        switch (orientation) {
            case ORIENTATION_LANDSCAPE:
                return ICapabilitiesOrientation.PortraitUp;
            case ORIENTATION_PORTRAIT:
                return ICapabilitiesOrientation.LandscapeLeft;
        }
        return ICapabilitiesOrientation.Unknown;
    }*/


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

}
