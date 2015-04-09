package me.adaptive.arp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Custom Activity to show an internal video player inside an Adaptive ARP Application
 */
public class VideoActivity extends Activity {

    // Logger
    private static final String LOG_TAG = "VideoActivity";
    private static ILogging logger;

    // Dialog
    private ProgressDialog dialog = null;

    /**
     * Default Constructor.
     */
    public VideoActivity() {
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

        Uri uri = Uri.parse(getIntent().getStringExtra("url"));
        //TODO validate

        logger.log(ILoggingLogLevel.Info, LOG_TAG, "Stating Video Activity with uri: " + uri);

        // animation
        overridePendingTransition(R.anim.right_slide_in, R.anim.fade_out);
        setContentView(R.layout.activity_video);

        // remove title
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // dialog
        dialog = new ProgressDialog(this, R.style.MyTheme);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        dialog.show();

        // VideoView
        final VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(uri);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.show();

        // listener fired when the video is fully loaded
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "setOnPreparedListener video");
                dialog.hide();
                dialog.dismiss();
                videoView.start();
            }
        });

        // listener fired when the video finishes
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "setOnCompletionListener video");
                selfDestruct();
            }
        });
    }

    /**
     * This methods destroys the activity and removes from the view with an animation
     */
    private void selfDestruct() {
        logger.log(ILoggingLogLevel.Info, LOG_TAG, "Destroying Video Activity");
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.right_slide_out);
    }

    /**
     * Called when the activity has detected the user's press of the back key. The default
     * implementation simply finishes the current activity, but you can override this to do whatever
     * you want.
     */
    @Override
    public void onBackPressed() {
        selfDestruct();
    }

}
