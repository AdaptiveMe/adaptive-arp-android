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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseCommunicationDelegate;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.ITelephony;
import me.adaptive.arp.api.ITelephonyStatus;

/**
 * Interface for Managing the Telephony operations
 * Auto-generated implementation of ITelephony specification.
 */
public class TelephonyDelegate extends BaseCommunicationDelegate implements ITelephony {

    // Logger
    private static final String LOG_TAG = "TelephonyDelegate";
    private ILogging logger;

    // context
    private Context context;

    /**
     * Default Constructor.
     */
    public TelephonyDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
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
            final Intent intent = new Intent(Intent.ACTION_CALL);
            String tel = "tel:" + number.trim();
            intent.setData(Uri.parse(tel));

            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "Calling number: " + tel);

            Activity mainActivity = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getActivity();

            // Run on main Thread
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            return ITelephonyStatus.Failed;
        }

        return ITelephonyStatus.Dialing;
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
