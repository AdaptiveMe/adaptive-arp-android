package me.adaptive.arp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {

    ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_slide_in,R.anim.fade_out);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getActionBar().hide();

        setContentView(R.layout.activity_video);

        //progDailog = ProgressDialog.show(this, "Please wait ...", "Retrieving data ...", true);
        progDailog = new ProgressDialog(VideoActivity.this,R.style.MyTheme);
        progDailog.setCancelable(false);
        progDailog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progDailog.show();

        Uri uri = Uri.parse(getIntent().getStringExtra("url"));
        final VideoView videoView = (VideoView) findViewById(R.id.adaptivevideo);

        videoView.setVideoURI(uri);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        mc.show();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("Debug", "onCompletion");
                finishVideo();
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("Debug", "onPrepared");
                progDailog.hide();
                progDailog.dismiss();

                videoView.start();

            }
        });
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        finishVideo();
        //super.onBackPressed();
    }

    private void finishVideo() {
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.right_slide_out);
    }

}
