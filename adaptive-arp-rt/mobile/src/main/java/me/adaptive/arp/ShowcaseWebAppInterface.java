package me.adaptive.arp;/*
 * =| ADAPTIVE RUNTIME PLATFORM |=======================================================================================
 *
 * (C) Copyright 2013-2014 Carlos Lozano Diez t/a Adaptive.me <http://adaptive.me>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Original author:
 *
 *     * Carlos Lozano Diez
 *                 <http://github.com/carloslozano>
 *                 <http://twitter.com/adaptivecoder>
 *                 <mailto:carlos@adaptive.me>
 *
 * Contributors:
 *
 *     * Francisco Javier Martin Bueno
 *             <https://github.com/kechis>
 *             <mailto:kechis@gmail.com>
 *
 * =====================================================================================================================
 */

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.Contact;
import me.adaptive.arp.api.ContactBridge;
import me.adaptive.arp.api.ContactUid;
import me.adaptive.arp.api.IAdaptiveRPGroup;
import me.adaptive.arp.api.IContactFieldGroup;
import me.adaptive.arp.api.IContactFilter;
import me.adaptive.arp.api.IContactResultCallback;
import me.adaptive.arp.api.IContactResultCallbackError;
import me.adaptive.arp.api.IContactResultCallbackWarning;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.LoggingBridge;

public class ShowcaseWebAppInterface {
    Context mContext;

    LoggingBridge Logger = AppRegistryBridge.getInstance().getLoggingBridge();
    IContactResultCallback cb = new IContactResultCallback() {
        @Override
        public void onError(IContactResultCallbackError error) {
            Logger.log(ILoggingLogLevel.Error, error.toString());
        }

        @Override
        public void onResult(Contact[] contacts) {
            if (Logger != null) {
                Log.d("Native", contacts.length + " contacts");
                Logger.log(ILoggingLogLevel.Debug, "MainActivity Size: " + contacts.length);
                for (Contact contact : contacts) {
                    Logger.log(ILoggingLogLevel.Debug, "MainActivity ID RETURNED: " + contact.getContactId());
                                /*ContactPhone[] phone = contact.getContactPhones();
                                if(phone != null && phone.length> 0){
                                    for (ContactPhone phon : contact.getContactPhones()) {
                                        Logger.log(ILoggingLogLevel.DEBUG, "MainActivity PHONE RETURNED: " + phon.getPhone());
                                    }
                                }
                                ContactEmail[] email = contact.getContactEmails();
                                if(email != null && email.length> 0){
                                    for (ContactEmail mail : email) {
                                        Logger.log(ILoggingLogLevel.Debug, "MainActivity MAIL RETURNED: " + mail.getEmail());
                                    }
                                }*/

                }
            } else {
                Log.e("Native", "no log " + contacts.length + " contacts");
            }

        }

        @Override
        public void onWarning(Contact[] contacts, IContactResultCallbackWarning warning) {
            AppRegistryBridge.getInstance().getLoggingBridge().log(ILoggingLogLevel.Warn, warning.toString());
        }

        @Override
        public IAdaptiveRPGroup getAPIGroup() {
            return null;
        }

        @Override
        public String getAPIVersion() {
            return null;
        }
    };
    ContactBridge contactBridge = AppRegistryBridge.getInstance().getContactBridge();

    /**
     * Instantiate the interface and set the context
     */
    ShowcaseWebAppInterface(Context c) {
        mContext = c;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void getContacts() {
        contactBridge.getContacts(cb);
    }

    @JavascriptInterface
    public void searchContacts(String term) {
        if (term.isEmpty()) term = "Kmail";
        contactBridge.searchContacts(term, cb);
    }

    @JavascriptInterface
    public void getContact(String id) {
        if (id.isEmpty()) id = "4331";
        AppRegistryBridge.getInstance().getContactBridge().getContact(new ContactUid(id), cb);
    }

    @JavascriptInterface
    public void getContactsWithFilter(IContactFilter[] filters) {
        if (filters.length == 0) filters = new IContactFilter[]{IContactFilter.HasEmail};
        AppRegistryBridge.getInstance().getContactBridge().getContactsWithFilter(cb, null, filters);
    }

    @JavascriptInterface
    public void getContactsForFields(IContactFieldGroup[] fieldGroups) {
        if (fieldGroups.length == 0)
            fieldGroups = new IContactFieldGroup[]{IContactFieldGroup.PersonalInfo, IContactFieldGroup.Emails};
        AppRegistryBridge.getInstance().getContactBridge().getContactsForFields(cb, fieldGroups);
    }

    @JavascriptInterface
    public void playVideo(String url) {
        AppRegistryBridge.getInstance().getVideoBridge().playStream(url);
    }

    @JavascriptInterface
    public void externalBrowser(String url, String title, String back) {
        AppRegistryBridge.getInstance().getBrowserBridge().getDelegate().openInternalBrowser(url, title, back);

    }
}