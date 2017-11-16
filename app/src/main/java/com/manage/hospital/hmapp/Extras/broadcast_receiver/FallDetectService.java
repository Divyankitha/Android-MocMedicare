package com.manage.hospital.hmapp.Extras.broadcast_receiver;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class FallDetectService extends Service implements SensorEventListener {
    public static final String FALL_NOTIFICATION = "com.apurva.accellerometer.FallDetectionService.Fall";
    public static final String ACCEL_DATA_NOTIFICATION = "com.apurva.accellerometer.FallDetectionService.AccelData";
    public static final String ACCEL_DATA_KEY = "AccelDataKey";

    static final int WINDOW_SIZE = 50;
    static final int WALKING_OSCILATION_COUNT_THRESHOLD = 3; // if oscillation occurs more than this number in the mWindow fall is actually walking
    static final double THRESHOLD_FALL_LOW = 5.0; // lower threshold of accel data that will trigger the detection of free fall mState
    static final double THRESHOLD_FALL_HIGH = 15.0; // upper threshold of accel date that will trigger the detection of hitting floor mState

    double[] mWindow;
    int mSkipFrames;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mWindow = new double[WINDOW_SIZE];
        for(int i=0; i < WINDOW_SIZE; i++)
            mWindow[i] = 10.0;
        mSkipFrames = 0;

        SensorManager sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

        return Service.START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            double ax = event.values[0];
            double ay = event.values[1];
            double az = event.values[2];
            double a = ((int)(100 * Math.sqrt(ax * ax + ay * ay + az * az)))/100.0;

            updateWindow(a);
            process();
        }
    }

    private void updateWindow(double val) {
        for(int i=0; i < (WINDOW_SIZE - 1); i++)
            mWindow[i] = mWindow[i+1];
        mWindow[WINDOW_SIZE - 1] = val;
        publishAccelData(mWindow[0]);
    }

    private void process() {
        if(mSkipFrames > 0) {
            mSkipFrames--;
        } else {
            if(mWindow[0] < THRESHOLD_FALL_LOW) {
                processFullWindow();
                mSkipFrames = 50;
            }
        }
    }

    private void processFullWindow() {
        int i = 0;
        int countOscialltion = 0;
        FallDetectionState state = FallDetectionState.NATURAL;
        while(i < WINDOW_SIZE) {
            switch(state) {
                case NATURAL:
                    if (mWindow[i] <= THRESHOLD_FALL_LOW) {
                        state = FallDetectionState.FALL_START;
                    }
                    break;

                case FALL_START:
                    if(mWindow[i] >= THRESHOLD_FALL_HIGH) {
                        state = FallDetectionState.HIT_GROUND;
                    }
                    break;

                case HIT_GROUND:
                    if ((mWindow[i] > THRESHOLD_FALL_LOW) && (mWindow[i] < THRESHOLD_FALL_HIGH)) {
                        countOscialltion++;
                        state = FallDetectionState.NATURAL;
                    }
                    break;
            }
            i++;
        }

        if(countOscialltion > WALKING_OSCILATION_COUNT_THRESHOLD)
            state = FallDetectionState.WALKING;
        else if(countOscialltion >= 1) {
            state = FallDetectionState.FALL_DETECTED;
            publishFall();
        } else
            state = FallDetectionState.NATURAL;
    }

    private void publishAccelData(double accel) {
        Intent intent = new Intent(ACCEL_DATA_NOTIFICATION);
        intent.putExtra(ACCEL_DATA_KEY, accel);
        sendBroadcast(intent);
    }

    private void publishFall() {
        Intent intent = new Intent(FALL_NOTIFICATION);
        sendBroadcast(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
