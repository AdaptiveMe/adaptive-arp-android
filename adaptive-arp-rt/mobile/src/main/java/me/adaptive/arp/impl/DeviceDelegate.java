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

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.Surface;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseSystemDelegate;
import me.adaptive.arp.api.DeviceInfo;
import me.adaptive.arp.api.IButtonListener;
import me.adaptive.arp.api.ICapabilitiesOrientation;
import me.adaptive.arp.api.IDevice;
import me.adaptive.arp.api.IDeviceOrientationListener;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.Locale;
import me.adaptive.arp.api.RotationEvent;
import me.adaptive.arp.api.RotationEventState;

/**
 * Interface for Managing the Device operations
 * Auto-generated implementation of IDevice specification.
 */
public class DeviceDelegate extends BaseSystemDelegate implements IDevice {

    // Logger
    private static final String LOG_TAG = "DeviceDelegate";
    private ILogging logger;

    // Listeners
    private List<IButtonListener> buttonListeners;
    private List<IDeviceOrientationListener> deviceOrientationListeners;

    // Context
    private Context context;

    /**
     * Default Constructor.
     */
    public DeviceDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        buttonListeners = new ArrayList<>();
        deviceOrientationListeners = new ArrayList<>();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
    }

    /**
     * Register a new listener that will receive button events.
     *
     * @param listener to be registered.
     * @since ARP1.0
     */
    public void addButtonListener(IButtonListener listener) {

        if (!buttonListeners.contains(listener)) {
            buttonListeners.add(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "addButtonListener: " + listener.toString() + " added!");
        } else
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "addButtonListener: " + listener.toString() + " is already added!");
    }

    /**
     * Add a listener to start receiving device orientation change events.
     *
     * @param listener Listener to add to receive orientation change events.
     * @since v2.0.5
     */
    @Override
    public void addDeviceOrientationListener(IDeviceOrientationListener listener) {

        if (!deviceOrientationListeners.contains(listener)) {
            deviceOrientationListeners.add(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "addDeviceOrientationListener: " + listener.toString() + " added!");
        } else
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "addDeviceOrientationListener: " + listener.toString() + " is already added!");
    }

    /**
     * Returns the device information for the current device executing the runtime.
     *
     * @return DeviceInfo for the current device.
     * @since ARP1.0
     */
    public DeviceInfo getDeviceInfo() {

        final TelephonyManager tManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        return new DeviceInfo(android.os.Build.DEVICE, android.os.Build.BOARD, android.os.Build.BRAND, tManager.getDeviceId());
    }

    /**
     * Gets the current Locale for the device.
     *
     * @return The current Locale information.
     * @since ARP1.0
     */
    public Locale getLocaleCurrent() {

        final String language = java.util.Locale.getDefault().getLanguage();
        final String country = java.util.Locale.getDefault().getCountry();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "getLocaleCurrent: " + language + ", " + country);

        return new Locale(country, language);
    }

    /**
     * Returns the current orientation of the device. Please note that this may be different from the orientation
     * of the display. For display orientation, use the IDisplay APIs.
     *
     * @return The current orientation of the device.
     * @since v2.0.5
     */
    @Override
    public ICapabilitiesOrientation getOrientationCurrent() {

        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "getOrientationCurrent: " + rotation);

        switch (rotation) {
            case Surface.ROTATION_0:
                return ICapabilitiesOrientation.PortraitUp;
            case Surface.ROTATION_90:
                return ICapabilitiesOrientation.LandscapeRight;
            case Surface.ROTATION_180:
                return ICapabilitiesOrientation.PortraitDown;
            case Surface.ROTATION_270:
                return ICapabilitiesOrientation.LandscapeLeft;
            default:
                return ICapabilitiesOrientation.Unknown;
        }

    }

    /**
     * De-registers an existing listener from receiving button events.
     *
     * @param listener to be removed.
     * @since ARP1.0
     */
    public void removeButtonListener(IButtonListener listener) {

        if (buttonListeners.contains(listener)) {
            buttonListeners.remove(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeButtonListener: " + listener.toString() + " Removed!");
        } else
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "removeButtonListener: " + listener.toString() + " is not registered");
    }

    /**
     * Removed all existing buttonListeners from receiving button events.
     *
     * @since ARP1.0
     */
    public void removeButtonListeners() {

        buttonListeners.clear();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeButtonListeners: all ButtonListeners have been removed!");
    }

    /**
     * Remove a listener to stop receiving device orientation change events.
     *
     * @param listener Listener to remove from receiving orientation change events.
     * @since v2.0.5
     */
    @Override
    public void removeDeviceOrientationListener(IDeviceOrientationListener listener) {

        if (deviceOrientationListeners.contains(listener)) {
            deviceOrientationListeners.remove(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeDeviceOrientationListener: " + listener.toString() + " removed!");
        } else
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "removeDeviceOrientationListener: " + listener.toString() + " is not registered");
    }

    /**
     * Remove all buttonListeners receiving device orientation events.
     *
     * @since v2.0.5
     */
    @Override
    public void removeDeviceOrientationListeners() {

        deviceOrientationListeners.clear();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeDeviceOrientationListeners: all DeviceOrientationListeners have been removed!");
    }

    /**
     * Public method called for update the current state of all the listeners registered
     */
    public void updateDeviceOrientationListeners() {

        for (IDeviceOrientationListener listener : deviceOrientationListeners) {
            listener.onResult(new RotationEvent(ICapabilitiesOrientation.Unknown, this.getOrientationCurrent(),
                    RotationEventState.DidFinishRotation, new Date().getTime()));
        }
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
