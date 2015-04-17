package me.adaptive.arp.util;

import android.app.Service;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.Geolocation;
import me.adaptive.arp.api.IGeolocationListener;
import me.adaptive.arp.api.IGeolocationListenerWarning;
import me.adaptive.arp.impl.GeolocationDelegate;

/**
 * Implementation of the Location Listener
 */
public class LocationListenerImpl implements LocationListener {

    // Location manager
    private LocationManager locationManager;

    /**
     * Default Constructor.
     */
    public LocationListenerImpl() {
        super();
        Context context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
        locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
    }

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        refreshListeners(location);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        Geolocation geo = convertCoordinates(location);

        List<IGeolocationListener> listeners = ((GeolocationDelegate) AppRegistryBridge.getInstance().getGeolocationBridge().getDelegate()).getListeners();

        if (!listeners.isEmpty()) {
            for (IGeolocationListener l : listeners) {
                l.onWarning(geo, IGeolocationListenerWarning.StaleData);
            }
        }
    }

    /**
     * Convert one android location to Adaptive Geolocation
     *
     * @param location Android Location
     * @return Adaptive coordinate
     */
    private Geolocation convertCoordinates(Location location) {
        return new Geolocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy(), location.getAccuracy(), System.currentTimeMillis());
    }

    /**
     * Update all the listeners with a new value of location
     *
     * @param location New location
     */
    private void refreshListeners(Location location) {

        List<IGeolocationListener> listeners = ((GeolocationDelegate) AppRegistryBridge.getInstance().getGeolocationBridge().getDelegate()).getListeners();

        if (!listeners.isEmpty()) {
            Geolocation geo = convertCoordinates(location);
            for (IGeolocationListener l : listeners) {
                l.onResult(geo);
            }
        }
    }
}
