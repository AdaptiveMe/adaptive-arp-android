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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ICapabilitiesNet;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.INetworkStatus;
import me.adaptive.arp.api.INetworkStatusListener;

/**
 * Interface for Managing the Network status
 * Auto-generated implementation of INetworkStatus specification.
 */
public class NetworkStatusDelegate extends BaseCommunicationDelegate implements INetworkStatus {


    public static String APIService = "networkStatus";
    static LoggingDelegate Logger;
    public List<INetworkStatusListener> listeners = new ArrayList<INetworkStatusListener>();

    /**
     * Default Constructor.
     */
    public NetworkStatusDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

    }

    /**
     * Add the listener for network status changes of the app
     *
     * @param listener Listener with the result
     * @since ARP1.0
     */
    public void addNetworkStatusListener(INetworkStatusListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            Logger.log(ILoggingLogLevel.Debug, APIService, "addNetworkStatusListener: " + listener.toString() + " Added!");
        } else
            Logger.log(ILoggingLogLevel.Debug, APIService, "addNetworkStatusListener: " + listener.toString() + " is already added!");
    }

    /**
     * Un-registers an existing listener from receiving network status events.
     *
     * @param listener Listener with the result
     * @since ARP1.0
     */
    public void removeNetworkStatusListener(INetworkStatusListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeNetworkStatusListener" + listener.toString() + " Removed!");
        } else
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeNetworkStatusListener: " + listener.toString() + " is NOT registered");
    }

    /**
     * Removes all existing listeners from receiving network status events.
     *
     * @since ARP1.0
     */
    public void removeNetworkStatusListeners() {
        listeners.clear();
        Logger.log(ILoggingLogLevel.Debug, APIService, "removeNetworkStatusListeners: ALL NetworkStatusListeners have been removed!");
    }


    public class NetworkStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                for (String key: extras.keySet()) {
                    Logger.log(ILoggingLogLevel.Debug, APIService, "key [" + key + "]: " + extras.get(key));
                }
            }
            else {
                Logger.log(ILoggingLogLevel.Debug, APIService, "no extras");
            }

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            ICapabilitiesNet NetworkType = ICapabilitiesNet.Unavailable;
            if(isConnected)
                switch(activeNetwork.getType()){
                    case ConnectivityManager.TYPE_WIMAX:
                    case ConnectivityManager.TYPE_WIFI:
                        Logger.log(ILoggingLogLevel.Debug,APIService,"WIFI");
                        NetworkType = ICapabilitiesNet.WIFI;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        Logger.log(ILoggingLogLevel.Debug,APIService,"MOBILE");
                        int networkType = activeNetwork.getSubtype();
                        switch(networkType){
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                                NetworkType = ICapabilitiesNet.GSM;
                                break;
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                NetworkType = ICapabilitiesNet.GPRS;
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                NetworkType = ICapabilitiesNet.HSDPA;
                                break;
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                NetworkType = ICapabilitiesNet.LTE;
                                break;
                            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            default:
                                NetworkType = ICapabilitiesNet.Unknown;
                        }
                        break;
                }

            for(INetworkStatusListener listener: listeners){
                listener.onResult(NetworkType);
            }
        }
    }

}


/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
