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
import me.adaptive.arp.api.ILifecycle;
import me.adaptive.arp.api.ILifecycleListener;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Interface for Managing the Lifecycle listeners
 * Auto-generated implementation of ILifecycle specification.
 */
public class LifecycleDelegate extends BaseApplicationDelegate implements ILifecycle {


    public static String APIService = "lifecycle";
    static LoggingDelegate Logger;
    public List<ILifecycleListener> listeners = new ArrayList<ILifecycleListener>();

    public List<ILifecycleListener> getListeners() {
        return listeners;
    }

    /**
     * Default Constructor.
     */
    public LifecycleDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

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
            Logger.log(ILoggingLogLevel.Debug, APIService, "addLifecycleListener: " + listener.toString() + " Added!");
        } else
            Logger.log(ILoggingLogLevel.Warn, APIService, "addLifecycleListener: " + listener.toString() + " is already added!");
    }
    private boolean isBackground;

    public void setBackground(boolean isBackground) {
        this.isBackground = isBackground;
    }

    /**
     * Whether the application is in background or not
     *
     * @return true if the application is in background;false otherwise
     * @since ARP1.0
     */
    public boolean isBackground() {
        /* check with the first task(task in the foreground)
        // in the returned list of tasks
        Context context = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        return !services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(context.getPackageName().toString());
                */
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
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeLifecycleListener" + listener.toString() + " Removed!");
        } else
            Logger.log(ILoggingLogLevel.Warn, APIService, "removeLifecycleListener: " + listener.toString() + " is NOT registered");

    }

    /**
     * Removes all existing listeners from receiving lifecycle events.
     *
     * @since ARP1.0
     */
    public void removeLifecycleListeners() {
        listeners.clear();
        Logger.log(ILoggingLogLevel.Debug, APIService, "removeLifecycleListeners: ALL LifecycleListener have been removed!");
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
