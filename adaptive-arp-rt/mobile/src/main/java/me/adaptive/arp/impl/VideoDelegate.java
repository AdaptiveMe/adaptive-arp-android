/**
 --| ADAPTIVE RUNTIME PLATFORM |----------------------------------------------------------------------------------------

 (C) Copyright 2013-2015 Carlos Lozano Diez t/a Adaptive.me <http://adaptive.me>.

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 . Unless required by appli-
 -cable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  License  for the specific language governing
 permissions and limitations under the License.

 Original author:

 * Carlos Lozano Diez
 <http://github.com/carloslozano>
 <http://twitter.com/adaptivecoder>
 <mailto:carlos@adaptive.me>

 Contributors:

 * Ferran Vila Conesa
 <http://github.com/fnva>
 <http://twitter.com/ferran_vila>
 <mailto:ferran.vila.conesa@gmail.com>

 * See source code files for contributors.

 Release:

 * @version v2.0.3

-------------------------------------------| aut inveniam viam aut faciam |--------------------------------------------
 */

package me.adaptive.arp.impl;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.List;

import arp.adaptive.me.videoplayer.VideoActivity;
import me.adaptive.arp.R;
import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseMediaDelegate;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IVideo;

import static me.adaptive.arp.impl.AppContextDelegate.getMainActivity;

/**
 * Interface for Managing the Video operations
 * Auto-generated implementation of IVideo specification.
 */
public class VideoDelegate extends BaseMediaDelegate implements IVideo {


    public static String APIService = "media";
    static LoggingDelegate Logger;
    private MediaPlayer mediaPlayer;

    /**
     * Default Constructor.
     */
    public VideoDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

    }


    /**
     * Play url video stream
     *
     * @param url of the video
     * @since ARP1.0
     */
    public void playStream(String url) {
        boolean result = false;

        Logger.log(ILoggingLogLevel.Debug, APIService, "playStream: url: " + url);
        Uri uri = Uri.parse(url);
        try {
            /*Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setType("video/*");
            //intent.setData(uri);
            intent.setDataAndType(uri, "video/mp4");
            if(isCallable(intent)){
                ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().startActivity(Intent.createChooser(intent, "Complete action using"));
            }else {
            mediaPlayer =  MediaPlayer.create((android.content.Context) AppRegistryBridge.getInstance().getPlatformContext().getDelegate().getContext(), uri);

                    mediaPlayer.setOnCompletionListener(onCompleteListener);
            mediaPlayer.setOnPreparedListener(onPrepareListener);
            mediaPlayer.setOnErrorListener(onErrorListener);
            mediaPlayer.setScreenOnWhilePlaying(true);

            mediaPlayer.prepareAsync(); // prepare async to not block main thread

            }

            final VideoView videoView = (VideoView) getMainActivity().findViewById(R.id.adaptivevideo);
            videoView.setVisibility(View.VISIBLE);
            videoView.bringToFront();
            videoView.setVideoURI(uri);
            MediaController mc = new MediaController(getMainActivity());
            videoView.setMediaController(mc);
            mc.show(0);
            videoView.requestFocus();
            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Logger.log(ILoggingLogLevel.Debug,"onCompletion");
                    videoView.setVisibility(View.GONE);
                    //videoView.invalidate();
                }
            });*/
            Intent intent = new Intent((android.content.Context) AppRegistryBridge.getInstance().getPlatformContext().getContext(), VideoActivity.class);
            intent.putExtra("url", url);
            getMainActivity().startActivity(intent);
            getMainActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.Error, APIService, "playStream: Error " + ex.getLocalizedMessage());
        } finally {
            Logger.log(ILoggingLogLevel.Debug, APIService, "playStream: " + String.valueOf(result));
        }
    }


    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext().getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}
/**
 ------------------------------------| Engineered with ? in Barcelona, Catalonia |--------------------------------------
 */
