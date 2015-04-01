package me.adaptive.arp.util;/*
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ICapabilitiesNet;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.INetworkStatusListener;
import me.adaptive.arp.api.NetworkEvent;
import me.adaptive.arp.impl.LoggingDelegate;
import me.adaptive.arp.impl.NetworkStatusDelegate;

public class NetworkStatusReceiver extends BroadcastReceiver {

    private static String APIService = "NetworkStatusReceiver";
    static LoggingDelegate Logger;
    private static List<INetworkStatusListener> listeners;

    /**
     * Default Constructor.
     */
    public NetworkStatusReceiver() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
        listeners = ((NetworkStatusDelegate) AppRegistryBridge.getInstance().getNetworkStatusBridge().getDelegate()).getListeners();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Logger.log(ILoggingLogLevel.Debug, APIService, "key [" + key + "]: " + extras.get(key));
            }
        } else {
            Logger.log(ILoggingLogLevel.Debug, APIService, "no extras");
        }
        if(listeners.isEmpty()) return;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        ICapabilitiesNet NetworkType = ICapabilitiesNet.Unavailable;
        if (isConnected)
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIMAX:
                case ConnectivityManager.TYPE_WIFI:
                    Logger.log(ILoggingLogLevel.Debug, APIService, "WIFI");
                    NetworkType = ICapabilitiesNet.WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    Logger.log(ILoggingLogLevel.Debug, APIService, "MOBILE");
                    int networkType = activeNetwork.getSubtype();
                    switch (networkType) {
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

        final NetworkEvent networkEvent = new NetworkEvent(NetworkType, System.currentTimeMillis());
        for (INetworkStatusListener listener : listeners) {
            listener.onResult(networkEvent);
        }
    }
}