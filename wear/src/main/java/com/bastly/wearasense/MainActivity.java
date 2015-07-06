package com.bastly.wearasense;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.bastly.wearasense.AlarmReceiver.TimeBroadCastReceiver;
import com.bastly.wearasense.Utils.VibrationManager;

public class MainActivity extends Activity implements SensorEventListener {

    private static final String TAG = MainActivity.class.getName();
    private static long elapsed = 0;
    private TextView mTextView;
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    private Vibrator vibration;
    float azimut;
    private double azimutDegrees;
    private AlarmManager alarmMgr;
    private long INTERVAL_MINUTE = 1 * 60 * 1000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        WindowManager.LayoutParams WMLP = getWindow().getAttributes();
        WMLP.screenBrightness = 0F;

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                INTERVAL_MINUTE,
                INTERVAL_MINUTE, pendingIntent);

        vibration = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "accelerometer " + accelerometer + "magnetometer " + magnetometer );
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {
            elapsed = System.currentTimeMillis();
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                    Log.d(TAG, "orientation " + Math.toDegrees(azimut));
                    azimutDegrees = Math.toDegrees(azimut);
                    vibration.vibrate(VibrationManager.getPattern(azimutDegrees), -1);
                } else {
                    Log.d(TAG, "unsuccessfull getting rotation matrix");
                }
            } else {
                Log.d(TAG, "gravity " + mGravity + "and geomagnetic " + mGeomagnetic);
            }
        }
}
