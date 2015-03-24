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

import java.util.ArrayList;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BasePIMDelegate;
import me.adaptive.arp.api.Email;
import me.adaptive.arp.api.EmailAddress;
import me.adaptive.arp.api.EmailAttachmentData;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IMail;
import me.adaptive.arp.api.IMessagingCallback;
import me.adaptive.arp.api.IMessagingCallbackError;
import me.adaptive.arp.api.IMessagingCallbackWarning;

/**
 * Interface for Managing the Mail operations
 * Auto-generated implementation of IMail specification.
 */
public class MailDelegate extends BasePIMDelegate implements IMail {


    static LoggingDelegate Logger;
    public String APIService = "mail";

    /**
     * Default Constructor.
     */
    public MailDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

    }

    /**
     * Send an Email
     *
     * @param data     Payload of the email
     * @param callback Result callback of the operation
     * @since ARP1.0
     */
    public void sendEmail(Email data, IMessagingCallback callback) {

        List<String> intermediate = new ArrayList<>();
        for (EmailAddress to : data.getToRecipients()) {
            intermediate.add(to.getAddress());
        }
        String[] TO = (String[]) intermediate.toArray();
        intermediate.clear();
        for (EmailAddress cc : data.getCcRecipients()) {
            intermediate.add(cc.getAddress());
        }
        String[] CC = (String[]) intermediate.toArray();
        intermediate.clear();
        for (EmailAddress bcc : data.getBccRecipients()) {
            intermediate.add(bcc.getAddress());
        }
        String[] BCC = (String[]) intermediate.toArray();
        intermediate.clear();


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //emailIntent.setData(Uri.parse("mailto:"));
        //emailIntent.setType("text/plain");
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_BCC, BCC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, data.getSubject());
        emailIntent.putExtra(Intent.EXTRA_TEXT, data.getMessageBody());
        emailIntent.putExtra(Intent.EXTRA_MIME_TYPES, data.getMessageBodyMimeType());
        for (EmailAttachmentData attachmentData : data.getEmailAttachmentData()) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, attachmentData.getReferenceUrl());
        }


        try {
            ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            callback.onResult(true);
            Logger.log(ILoggingLogLevel.Debug, "Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Logger.log(ILoggingLogLevel.Error, "Unable to find activity");
            callback.onError(IMessagingCallbackError.EmailAccountNotFound);
        } catch (Exception ex) {
            callback.onWarning(false, IMessagingCallbackWarning.Unknown);
        }
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
