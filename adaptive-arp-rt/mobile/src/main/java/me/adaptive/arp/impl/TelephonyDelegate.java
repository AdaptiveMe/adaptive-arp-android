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

import android.content.Intent;
import android.net.Uri;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.ITelephony;
import me.adaptive.arp.api.ITelephonyStatus;

/**
 * Interface for Managing the Telephony operations
 * Auto-generated implementation of ITelephony specification.
 */
public class TelephonyDelegate extends BaseCommunicationDelegate implements ITelephony {

    public static String APIService = "telephony";
    static LoggingDelegate Logger;

    /**
     * Default Constructor.
     */
    public TelephonyDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
    }

    /**
     * Invoke a phone call
     *
     * @param number to call
     * @return Status of the call
     * @since ARP1.0
     */
    public ITelephonyStatus call(String number) {
        try {
            Logger.log(ILoggingLogLevel.DEBUG, "Calling " + number);
            String uri = "tel:" + number.trim();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            AppContextDelegate.getMainActivity().startActivity(intent);
        } catch (Exception ex) {
            return ITelephonyStatus.Failed;
        }
        return ITelephonyStatus.Dialing;
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
