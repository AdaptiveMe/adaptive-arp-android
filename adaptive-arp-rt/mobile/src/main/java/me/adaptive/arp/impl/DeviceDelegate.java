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
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.DeviceInfo;
import me.adaptive.arp.api.IButtonListener;
import me.adaptive.arp.api.ICapabilitiesOrientation;
import me.adaptive.arp.api.IDevice;
import me.adaptive.arp.api.IDeviceOrientationListener;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.Locale;

/**
 * Interface for Managing the Device operations
 * Auto-generated implementation of IDevice specification.
 */
public class DeviceDelegate extends BaseSystemDelegate implements IDevice {


    public static String APIService = "device";
    static LoggingDelegate Logger;
    public List<IButtonListener> listeners = new ArrayList<IButtonListener>();
    public List<IDeviceOrientationListener> listenersOrientation = new ArrayList<IDeviceOrientationListener>();


    public List<IButtonListener> getListeners() {
        return listeners;
    }

    public List<IDeviceOrientationListener> getListenersOrientation() {
        return listenersOrientation;
    }

    /**
     * Default Constructor.
     */
    public DeviceDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
    }

    /**
     * Register a new listener that will receive button events.
     *
     * @param listener to be registered.
     * @since ARP1.0
     */
    public void addButtonListener(IButtonListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            Logger.log(ILoggingLogLevel.Debug, APIService, "addButtonListener: " + listener.toString() + " Added!");
        } else
            Logger.log(ILoggingLogLevel.Debug, APIService, "addButtonListener: " + listener.toString() + " is already added!");
    }

    /**
     * Add a listener to start receiving device orientation change events.
     *
     * @param listener Listener to add to receive orientation change events.
     * @since v2.0.5
     */
    @Override
    public void addDeviceOrientationListener(IDeviceOrientationListener listener) {
        if (!listenersOrientation.contains(listener)) {
            listenersOrientation.add(listener);
            Logger.log(ILoggingLogLevel.Debug, APIService, "addDeviceOrientationListener: " + listener.toString() + " Added!");
        } else
            Logger.log(ILoggingLogLevel.Debug, APIService, "addDeviceOrientationListener: " + listener.toString() + " is already added!");
    }

    /**
     * Returns the device information for the current device executing the runtime.
     *
     * @return DeviceInfo for the current device.
     * @since ARP1.0
     */
    public DeviceInfo getDeviceInfo() {
        TelephonyManager tManager = (TelephonyManager) ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getSystemService(((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext().TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();
        return new DeviceInfo(android.os.Build.DEVICE, android.os.Build.BOARD, android.os.Build.BRAND, uuid);
    }

    /**
     * Gets the current Locale for the device.
     *
     * @return The current Locale information.
     * @since ARP1.0
     */
    public Locale getLocaleCurrent() {
        String language = java.util.Locale.getDefault().getLanguage();
        String country = java.util.Locale.getDefault().getCountry();
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
        Context context = ((Context)AppRegistryBridge.getInstance().getPlatformContext().getDelegate().getContext());
        /*Configuration config = context.getResources().getConfiguration();
        switch(config.orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                return ICapabilitiesOrientation.Portrait_Up;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                return ICapabilitiesOrientation.Landscape_Left;
                break;
        }*/

        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
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
        if (listeners.contains(listener)) {
            listeners.remove(listener);
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeButtonListener: " + listener.toString() + " Removed!");
        } else
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeButtonListener: " + listener.toString() + " is NOT registered");
    }

    /**
     * Removed all existing listeners from receiving button events.
     *
     * @since ARP1.0
     */
    public void removeButtonListeners() {
        listeners.clear();
        Logger.log(ILoggingLogLevel.Debug, APIService, "removeButtonListeners: " + "ALL ButtonListeners have been removed!");
    }

    /**
     * Remove a listener to stop receiving device orientation change events.
     *
     * @param listener Listener to remove from receiving orientation change events.
     * @since v2.0.5
     */
    @Override
    public void removeDeviceOrientationListener(IDeviceOrientationListener listener) {
        if (listenersOrientation.contains(listener)) {
            listenersOrientation.remove(listener);
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeDeviceOrientationListener: " + listener.toString() + " Removed!");
        } else
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeDeviceOrientationListener: " + listener.toString() + " is NOT registered");
    }

    /**
     * Remove all listeners receiving device orientation events.
     *
     * @since v2.0.5
     */
    @Override
    public void removeDeviceOrientationListeners() {
        listenersOrientation.clear();
        Logger.log(ILoggingLogLevel.Debug, APIService, "removeDeviceOrientationListeners: " + "ALL DeviceOrientationListeners have been removed!");
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
