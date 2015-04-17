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

import java.util.ArrayList;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseApplicationDelegate;
import me.adaptive.arp.api.ILifecycle;
import me.adaptive.arp.api.ILifecycleListener;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.Lifecycle;
import me.adaptive.arp.api.LifecycleState;

/**
 * Interface for Managing the Lifecycle listeners
 * Auto-generated implementation of ILifecycle specification.
 */
public class LifecycleDelegate extends BaseApplicationDelegate implements ILifecycle {

    // logger
    private static final String LOG_TAG = "LifecycleDelegate";
    private ILogging logger;

    // Listeners
    private List<ILifecycleListener> listeners;

    // Background variable
    private boolean isBackground = false;

    /**
     * Default Constructor.
     */
    public LifecycleDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        listeners = new ArrayList<>();
    }

    /**
     * Add the listener for the lifecycle of the app
     *
     * @param listener Lifecycle listener
     * @since ARP1.0
     */
    public void addLifecycleListener(ILifecycleListener listener) {

        if (!listeners.contains(listener)) {

            listeners.add(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "addLifecycleListener: " + listener.toString() + " added!");
        } else
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "addLifecycleListener: " + listener.toString() + " is already added!");
    }

    /**
     * Whether the application is in background or not
     *
     * @return true if the application is in background;false otherwise
     * @since ARP1.0
     */
    public boolean isBackground() {
        return this.isBackground;
    }

    /**
     * Un-registers an existing listener from receiving lifecycle events.
     *
     * @param listener Lifecycle listener
     * @since ARP1.0
     */
    public void removeLifecycleListener(ILifecycleListener listener) {

        if (listeners.contains(listener)) {
            listeners.remove(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeLifecycleListener" + listener.toString() + " removed!");
        } else
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "removeLifecycleListener: " + listener.toString() + " is not registered");

    }

    /**
     * Removes all existing listeners from receiving lifecycle events.
     *
     * @since ARP1.0
     */
    public void removeLifecycleListeners() {
        listeners.clear();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeLifecycleListeners: all LifecycleListener have been removed!");
    }

    /**
     * Method for updating the lifecycle listeners
     *
     * @param state State of the lifecycle
     */
    public void updateLifecycleListeners(LifecycleState state) {
        for (ILifecycleListener listener : this.listeners) {
            listener.onResult(new Lifecycle(state, System.currentTimeMillis()));
        }
    }

    /**
     * Method for updating the background state of the application
     *
     * @param isBackground True is application is in background, false otherwise
     */
    public void updateBackground(boolean isBackground) {
        this.isBackground = isBackground;
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
