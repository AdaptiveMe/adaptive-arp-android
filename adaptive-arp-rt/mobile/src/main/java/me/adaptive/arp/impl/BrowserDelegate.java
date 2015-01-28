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
import me.adaptive.arp.api.IBrowser;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
   Interface for Managing the browser operations
   Auto-generated implementation of IBrowser specification.
*/
public class BrowserDelegate extends BaseUIDelegate implements IBrowser {


    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_HTML = "extra_html";
    public static final String EXTRA_BROWSER_TITLE = "extra_title";
    public static final String EXTRA_BUTTON_TEXT = "extra_buttontext";
    public static final String EXTRA_FILE_EXTENSIONS = "extra_fileextensions";

    private static final String ACTION_SHOW_BROWSER = "arp.adaptive.me.SHOW_BROWSER";

    public static String APIService = "browser";
    static LoggingDelegate Logger;

     /**
        Default Constructor.
     */
     public BrowserDelegate() {
          super();
         Logger = ((LoggingDelegate)AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
     }

     /**
        Method for opening a URL like a link in the native default browser

        @param url Url to open
        @return The result of the operation
        @since ARP1.0
     */
     public boolean openExtenalBrowser(String url) {
         try {
             Intent i = new Intent(Intent.ACTION_VIEW);
             i.setData(Uri.parse(url));
             AppContextDelegate.getMainActivity().startActivity(i);
             return true;
         }catch (Exception ex){
             return false;
         }
     }

     /**
        Method for opening a browser embedded into the application

        @param url            Url to open
        @param title          Title of the Navigation bar
        @param backButtonText Title of the Back button bar
        @return The result of the operation
        @since ARP1.0
     */
     public boolean openInternalBrowser(String url, String title, String backButtonText) {
         boolean result = false;
         try {
             Intent intent = new Intent(AppContextDelegate.getMainActivity().getApplicationContext()
                     .getPackageName() + ACTION_SHOW_BROWSER);
             intent.putExtra(EXTRA_URL, url);
             intent.putExtra(EXTRA_BROWSER_TITLE, title);
             intent.putExtra(EXTRA_BUTTON_TEXT, backButtonText);

             AppContextDelegate.getMainActivity().startActivity(intent);
             result = true;
         } catch (Exception ex) {
             Logger.log(ILoggingLogLevel.DEBUG,APIService, "tryConnection error "+ ex.getLocalizedMessage());
         }
         return result;
     }

     /**
        Method for opening a browser embedded into the application in a modal window

        @param url            Url to open
        @param title          Title of the Navigation bar
        @param backButtonText Title of the Back button bar
        @return The result of the operation
        @since ARP1.0
     */
     public boolean openInternalBrowserModal(String url, String title, String backButtonText) {
          boolean response;
          // TODO: Not implemented.
          throw new UnsupportedOperationException(this.getClass().getName()+":openInternalBrowserModal");
          // return response;
     }

}
/**
------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
*/
