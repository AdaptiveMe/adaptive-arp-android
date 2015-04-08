package me.adaptive.arp;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.common.webview.Utils;
import me.adaptive.arp.core.WebChromeClient;
import me.adaptive.arp.core.WebViewClient;

/**
 * Custom Activity to show an internal browser inside an Adaptive ARP Application
 */
public class BrowserActivity extends Activity {

    // Logger
    private static final String LOG_TAG = "BrowserActivity";
    private static ILogging logger;

    // Internal webView (layout defined)
    private WebView webView = null;

    /**
     * Default Constructor.
     */
    public BrowserActivity() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
    }

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        logger.log(ILoggingLogLevel.Info, LOG_TAG, "Stating Browser Activity with url: " + url);

        // animation
        overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
        setContentView(R.layout.activity_browser);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // title
        setTitle(title);

        // webView settings
        webView = (WebView) findViewById(R.id.webView);
        Utils.setWebViewSettings(webView);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl(url);
    }

    /**
     * This methods destroys the activity and removes from the view with an animation
     */
    private void selfDestruct() {

        logger.log(ILoggingLogLevel.Info, LOG_TAG, "Destroying Browser Activity");

        finish();
        // animation
        overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it
     * here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                selfDestruct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the activity has detected the user's press of the back key. The default
     * implementation simply finishes the current activity, but you can override this to do whatever
     * you want.
     */
    @Override
    public void onBackPressed() {

        // If webView has history, go back to the webView history, else, de
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            selfDestruct();
        }
    }
}
