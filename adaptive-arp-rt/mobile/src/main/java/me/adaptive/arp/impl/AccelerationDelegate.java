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

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.adaptive.arp.api.Acceleration;
import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.IAcceleration;
import me.adaptive.arp.api.IAccelerationListener;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
   Interface defining methods about the acceleration sensor
   Auto-generated implementation of IAcceleration specification.
*/
public class AccelerationDelegate extends BaseSensorDelegate implements IAcceleration {


    public String APIService = "accelerometer";
    static LoggingDelegate Logger;
    private List<IAccelerationListener> listeners = new ArrayList<IAccelerationListener>();

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] orientation = new float[3];
    private float[] rotation = new float[9];
    private float[] linear_acceleration = new float[3];
    private boolean searching = false;
     /**
        Default Constructor.
     */
     public AccelerationDelegate() {
         super();

         Logger = ((LoggingDelegate)AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
         mSensorManager = (SensorManager) AppContextDelegate.getMainActivity()
                 .getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
         mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
     }

     /**
        Register a new listener that will receive acceleration events.

        @param listener to be registered.
        @since ARP1.0
     */
     public void addAccelerationListener(IAccelerationListener listener) {
         if (!listeners.contains(listener)){
             listeners.add(listener);
             Logger.log(ILoggingLogLevel.DEBUG, APIService, "addAccelerationListener: "+ listener.toString()+" Added!");
         }else Logger.log(ILoggingLogLevel.WARN, APIService, "addAccelerationListener: "+ listener.toString() + " is already added!");
         if(!listeners.isEmpty()){
             mSensorManager.registerListener(sensorListener, mSensor,
                     SensorManager.SENSOR_DELAY_NORMAL);
         }
     }

     /**
        De-registers an existing listener from receiving acceleration events.

        @param listener to be registered.
        @since ARP1.0
     */
     public void removeAccelerationListener(IAccelerationListener listener) {
         if(listeners.contains(listener)){
             listeners.remove(listener);
             Logger.log(ILoggingLogLevel.DEBUG, APIService, "removeAccelerationListener"+ listener.toString()+" Removed!");
         }else Logger.log(ILoggingLogLevel.WARN, APIService, "removeAccelerationListener: "+ listener.toString() + " is NOT registered");
         if(listeners.isEmpty()) mSensorManager.unregisterListener(sensorListener);
     }

     /**
        Removed all existing listeners from receiving acceleration events.

        @since ARP1.0
     */
     public void removeAccelerationListeners() {
         listeners.clear();
         Logger.log(ILoggingLogLevel.DEBUG, APIService, "removeAccelerationListeners: ALL AccelerationListeners have been removed!");
         mSensorManager.unregisterListener(sensorListener);
     }

    /**
     * listen to sensor (ACCELEROMETER, MAGNETIC FIELD) changes
     */
    private SensorEventListener sensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                final float alpha = 0.8F;// TODO calculate the alpha value
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];
                // updating the rotation array
                SensorManager.getRotationMatrix(rotation, null, gravity,
                        geomagnetic);
                // updating the orientation array
                SensorManager.getOrientation(rotation, orientation);

                Acceleration acc = new Acceleration(linear_acceleration[0],linear_acceleration[1],linear_acceleration[2], new Date().getTime());

                if(!listeners.isEmpty()){
                    for(IAccelerationListener accListener: listeners){
                        accListener.onResult(acc);
                    }
                }
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                geomagnetic = event.values.clone();
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                System.out.println("PROXIMITY = " + event.values[0]);
                if (event.values[0] == 0.0) {
                    dimScreem();
                } else {
                    // TODO make it work!!!!!
                    wakeScreen();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // has nothing to do
        }
    };

    private void setBright(float value) {

        Window mywindow = ((Activity) AppContextDelegate.getMainActivity().getApplicationContext())
                .getWindow();

        WindowManager.LayoutParams lp = mywindow.getAttributes();

        lp.screenBrightness = value;

        mywindow.setAttributes(lp);
    }

    private void dimScreem() {

        Runnable task = new Runnable() {

            @Override
            public void run() {
                setBright(0.0F);
            }
        };
        ((Activity) AppContextDelegate.getMainActivity().getApplicationContext()).runOnUiThread(task);
    }


    private void wakeScreen() {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                setBright(1.0F);
            }
        };
        ((Activity) AppContextDelegate.getMainActivity().getApplicationContext()).runOnUiThread(task);
    }
}
/**
------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
*/
