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

import android.util.Log;

import me.adaptive.arp.BuildConfig;
import me.adaptive.arp.api.BaseUtilDelegate;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Interface for Managing the Logging operations
 * Auto-generated implementation of ILogging specification.
 */
public class LoggingDelegate extends BaseUtilDelegate implements ILogging {

    /**
     * Logs the given message, with the given log level if specified, to the standard platform/environment.
     *
     * @param level   Log level
     * @param message Message to be logged
     * @since ARP1.0
     */
    public void log(ILoggingLogLevel level, String message) {

        log(level, "GENERAL", message);
    }

    /**
     * Logs the given message, with the given log level if specified, to the standard platform/environment.
     *
     * @param level    Log level
     * @param category Category/tag name to identify/filter the log.
     * @param message  Message to be logged
     * @since ARP1.0
     */
    public void log(ILoggingLogLevel level, String category, String message) {

        switch (level) {
            case Debug:
                if (BuildConfig.DEBUG) {
                    Log.d("[DEBUG - " + category + "] ", message);
                }
                break;
            case Info:
                Log.i("[INFO - " + category + "] ", message);
                break;
            case Warn:
                Log.w("[WARN - " + category + "] ", message);
                break;
            case Error:
                Log.e("[ERROR - " + category + "] ", message);
                break;
        }
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
