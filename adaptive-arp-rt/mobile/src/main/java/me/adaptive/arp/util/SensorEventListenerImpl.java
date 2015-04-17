package me.adaptive.arp.util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.Date;
import java.util.List;

import me.adaptive.arp.api.Acceleration;
import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.IAccelerationListener;
import me.adaptive.arp.impl.AccelerationDelegate;

/**
 * Sensor event listener implementation.
 */
public class SensorEventListenerImpl implements android.hardware.SensorEventListener {

    private float[] gravSensorVals;
    private float[] grav = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] orientation = new float[3];
    private float[] rotation = new float[9];
    private float[] linear_acceleration = new float[3];

    /**
     * Called when sensor values have changed.
     *
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            gravSensorVals = lowPass(event.values.clone(), gravSensorVals);
            grav[0] = gravSensorVals[0];
            grav[1] = gravSensorVals[1];
            grav[2] = gravSensorVals[2];

            SensorManager.getRotationMatrix(rotation, null, grav, geomagnetic);
            SensorManager.getOrientation(rotation, orientation);

            linear_acceleration[0] = gravSensorVals[0] - grav[0];
            linear_acceleration[1] = gravSensorVals[1] - grav[1];
            linear_acceleration[2] = gravSensorVals[2] - grav[2];
            Acceleration acc = new Acceleration(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2], new Date().getTime());

            List<IAccelerationListener> listeners = ((AccelerationDelegate)AppRegistryBridge.getInstance().getAccelerationBridge().getDelegate()).getListeners();

            if (!listeners.isEmpty()) {
                for (IAccelerationListener l : listeners) {
                    l.onResult(acc);
                }
            }
        }

    }

    /**
     * Returns the sensor values
     *
     * @param input  Input values
     * @param output Output values
     * @return Returns an array of values
     */
    private float[] lowPass(float[] input, float[] output) {

        float alpha = 0.15f;

        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     *
     * @param accuracy The new accuracy of this sensor
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // has nothing to do
    }
}
