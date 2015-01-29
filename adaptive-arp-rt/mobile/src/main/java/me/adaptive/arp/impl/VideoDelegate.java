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
import android.webkit.MimeTypeMap;

import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IVideo;

/**
 * Interface for Managing the Video operations
 * Auto-generated implementation of IVideo specification.
 */
public class VideoDelegate extends BaseMediaDelegate implements IVideo {


    public static String APIService = "media";
    static LoggingDelegate Logger;
    private MediaPlayer mp;

    /**
     * Default Constructor.
     */
    public VideoDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * Play url video stream
     *
     * @param url of the video
     * @since ARP1.0
     */
    public void playStream(String url) {
        boolean result = false;

        Logger.log(ILoggingLogLevel.DEBUG, APIService, "playStream: url: " + url);

        try {
            String mimeType = getMimeType(url);
            if (mimeType.startsWith("video/")) {

                // start activity
                Uri uri = Uri.parse(url);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "video/mp4");

                if (isCallable(intent)) {
                    AppContextDelegate.getMainActivity().startActivity(intent);
                } else {
                    Logger.log(ILoggingLogLevel.ERROR, APIService, "NOT callable");
                }
            } else {
                Uri uri = Uri.parse(url);

                createMediaPlayer(uri);
                mp.prepareAsync();
                mp.start();

                result = true;
            }

        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "playStream: Error " + ex.getLocalizedMessage());
        } finally {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "playStream: " + String.valueOf(result));
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = AppContextDelegate.getMainActivity().getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void createMediaPlayer(Uri uri) {
        if (uri == null) {
            mp = new MediaPlayer();
        } else {
            mp = MediaPlayer.create(AppContextDelegate.getMainActivity().getApplicationContext(), uri);
        }
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
