package me.adaptive.arp.rt;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import me.adaptive.arp.impl.pim.ContactImpl;


public class MainActivity extends Activity {

    private WebView mWebView;
    private WebSettings webSettings;

    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        configureWebView(mWebView);
        mWebView.loadUrl("http://beta.html5test.com/");

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          runImplTest();
                                      }
                                  }
        );

        MainActivity.context = getApplicationContext();
    }

    private void runImplTest() {
        ContactImpl contact = new ContactImpl();
        //contact.searchContacts(null,new ContactResultCallbackImpl());
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    private void configureWebView(WebView webview) {
        webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
