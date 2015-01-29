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

import android.content.Context;
import android.content.Intent;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.Email;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IMail;
import me.adaptive.arp.api.IMessagingCallback;
import me.adaptive.arp.api.IMessagingCallbackError;

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
    public void sendEmail(final Email data, final IMessagingCallback callback) {
        AppContextDelegate.getExecutorService().submit(new Runnable() {
            public void run() {
                boolean result = false;

                try {
                    Intent emailIntent;
                    boolean hasAttachment = data.getEmailAttachmentData() != null
                            && data.getEmailAttachmentData().length > 0;
                    boolean isMultiple = hasAttachment
                            && data.getEmailAttachmentData().length > 1;

                    if (isMultiple) {
                        emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    } else {
                        emailIntent = new Intent(Intent.ACTION_SEND);
                    }
                    emailIntent
                            .setType(data.getMessageBodyMimeType() != null ? data
                                    .getMessageBodyMimeType() : "text/html");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, data.getSubject());
                    emailIntent.putExtra(Intent.EXTRA_TEXT, data.getMessageBody());
                    emailIntent.putExtra(Intent.EXTRA_EMAIL,
                            data.getToRecipients());
                    emailIntent.putExtra(Intent.EXTRA_BCC,
                            data.getBccRecipients());
                    emailIntent.putExtra(Intent.EXTRA_CC,
                            data.getCcRecipients());

            /*if (hasAttachment) {
                if (isMultiple) {
                    ArrayList<Uri> uris = new ArrayList<Uri>();
                    for (AttachmentData att : data.getAttachmentData()) {
                        File attFile = createFileFromAttachment(att);
                        if (attFile != null) {
                            Uri u = Uri.fromFile(attFile);
                            uris.add(u);
                        }
                    }
                    emailIntent.putParcelableArrayListExtra(
                            Intent.EXTRA_STREAM, uris);
                }
                Uri u = Uri.fromFile(createFileFromAttachment(data
                        .getAttachmentData()[0]));
                emailIntent.putExtra(Intent.EXTRA_STREAM, u);

            }*/

                    Context context = AppContextDelegate.getMainActivity().getApplicationContext();
                    context.startActivity(Intent.createChooser(emailIntent, "Email"));
                    result = true;
                } catch (Exception ex) {
                    Logger.log(ILoggingLogLevel.ERROR, APIService, "sendEmail: error " + ex.getLocalizedMessage());
                    callback.onError(IMessagingCallbackError.Unknown);
                }

                callback.onResult(result);
            }
        });
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
