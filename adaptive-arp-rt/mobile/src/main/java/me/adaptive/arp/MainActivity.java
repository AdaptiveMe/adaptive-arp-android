package me.adaptive.arp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Date;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ContactBridge;
import me.adaptive.arp.api.ICapabilitiesOrientation;
import me.adaptive.arp.api.IDeviceOrientationListener;
import me.adaptive.arp.api.IDisplayOrientationListener;
import me.adaptive.arp.api.ILifecycleListener;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.Lifecycle;
import me.adaptive.arp.api.LifecycleBridge;
import me.adaptive.arp.api.LifecycleState;
import me.adaptive.arp.api.LoggingBridge;
import me.adaptive.arp.api.RotationEvent;
import me.adaptive.arp.api.RotationEventState;
import me.adaptive.arp.impl.AccelerationDelegate;
import me.adaptive.arp.impl.AppContextDelegate;
import me.adaptive.arp.impl.BrowserDelegate;
import me.adaptive.arp.impl.CapabilitiesDelegate;
import me.adaptive.arp.impl.ContactDelegate;
import me.adaptive.arp.impl.DatabaseDelegate;
import me.adaptive.arp.impl.DeviceDelegate;
import me.adaptive.arp.impl.DisplayDelegate;
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

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;


public class MainActivity extends Activity {

    OrientationEventListener orientationEventListener;
    LoggingBridge Logger = AppRegistryBridge.getInstance().getLoggingBridge();
    LifecycleBridge lifecycle = AppRegistryBridge.getInstance().getLifecycleBridge();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppRegistryBridge.getInstance().getPlatformContext().setDelegate(new AppContextDelegate(this));

        Logger.setDelegate(new LoggingDelegate());
        lifecycle.setDelegate(new LifecycleDelegate());
        final ContactBridge contactBridge = AppRegistryBridge.getInstance().getContactBridge();
        contactBridge.setDelegate(new ContactDelegate());

