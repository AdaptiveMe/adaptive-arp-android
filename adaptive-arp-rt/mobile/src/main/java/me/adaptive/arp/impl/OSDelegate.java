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

import android.os.Build;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseSystemDelegate;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IOS;
import me.adaptive.arp.api.IOSType;
import me.adaptive.arp.api.OSInfo;

/**
 * Interface for Managing the OS operations
 * Auto-generated implementation of IOS specification.
 */
public class OSDelegate extends BaseSystemDelegate implements IOS {

    // logger
    private static final String LOG_TAG = "RuntimeDelegate";
    private ILogging logger;

    /**
     * Default Constructor.
     */
    public OSDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
    }

    /**
     * Returns the OSInfo for the current operating system.
     *
     * @return OSInfo with name, version and vendor of the OS.
     * @since ARP1.0
     */
    public OSInfo getOSInfo() {

        OSInfo os = new OSInfo(IOSType.Android, Build.VERSION.RELEASE, Build.MANUFACTURER);
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "Operating System: " + os);
        return os;
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
