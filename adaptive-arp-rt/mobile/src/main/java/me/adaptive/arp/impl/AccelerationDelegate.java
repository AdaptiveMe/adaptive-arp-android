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
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseSensorDelegate;
import me.adaptive.arp.api.IAcceleration;
import me.adaptive.arp.api.IAccelerationListener;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.util.SensorEventListenerImpl;

/**
 * Interface defining methods about the acceleration sensor
 * Auto-generated implementation of IAcceleration specification.
 */
public class AccelerationDelegate extends BaseSensorDelegate implements IAcceleration {

    // logger
    private static final String LOG_TAG = "AccelerationDelegate";
    private ILogging logger;

    // Listeners
    private List<IAccelerationListener> listeners;

    // Sensor Listener
    private SensorEventListenerImpl sensorListener;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    /**
     * Default Constructor.
     */
    public AccelerationDelegate() {

        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        mSensorManager = (SensorManager) ((Context) AppRegistryBridge.getInstance().getPlatformContext().getContext()).getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listeners = new ArrayList<>();
        sensorListener = new SensorEventListenerImpl();
    }

    /**
     * Register a new listener that will receive acceleration events.
     *
     * @param listener to be registered.
     * @since ARP1.0
     */
    public void addAccelerationListener(IAccelerationListener listener) {

        if (!listeners.contains(listener)) {
            listeners.add(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "addAccelerationListener: " + listener.toString() + " Added!");
        } else
            logger.log(ILoggingLogLevel.Warn, LOG_TAG, "addAccelerationListener: " + listener.toString() + " is already added!");

        if (!listeners.isEmpty()) {
            mSensorManager.registerListener(sensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * De-registers an existing listener from receiving acceleration events.
     *
     * @param listener to be registered.
     * @since ARP1.0
     */
    public void removeAccelerationListener(IAccelerationListener listener) {

        if (listeners.contains(listener)) {
            listeners.remove(listener);
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeAccelerationListener" + listener.toString() + " Removed!");
        } else
            logger.log(ILoggingLogLevel.Warn, LOG_TAG, "removeAccelerationListener: " + listener.toString() + " is NOT registered");

        if (listeners.isEmpty()) mSensorManager.unregisterListener(sensorListener);
    }

    /**
     * Removed all existing listeners from receiving acceleration events.
     *
     * @since ARP1.0
     */
    public void removeAccelerationListeners() {

        listeners.clear();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeAccelerationListeners: ALL AccelerationListeners have been removed!");
        mSensorManager.unregisterListener(sensorListener);
    }

    /**
     * Getter for the listeners array
     *
     * @return Array of listeners
     */
    public List<IAccelerationListener> getListeners() {
        return listeners;
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
