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

import android.app.Activity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.adaptive.arp.api.IAppContext;
import me.adaptive.arp.api.IOSType;

/**
 * Interface for context management purposes
 * Auto-generated implementation of IAppContext specification.
 */
public class AppContextDelegate implements IAppContext {

    // Main activity of the application
    private Activity activity;

    // Executor service of the application
    private ExecutorService executor;

    /**
     * Constructor with context
     *
     * @param activity Main context of the application
     */
    public AppContextDelegate(Activity activity) {
        super();
        this.activity = activity;
        this.executor = Executors.newFixedThreadPool(50);
    }

    /**
     * Constructor with context and executor
     *
     * @param activity Main context of the application
     * @param executor Executor of the application
     */
    public AppContextDelegate(Activity activity, ExecutorService executor) {
        super();
        this.activity = activity;
        this.executor = executor;
    }

    /**
     * The main application context. This should be cast to the platform specific implementation.
     *
     * @return Object representing the specific singleton application context provided by the OS.
     * @since ARP1.0
     */
    public Object getContext() {
        return activity.getApplicationContext();
    }

    /**
     * The type of context provided by the getContext method.
     *
     * @return Type of platform context.
     * @since ARP1.0
     */
    public IOSType getContextType() {
        return IOSType.Android;
    }

    /**
     * Getter for the executor service
     *
     * @return Executor service
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Main activity getter
     *
     * @return Main activity
     */
    public Activity getActivity() {
        return activity;
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
