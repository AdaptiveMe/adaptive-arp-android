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
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.adaptive.arp.api.Acceleration;
import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseSensorDelegate;
import me.adaptive.arp.api.IAcceleration;
import me.adaptive.arp.api.IAccelerationListener;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Interface defining methods about the acceleration sensor
 * Auto-generated implementation of IAcceleration specification.
 */
public class AccelerationDelegate extends BaseSensorDelegate implements IAcceleration {


    static final float ALPHA = 0.15f;
    static LoggingDelegate Logger;
    public String APIService = "accelerometer";
    protected float[] gravSensorVals;
    private List<IAccelerationListener> listeners = new ArrayList<IAccelerationListener>();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] grav = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] orientation = new float[3];
    private float[] rotation = new float[9];
    private float[] linear_acceleration = new float[3];
    private SensorEventListener sensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent evt) {
            if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    gravSensorVals = lowPass(evt.values.clone(), gravSensorVals);
                    grav[0] = gravSensorVals[0];
                    grav[1] = gravSensorVals[1];
                    grav[2] = gravSensorVals[2];

                }
                if (gravSensorVals != null) {
                    // updating the rotation array
                    SensorManager.getRotationMatrix(rotation, null, grav,
                            geomagnetic);
                    // updating the orientation array
                    SensorManager.getOrientation(rotation, orientation);
                    /*
                    //azimuth -- Z
                    euler_acceleration[0] = (float)(((gravSensorVals[0]*180)/Math.PI)+180);
                    //pitch -- X
                    euler_acceleration[1] = (float)(((gravSensorVals[1]*180/Math.PI))+90);
                    //roll -- Y
                    euler_acceleration[2] = (float)(((gravSensorVals[2]*180/Math.PI)));
                    */
                    linear_acceleration[0] = gravSensorVals[0] - grav[0];
                    linear_acceleration[1] = gravSensorVals[1] - grav[1];
                    linear_acceleration[2] = gravSensorVals[2] - grav[2];
                    Acceleration acc = new Acceleration(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2], new Date().getTime());

                    if (!listeners.isEmpty()) {
                        for (IAccelerationListener accListener : listeners) {
                            accListener.onResult(acc);
                        }
                    }
                }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // has nothing to do
        }
    };
    private float[] euler_acceleration = new float[3];
    private boolean searching = false;

    /**
     * Default Constructor.
     */
    public AccelerationDelegate() {
        super();

        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
        mSensorManager = (SensorManager) ((Context)AppRegistryBridge.getInstance().getPlatformContext().getContext()).getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * listen to sensor (ACCELEROMETER, MAGNETIC FIELD) changes
     */

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
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
            Logger.log(ILoggingLogLevel.Debug, APIService, "addAccelerationListener: " + listener.toString() + " Added!");
        } else
            Logger.log(ILoggingLogLevel.Warn, APIService, "addAccelerationListener: " + listener.toString() + " is already added!");
        if (!listeners.isEmpty()) {
            mSensorManager.registerListener(sensorListener, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
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
            Logger.log(ILoggingLogLevel.Debug, APIService, "removeAccelerationListener" + listener.toString() + " Removed!");
        } else
            Logger.log(ILoggingLogLevel.Warn, APIService, "removeAccelerationListener: " + listener.toString() + " is NOT registered");
        if (listeners.isEmpty()) mSensorManager.unregisterListener(sensorListener);
    }

    /**
     * Removed all existing listeners from receiving acceleration events.
     *
     * @since ARP1.0
     */
    public void removeAccelerationListeners() {
        listeners.clear();
        Logger.log(ILoggingLogLevel.Debug, APIService, "removeAccelerationListeners: ALL AccelerationListeners have been removed!");
        mSensorManager.unregisterListener(sensorListener);
    }


}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
