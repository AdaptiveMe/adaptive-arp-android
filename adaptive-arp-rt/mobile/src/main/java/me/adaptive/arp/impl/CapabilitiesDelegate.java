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

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.provider.MediaStore;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseSystemDelegate;
import me.adaptive.arp.api.ICapabilities;
import me.adaptive.arp.api.ICapabilitiesButton;
import me.adaptive.arp.api.ICapabilitiesCommunication;
import me.adaptive.arp.api.ICapabilitiesData;
import me.adaptive.arp.api.ICapabilitiesMedia;
import me.adaptive.arp.api.ICapabilitiesNet;
import me.adaptive.arp.api.ICapabilitiesNotification;
import me.adaptive.arp.api.ICapabilitiesOrientation;
import me.adaptive.arp.api.ICapabilitiesSensor;

/**
 * Interface for testing the Capabilities operations
 * Auto-generated implementation of ICapabilities specification.
 */
public class CapabilitiesDelegate extends BaseSystemDelegate implements ICapabilities {


    private final boolean tvDevice;
    public String APIService = "capabilities";

    /**
     * Default Constructor.
     */
    public CapabilitiesDelegate() {
        super();
        Context context = ((Context)AppRegistryBridge.getInstance().getPlatformContext().getContext());
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(context.UI_MODE_SERVICE);
        tvDevice = uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;

    }

    /**
     * Obtains the default orientation of the device/display. If no default orientation is available on
     * the platform, this method will return the current orientation. To capture device or display orientation
     * changes please use the IDevice and IDisplay functions and listeners API respectively.
     *
     * @return The default orientation for the device/display.
     * @since v2.0.5
     */
    @Override
    public ICapabilitiesOrientation getOrientationDefault() {
        return ICapabilitiesOrientation.PortraitUp;
    }

    /**
     * Provides the device/display orientations supported by the platform. A platform will usually
     * support at least one orientation. This is usually PortaitUp.
     *
     * @return The orientations supported by the device/display of the platform.
     * @since v2.0.5
     */
    @Override
    public ICapabilitiesOrientation[] getOrientationsSupported() {
        if (tvDevice) {
            return new ICapabilitiesOrientation[]{ICapabilitiesOrientation.PortraitUp};
        } else {
            return new ICapabilitiesOrientation[]{ICapabilitiesOrientation.PortraitUp, ICapabilitiesOrientation.LandscapeLeft,
                    ICapabilitiesOrientation.LandscapeRight, ICapabilitiesOrientation.PortraitDown};
        }
    }

    /**
     * Determines whether a specific hardware button is supported for interaction.
     *
     * @param type Type of feature to check.
     * @return true is supported, false otherwise.
     * @since ARP1.0
     */
    public boolean hasButtonSupport(ICapabilitiesButton type) {
        switch (type) {
            case BackButton:
            case HomeButton:
            case OptionButton:
                return !tvDevice;
            default:
                return false;
        }
    }

