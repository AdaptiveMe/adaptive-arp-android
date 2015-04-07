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
import java.util.Date;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseSystemDelegate;
import me.adaptive.arp.api.ICapabilitiesOrientation;
import me.adaptive.arp.api.IDisplay;
import me.adaptive.arp.api.IDisplayOrientationListener;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.RotationEvent;
import me.adaptive.arp.api.RotationEventState;

/**
 * Interface for Managing the Display operations
 * Auto-generated implementation of IDisplay specification.
 */
public class DisplayDelegate extends BaseSystemDelegate implements IDisplay {

    // logger
    private static final String LOG_TAG = "DisplayDelegate";
    private ILogging logger;

    // Listeners
    private List<IDisplayOrientationListener> displayOrientationListeners;

    // Context
    private Context context;

    /**
     * Default Constructor.
     */
    public DisplayDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        displayOrientationListeners = new ArrayList<>();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
    }

    /**
     * Add a listener to start receiving display orientation change events.
     *
     * @param listener Listener to add to receive orientation change events.
     * @since v2.0.5
     */
    public void addDisplayOrientationListener(IDisplayOrientationListener listener) {

        if (!displayOrientationListeners.contains(listener)) {
            displayOrientationListeners.add(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "addDisplayOrientationListener: " + listener.toString() + " added!");
        } else
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "addDisplayOrientationListener: " + listener.toString() + " is already added!");
    }

    /**
     * Returns the current orientation of the display. Please note that this may be different from the orientation
     * of the device. For device orientation, use the IDevice APIs.
     *
     * @return The current orientation of the display.
     * @since v2.0.5
     */
    public ICapabilitiesOrientation getOrientationCurrent() {

        Configuration config = context.getResources().getConfiguration();

        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "getOrientationCurrent: " + config.toString());

        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                return ICapabilitiesOrientation.PortraitUp;
            case Configuration.ORIENTATION_PORTRAIT:
                return ICapabilitiesOrientation.LandscapeLeft;
            default:
                return ICapabilitiesOrientation.Unknown;
        }
    }

    /**
     * Remove a listener to stop receiving display orientation change events.
     *
     * @param listener Listener to remove from receiving orientation change events.
     * @since v2.0.5
     */
    public void removeDisplayOrientationListener(IDisplayOrientationListener listener) {

        if (displayOrientationListeners.contains(listener)) {
            displayOrientationListeners.remove(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeDisplayOrientationListener: " + listener.toString() + " removed!");
        } else
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "removeDisplayOrientationListener: " + listener.toString() + " is not registered");
    }

    /**
     * Remove all listeners receiving display orientation events.
     *
     * @since v2.0.5
     */
    public void removeDisplayOrientationListeners() {

        displayOrientationListeners.clear();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeDisplayOrientationListeners: all listeners have been removed!");
    }

    /**
     * Public method called for update the current state of all the listeners registered
     */
    public void updateDisplayOrientationListeners() {

        for (IDisplayOrientationListener listener : displayOrientationListeners) {
            listener.onResult(new RotationEvent(ICapabilitiesOrientation.Unknown, this.getOrientationCurrent(),
                    RotationEventState.DidFinishRotation, new Date().getTime()));
        }
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
