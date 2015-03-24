package me.adaptive.arp;


import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        Uri uri = Uri.parse(getIntent().getStringExtra("url"));
        final VideoView videoView = (VideoView) findViewById(R.id.adaptivevideo);
        videoView.setVisibility(View.VISIBLE);
        videoView.bringToFront();
        videoView.setVideoURI(uri);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        mc.show();
        videoView.requestFocus();
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("Debug", "onCompletion");

                finishVideo();
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
        super.onBackPressed();
        finishVideo();
    }

    private void finishVideo() {
        finish();
        overridePendingTransition(R.anim.right_slide_out, R.anim.right_slide_in);
    }

}
