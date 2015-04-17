package me.adaptive.arp;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.common.webview.Utils;

/**
 * Custom Activity to show an internal browser inside an Adaptive ARP Application
 */
public class BrowserActivity extends Activity {

    // Logger
    private static final String LOG_TAG = "BrowserActivity";
    private static ILogging logger;

    // Internal webView (layout defined)
    private WebView webView = null;

    private String url;
    private String title;
    private boolean modal = false;
    /**
     * Default Constructor.
     */
    public BrowserActivity() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
    }

    /**
     * This method overrides the MainActivity method to set up the actual window for the popup.
     * This is really the only method needed to turn the app into popup form. Any other methods would change the behavior of the UI.
     * Call this method at the beginning of the main activity.
     * You can't call setContentView(...) before calling the window service because it will throw an error every time.
     */

    public void setUpWindow() {

        // Creates the layout for the window and the look of it
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Params for the window.
        // You can easily set the alpha and the dim behind the window from here
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;    // lower than one makes it more transparent
        params.dimAmount = 0f;  // set it higher if you want to dim behind the window
        getWindow().setAttributes(params);

        // Gets the display size so that you can set the window to a percent of that
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // You could also easily used an integer value from the shared preferences to set the percent
        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * .7));
        } else {
            getWindow().setLayout((int) (width * .7), (int) (height * .8));
        }
        setTheme(R.style.Theme_UserModal);
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

        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        modal = getIntent().getBooleanExtra("modal",false);

        logger.log(ILoggingLogLevel.Info, LOG_TAG, "Stating Browser Activity with url: " + url);

        // animation
        overridePendingTransition(R.anim.slide_up, R.anim.fade_out);

        if(modal) {
            setUpWindow();
        }
        setContentView(R.layout.activity_browser);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // title
        setTitle(title);

        // webView settings
        webView = (WebView) findViewById(R.id.webView);
        Utils.setWebViewSettings(webView, BuildConfig.DEBUG);



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