        AppRegistryBridge.getInstance().getAccelerationBridge().setDelegate(new AccelerationDelegate());
        AppRegistryBridge.getInstance().getBrowserBridge().setDelegate(new BrowserDelegate());
        AppRegistryBridge.getInstance().getCapabilitiesBridge().setDelegate(new CapabilitiesDelegate());
        AppRegistryBridge.getInstance().getDatabaseBridge().setDelegate(new DatabaseDelegate());
        AppRegistryBridge.getInstance().getDeviceBridge().setDelegate(new DeviceDelegate());
        AppRegistryBridge.getInstance().getGeolocationBridge().setDelegate(new GeolocationDelegate());
        AppRegistryBridge.getInstance().getGlobalizationBridge().setDelegate(new GlobalizationDelegate());
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebChromeClient(new WebChromeClient() {
            /**
             * Tell the host application the current progress of loading a page.
             *
             * @param view        The WebView that initiated the callback.
             * @param newProgress Current page loading progress, represented by
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Toast.makeText(getApplicationContext(), "onProgressChanged", Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onProgressChanged");
                super.onProgressChanged(view, newProgress);
            }

            /**
             * Tell the client to display a javascript alert dialog.  If the client
             * returns true, WebView will assume that the client will handle the
             * dialog.  If the client returns false, it will continue execution.
             *
             * @param view    The WebView that initiated the callback.
             * @param url     The url of the page requesting the dialog.
             * @param message Message to be displayed in the window.
             * @param result  A JsResult to confirm that the user hit enter.
             * @return boolean Whether the client will handle the alert dialog.
             */
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                Toast.makeText(getApplicationContext(), "onJsAlert url[" + url + "] - Message[" + message + "]", Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onJsAlert");
                return super.onJsAlert(view, url, message, result);
            }

            /**
             * Tell the client to display a confirm dialog to the user. If the client
             * returns true, WebView will assume that the client will handle the
             * confirm dialog and call the appropriate JsResult method. If the
             * client returns false, a default value of false will be returned to
             * javascript. The default behavior is to return false.
             *
             * @param view    The WebView that initiated the callback.
             * @param url     The url of the page requesting the dialog.
             * @param message Message to be displayed in the window.
             * @param result  A JsResult used to send the user's response to
             *                javascript.
             * @return boolean Whether the client will handle the confirm dialog.
             */
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                Toast.makeText(getApplicationContext(), "onJsConfirm url[" + url + "] - Message[" + message + "]", Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onJsConfirm");
                return super.onJsConfirm(view, url, message, result);
            }

            /**
             * Tell the client to display a prompt dialog to the user. If the client
             * returns true, WebView will assume that the client will handle the
             * prompt dialog and call the appropriate JsPromptResult method. If the
             * client returns false, a default value of false will be returned to to
             * javascript. The default behavior is to return false.
             *
             * @param view         The WebView that initiated the callback.
             * @param url          The url of the page requesting the dialog.
             * @param message      Message to be displayed in the window.
             * @param defaultValue The default value displayed in the prompt dialog.
             * @param result       A JsPromptResult used to send the user's reponse to
             *                     javascript.
             * @return boolean Whether the client will handle the prompt dialog.
             */
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Toast.makeText(getApplicationContext(), "onJsPrompt url[" + url + "] - Message[" + message + "]", Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onJsPrompt");
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            /**
             * Notify the host application that web content from the specified origin
             * is attempting to use the Geolocation API, but no permission state is
             * currently set for that origin. The host application should invoke the
             * specified callback with the desired permission state. See
             * {@link android.webkit.GeolocationPermissions} for details.
             *
             * @param origin   The origin of the web content attempting to use the
             *                 Geolocation API.
             * @param callback The callback to use to set the permission state for the
             */
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                Toast.makeText(getApplicationContext(), "onGeolocationPermissionsShowPrompt origin[" + origin + "]", Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onGeolocationPermissionsShowPrompt");
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            /**
             * Report a JavaScript console message to the host application. The ChromeClient
             * should override this to process the log message as they see fit.
             *
             * @param consoleMessage Object containing details of the console message.
             * @return true if the message is handled by the client.
             */
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Toast.makeText(getApplicationContext(), "consoleMessage [" + consoleMessage.message() + "]", Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onConsoleMessage");
                return super.onConsoleMessage(consoleMessage);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onJsConfirm");
            }

            /**
             * Notify the host application of a resource request and allow the
             * application to return the data.  If the return value is null, the WebView
             * will continue to load the resource as usual.  Otherwise, the return
             * response and data will be used.  NOTE: This method is called on a thread
             * other than the UI thread so clients should exercise caution
             * when accessing private data or the view system.
             *
             * @param view    The {@link android.webkit.WebView} that is requesting the
             *                resource.
             * @param request Object containing the details of the request.
             * @return A {@link android.webkit.WebResourceResponse} containing the
             * response information or null if the WebView should load the
             * resource itself.
             */
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "shouldInterceptRequest");
                return super.shouldInterceptRequest(view, request);
            }

            /**
             * Notify the host application that a page has finished loading. This method
             * is called only for main frame. When onPageFinished() is called, the
             * rendering picture may not be updated yet. To get the notification for the
             * new Picture, use {@link android.webkit.WebView.PictureListener#onNewPicture}.
             *
             * @param view The WebView that is initiating the callback.
             * @param url  The url of the page.
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(getApplicationContext(), "Loaded: " + url, Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onPageFinished");
                super.onPageFinished(view, url);

            }

            /**
             * Notify the host application that a page has started loading. This method
             * is called once for each main frame load so a page with iframes or
             * framesets will call onPageStarted one time for the main frame. This also
             * means that onPageStarted will not be called when the contents of an
             * embedded frame changes, i.e. clicking a link whose target is an iframe.
             *
             * @param view    The WebView that is initiating the callback.
             * @param url     The url to be loaded.
             * @param favicon The favicon for this page if it already exists in the
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Toast.makeText(getApplicationContext(), "Loading: " + url, Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onPageStarted");
                super.onPageStarted(view, url, favicon);
            }

            /**
             * Give the host application a chance to handle the key event synchronously.
             * e.g. menu shortcut key events need to be filtered this way. If return
             * true, WebView will not handle the key event. If return false, WebView
             * will always handle the key event, so none of the super in the view chain
             * will see the key event. The default behavior returns false.
             *
             * @param view  The WebView that is initiating the callback.
             * @param event The key event.
             * @return True if the host application wants to handle the key event
             * itself, otherwise return false
             */
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Toast.makeText(getApplicationContext(), "Pressed: " + event.getKeyCode(), Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "shouldOverrideKeyEvent");
                return super.shouldOverrideKeyEvent(view, event);

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Toast.makeText(getApplicationContext(), "Load: " + url, Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "onJsConfirm");
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*if (!Uri.parse(url).getHost().equals("www.oreilly.com")) {
                    return false;
                }
                view.loadUrl(url);*/
                Toast.makeText(getApplicationContext(), "shouldOverrideUrlLoading: " + url, Toast.LENGTH_SHORT).show();
                Logger.log(ILoggingLogLevel.Debug, "MainActivity", "shouldOverrideUrlLoading");
                return true;
            }

        });


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
        webView.addJavascriptInterface(new ShowcaseWebAppInterface(this), "AdaptiveShowcase");

        String url = "file:///android_asset/app/showcase.html";
        Logger.log(ILoggingLogLevel.Debug, "URL: " + url + " - file:///android_asset");
        webView.loadUrl(url);

        Logger.log(ILoggingLogLevel.Debug, "MainActivity", "TEST " + url);


        AppRegistryBridge.getInstance().getBrowserBridge().openInternalBrowser("http://www.google.com", "Google", "Adaptive.me!");

        orientationEventListener = new OrientationEventListener(getApplicationContext(), SensorManager.SENSOR_DELAY_UI) {
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
        LifecicleUpdate(LifecycleState.Running);
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Started);
    }

    private void LifecicleUpdate(LifecycleState state) {
        if (lifecycle.getDelegate() != null) {
            for (ILifecycleListener listener : ((LifecycleDelegate) AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).listeners) {
                listener.onResult(new Lifecycle(state, System.currentTimeMillis()));
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Running);


    }

    @Override
    public void onResume() {
        ((LifecycleDelegate) lifecycle.getDelegate()).setBackground(false);
        super.onResume();
        if (orientationEventListener.canDetectOrientation()) orientationEventListener.enable();
        LifecicleUpdate(LifecycleState.Resuming);
    }

    @Override
    public void onPause() {
        ((LifecycleDelegate) lifecycle.getDelegate()).setBackground(true);
        super.onPause();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Pausing);

        orientationEventListener.disable();
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
        LifecicleUpdate(LifecycleState.Stopping);
        orientationEventListener.disable();
    }

    @Override
    public void onDestroy() {
        super.onPause();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Stopping);
        orientationEventListener.disable();
    }

    private ICapabilitiesOrientation getOrientation(int orientation) {
        switch (orientation) {
            case ORIENTATION_LANDSCAPE:
                return ICapabilitiesOrientation.PortraitUp;
            case ORIENTATION_PORTRAIT:
                return ICapabilitiesOrientation.LandscapeLeft;
        }
        return ICapabilitiesOrientation.Unknown;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
