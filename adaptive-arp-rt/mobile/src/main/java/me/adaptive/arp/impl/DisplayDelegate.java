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

 * @version v2.0.6

-------------------------------------------| aut inveniam viam aut faciam |--------------------------------------------
 */

package me.adaptive.arp.impl;

import android.content.Context;
import android.content.res.Configuration;

import java.util.ArrayList;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ICapabilitiesOrientation;
import me.adaptive.arp.api.IDisplay;
import me.adaptive.arp.api.IDisplayOrientationListener;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Interface for Managing the Display operations
 * Auto-generated implementation of IDisplay specification.
 */
public class DisplayDelegate extends BaseSystemDelegate implements IDisplay {

    public String APIService = "display";
    static LoggingDelegate Logger;
    public List<IDisplayOrientationListener> listeners = new ArrayList<>();
    /**
     * Default Constructor.
     */
    public DisplayDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
    }

    /**
     * Add a listener to start receiving display orientation change events.
     *
     * @param listener Listener to add to receive orientation change events.
     * @since v2.0.5
     */
    public void addDisplayOrientationListener(IDisplayOrientationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            Logger.log(ILoggingLogLevel.Debug, APIService, "addDisplayOrientationListener: " + listener.toString() + " Added!");
        } else
            Logger.log(ILoggingLogLevel.Debug, APIService, "addDisplayOrientationListener: " + listener.toString() + " is already added!");
    }

    /**
     * Returns the current orientation of the display. Please note that this may be different from the orientation
     * of the device. For device orientation, use the IDevice APIs.
     *
     * @return The current orientation of the display.
     * @since v2.0.5
     */
    public ICapabilitiesOrientation getOrientationCurrent() {
        Context context = ((Context)AppRegistryBridge.getInstance().getPlatformContext().getDelegate().getContext());
        Configuration config = context.getResources().getConfiguration();
        switch(config.orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                return ICapabilitiesOrientation.PortraitUp;
            case Configuration.ORIENTATION_PORTRAIT:
                return ICapabilitiesOrientation.LandscapeLeft;
        }
        return ICapabilitiesOrientation.Unknown;
    }

    /**
     * Remove a listener to stop receiving display orientation change events.
     *
     * @param listener Listener to remove from receiving orientation change events.
     * @since v2.0.5
     */
    public void removeDisplayOrientationListener(IDisplayOrientationListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeDisplayOrientationListener: " + listener.toString() + " Removed!");
        } else
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeDisplayOrientationListener: " + listener.toString() + " is NOT registered");
    }

    /**
     * Remove all listeners receiving display orientation events.
     *
     * @since v2.0.5
     */
    public void removeDisplayOrientationListeners() {
        listeners.clear();
        Logger.log(ILoggingLogLevel.Debug, APIService, "removeDisplayOrientationListeners: " + "ALL listeners have been removed!");
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
