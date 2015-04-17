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

import android.app.Service;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseSensorDelegate;
import me.adaptive.arp.api.IGeolocation;
import me.adaptive.arp.api.IGeolocationListener;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.util.LocationListenerImpl;

/**
 * Interface for Managing the Geolocation operations
 * Auto-generated implementation of IGeolocation specification.
 */
public class GeolocationDelegate extends BaseSensorDelegate implements IGeolocation {

    // logger
    private static final String LOG_TAG = "GeolocationDelegate";
    private ILogging logger;

    // Update interval
    private static final long UPDATE_INTERVAL = 5 * 1000;
    // Listeners

    public List<IGeolocationListener> listeners;
    // Listener for managing geo events

    private LocationListener locationListener;

    // Location manager
    private LocationManager locationManager;

    // boolean for searching status
    private boolean searching = false;

    /**
     * Default Constructor.
     */
    public GeolocationDelegate() {
        super();
        listeners = new ArrayList<>();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        Context context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
        locationListener = new LocationListenerImpl();
        locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
    }

    /**
     * Register a new listener that will receive geolocation events.
     *
     * @param listener to be registered.
     * @since ARP1.0
     */
    public void addGeolocationListener(IGeolocationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "addGeolocationListener: " + listener.toString() + " Added!");
            if (!searching) startUpdatingLocation();
        } else
            logger.log(ILoggingLogLevel.Warn, LOG_TAG, "addGeolocationListener: " + listener.toString() + " is already added!");
    }

    /**
     * De-registers an existing listener from receiving geolocation events.
     *
     * @param listener to be registered.
     * @since ARP1.0
     */
    public void removeGeolocationListener(IGeolocationListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeGeolocationListener" + listener.toString() + " Removed!");
        } else
            logger.log(ILoggingLogLevel.Warn, LOG_TAG, "removeGeolocationListener: " + listener.toString() + " is NOT registered");
        if (listeners.isEmpty()) stopUpdatingLocation();
    }

    /**
     * Removed all existing listeners from receiving geolocation events.
     *
     * @since ARP1.0
     */
    public void removeGeolocationListeners() {
        listeners.clear();
        stopUpdatingLocation();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeGeolocationListeners: ALL GeolocationListeners have been removed!");
    }

    /**
     * Start the location notification for the system
     *
     * @return Result of the operation
     */
    private boolean startUpdatingLocation() {

        boolean isGPSRegistered = false;
        boolean isNetworkRegistered = false;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Runnable rGPS = new Runnable() {

                @Override
                public void run() {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, 0,
                            locationListener);
                }
            };
            ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getExecutor().submit(rGPS);
            logger.log(ILoggingLogLevel.Debug, "GPS provider is enabled");
            isGPSRegistered = true;
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Runnable rNet = new Runnable() {

                @Override
                public void run() {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL,
                            0, locationListener);
                }
            };
            ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getExecutor().submit(rNet);
            logger.log(ILoggingLogLevel.Debug, "Network provider is enabled");
            isNetworkRegistered = true;
        }

        searching = (isGPSRegistered || isNetworkRegistered);
        return searching;
    }

    /**
     * Stop generation location events
     *
     * @return Result of the operation
     */
    private boolean stopUpdatingLocation() {

        Runnable rRemove = new Runnable() {

            @Override
            public void run() {
                locationManager.removeUpdates(locationListener);
            }
        };
        ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getExecutor().submit(rRemove);
        return true;
    }

    /**
     * Getter for the listeners list
     *
     * @return List of registered listeners
     */
    public List<IGeolocationListener> getListeners() {
        return listeners;
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
