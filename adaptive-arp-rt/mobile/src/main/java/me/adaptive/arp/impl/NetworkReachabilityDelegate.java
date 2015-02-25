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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.INetworkReachability;
import me.adaptive.arp.api.INetworkReachabilityCallback;
import me.adaptive.arp.api.INetworkReachabilityCallbackError;

/**
 * Interface for Managing the Network reachability operations
 * Auto-generated implementation of INetworkReachability specification.
 */
public class NetworkReachabilityDelegate extends BaseCommunicationDelegate implements INetworkReachability {


    private static final String HTTP_SCHEME = "http://";
    private static final String HTTPS_SCHEME = "https://";
    public static String APIService = "networkReachability";
    static LoggingDelegate Logger;

    /**
     * Default Constructor.
     */
    public NetworkReachabilityDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

    }

    /**
     * Whether there is connectivity to a host, via domain name or ip address, or not.
     *
     * @param host     domain name or ip address of host.
     * @param callback Callback called at the end.
     * @since ARP1.0
     */
    public void isNetworkReachable(final String host, final INetworkReachabilityCallback callback) {
        ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getExecutorService().submit(new Runnable() {
            public void run() {
                checkHttpConnection(host, callback);
            }
        });
    }

    /**
     * Whether there is connectivity to an url of a service or not.
     *
     * @param url      to look for
     * @param callback Callback called at the end
     * @since ARP1.0
     */
    public void isNetworkServiceReachable(String url, INetworkReachabilityCallback callback) {
        //TODO REVIEW
        isNetworkReachable(url, callback);
    }

    private void checkHttpConnection(String testUrl, INetworkReachabilityCallback cb) {
        boolean hasScheme = testUrl.contains("://");
        if (!hasScheme) {
            testUrl = HTTP_SCHEME + testUrl;
        }

        ConnectivityManager cm = (ConnectivityManager) ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL(testUrl);   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(10 * 1000);          // 10 s.
                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    Logger.log(ILoggingLogLevel.Debug, APIService, "Connection: Success !");
                    //return true;
                    cb.onResult(true);
                } else {
                    Logger.log(ILoggingLogLevel.Error, APIService, "Connection: Failure ! response code " + urlc.getResponseCode());
                    cb.onError(INetworkReachabilityCallbackError.NoResponse);
                    //return false;
                }
            } catch (MalformedURLException e) {
                Logger.log(ILoggingLogLevel.Error, APIService, "Connection: Failure ! MalformedURLException");
                e.printStackTrace();
                cb.onError(INetworkReachabilityCallbackError.WrongParams);
                //return false;
            } catch (IOException e) {
                cb.onError(INetworkReachabilityCallbackError.NotAllowed);
                Logger.log(ILoggingLogLevel.Error, APIService, "Connection: Failure ! IOException");
                e.printStackTrace();
            }
        }
        Logger.log(ILoggingLogLevel.Error, APIService, "Connection: Failure !");
        //return false;

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }

    public boolean NetworkAvailable() {
        ConnectivityManager conMgr = (ConnectivityManager) ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING) {

            // notify user you are online
            //ServiceLocator.getLogger().log(ILogging.LogLevel.DEBUG, APIService, "NETWORK AVAILABLE");
            Logger.log(ILoggingLogLevel.Debug, APIService, "NETWORK AVAILABLE");
            return true;
        } else if (conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {

            // notify user you are not online
            //ServiceLocator.getLogger().log(ILogging.LogLevel.DEBUG, APIService, "NETWORK IS NOT AVAILABLE");
            Logger.log(ILoggingLogLevel.Debug, APIService, "NETWORK IS NOT AVAILABLE");
            return false;
        }
        return false;
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