    /**
     * Determines whether a specific Communication capability is supported by
     * the device.
     *
     * @param type Type of feature to check.
     * @return true if supported, false otherwise.
     * @since ARP1.0
     */
    public boolean hasCommunicationSupport(ICapabilitiesCommunication type) {
        String capability = null;
        boolean supported = false;
        ActivityInfo activityInfo;
        Intent intent;
        PackageManager pm = ((Context)AppRegistryBridge.getInstance().getPlatformContext().getContext()).getPackageManager();
        //TODO CHECK LIVE CONNECTIVITY?
        switch (type) {
            case Calendar:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_CALENDAR);
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                break;
            case Contact:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_CONTACTS);
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                break;
            case Mail:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                break;
            case Messaging:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                break;
            case Telephony:
                //TODO CHECK ACTION_DIAL?
                capability = PackageManager.FEATURE_TELEPHONY;
                break;

        }
        if (capability != null && !capability.isEmpty())
            supported = pm.hasSystemFeature(capability);
        return supported;
    }

    /**
     * Determines whether a specific Data capability is supported by the device.
     *
     * @param type Type of feature to check.
     * @return true if supported, false otherwise.
     * @since ARP1.0
     */
    public boolean hasDataSupport(ICapabilitiesData type) {
        switch (type) {
            case Database:
                return !tvDevice;
            case File:
                return !tvDevice;
            case Cloud:
            default:
                return false;
        }
    }

    /**
     * Determines whether a specific Media capability is supported by the
     * device.
     *
     * @param type Type of feature to check.
     * @return true if supported, false otherwise.
     * @since ARP1.0
     */
    public boolean hasMediaSupport(ICapabilitiesMedia type) {
        String capability = null;
        boolean supported = false;
        ActivityInfo activityInfo;
        Intent intent = null;
        PackageManager pm = ((Context)AppRegistryBridge.getInstance().getPlatformContext().getContext()).getPackageManager();
        switch (type) {
            case AudioPlayback:
                //Whether has something to handle the request
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_MUSIC);
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                //Can output audio
                capability = PackageManager.FEATURE_AUDIO_OUTPUT;
                break;
            case AudioRecording:
                //Whether has something to handle the request
                intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                //Can output audio
                capability = PackageManager.FEATURE_AUDIO_OUTPUT;
                break;
            case Camera:
                //Whether has something to handle the request
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                //Can output audio
                capability = PackageManager.FEATURE_CAMERA_ANY;
                break;
            case VideoPlayback:
                intent = intent.setAction(Intent.ACTION_VIEW);
                //TODO CHECK CRASH URI NULL
                intent.setType("video/*");
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                break;
            case VideoRecording:
                intent = new Intent();
                intent.addCategory(MediaStore.ACTION_VIDEO_CAPTURE);
                activityInfo = intent.resolveActivityInfo(pm, intent.getFlags());
                if (activityInfo.exported) {
                    supported = true;
                }
                capability = PackageManager.FEATURE_CAMERA_ANY;
                break;
            default:

        }
        if (capability != null && !capability.isEmpty())
            supported = pm.hasSystemFeature(capability);
        return supported;
    }

    /**
     * Determines whether a specific Net capability is supported by the device.
     *
     * @param type Type of feature to check.
     * @return true if supported, false otherwise.
     * @since ARP1.0
     */
    public boolean hasNetSupport(ICapabilitiesNet type) {
        //TODO CHECK LIVE? is this is network connected?
        String capability = null;
        boolean supported = false;
        PackageManager pm = ((Context)AppRegistryBridge.getInstance().getPlatformContext().getContext()).getPackageManager();
        switch (type) {
            case GPRS:
            case GSM:
            case HSDPA:
            case LTE:
                //Assuming min api lvl 21, will return true but it is not true.
                return !tvDevice;
            case WIFI:
                capability = PackageManager.FEATURE_WIFI;
        }
        if (capability != null && !capability.isEmpty())
            supported = pm.hasSystemFeature(capability);
        return supported;
    }

    /**
     * Determines whether a specific Notification capability is supported by the
     * device.
     *
     * @param type Type of feature to check.
     * @return true if supported, false otherwise.
     * @since ARP1.0
     */
    public boolean hasNotificationSupport(ICapabilitiesNotification type) {
        return !tvDevice;
    }

    /**
     * Determines whether the device/display supports a given orientation.
     *
     * @param orientation Orientation type.
     * @return True if the given orientation is supported, false otherwise.
     * @since v2.0.5
     */
    @Override
    public boolean hasOrientationSupport(ICapabilitiesOrientation orientation) {
        return !tvDevice;
    }

    /**
     * Determines whether a specific Sensor capability is supported by the
     * device.
     *
     * @param type Type of feature to check.
     * @return true if supported, false otherwise.
     * @since ARP1.0
     */
    public boolean hasSensorSupport(ICapabilitiesSensor type) {
        String capability = null;
        boolean supported = false;
        PackageManager pm = ((Context)AppRegistryBridge.getInstance().getPlatformContext().getContext()).getPackageManager();
        switch (type) {
            case Accelerometer:
                capability = PackageManager.FEATURE_SENSOR_ACCELEROMETER;
                break;
            case AmbientLight:
                capability = PackageManager.FEATURE_SENSOR_LIGHT;
                break;
            case Geolocation:
                capability = PackageManager.FEATURE_LOCATION_GPS;
                break;
            case Barometer:
                capability = PackageManager.FEATURE_SENSOR_BAROMETER;
                break;
            case Gyroscope:
                capability = PackageManager.FEATURE_SENSOR_GYROSCOPE;
                break;
            case Magnetometer:
                capability = PackageManager.FEATURE_SENSOR_COMPASS;
                break;
            case Proximity:
                capability = PackageManager.FEATURE_SENSOR_PROXIMITY;
                break;
        }
        if (capability != null && !capability.isEmpty())
            supported = pm.hasSystemFeature(capability);
        return supported;
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
