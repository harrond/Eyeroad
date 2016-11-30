package com.example.hoyoung.eyeload;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Hoyoung on 2016-09-15.
 */
public class SensorActivity extends Activity implements SensorEventListener, LocationListener {
    private static final String TAG = "SensorsActivity";
    private static final AtomicBoolean computing = new AtomicBoolean(false);

    private static final int MIN_TIME = 30 * 1000;
    private static final int MIN_DISTANCE = 10;

    private static final float temp[] = new float[9];
    private static final float rotation[] = new float[9];
    private static final float grav[] = new float[3];
    private static final float mag[] = new float[3];

    private static final Matrix worldCoord = new Matrix();//mixare쓰는부분
    private static final Matrix magneticCompensatedCoord = new Matrix();
    private static final Matrix xAxisRotation = new Matrix();
    private static final Matrix magneticNorthCompensation = new Matrix();

    private static GeomagneticField gmf = null;
    private static float smooth[] = new float[3];
    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorMag = null;
    private static Sensor sensorGrav = null;
    private static LocationManager locationMgr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        double angleX = Math.toRadians(-90);
        double angleY = Math.toRadians(-90);
        xAxisRotation.set(1f, 0f, 0f, 0f, (float) Math.cos(angleX), (float) -Math.sin(angleX), 0f, (float) Math.sin(angleX), (float) Math.cos(angleX));

        try {
            sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);


            if (sensors.size() > 0) {
                sensorGrav = sensors.get(0);
            }
            sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

            if (sensors.size() > 0) sensorMag = sensors.get(0);
            sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
            sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL); //센서를 게임 딜레이로 읽어드린다.

            locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            } catch (SecurityException e) {
                Log.e("PERMISSION_EXCEPTION", "LOCATION PERMISSION_NOT_GRANTED 111");
            }
            try {
                try {
                    try {
                        Location gps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER); //Gps로 구한 현재 위치
                        Location network = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //인터넷으로 구한 현재 위치
                        if (gps != null) {
                            onLocationChanged(gps);
                        } else if (network != null) {
                            onLocationChanged(network);
                        } else {
                            onLocationChanged(ARData.hardFix);
                        }
                    } catch (SecurityException e) {
                        Log.e("PERMISSION_EXCEPTION", "LOCATION PERMISSION_NOT_GRANTED 2222");
                    }

                } catch (Exception ex2) {
                    onLocationChanged(ARData.hardFix);
                }
                gmf = new GeomagneticField((float) ARData.getCurrentLocation().getLatitude(), (float) ARData.getCurrentLocation().getLongitude(), (float) ARData.getCurrentLocation().getAltitude(), System.currentTimeMillis());
                angleY = Math.toRadians(-gmf.getDeclination());
                synchronized (magneticNorthCompensation) {
                    magneticNorthCompensation.toIdentity();
                    magneticNorthCompensation.set((float) Math.cos(angleY), 0f, (float) Math.sin(angleY), 0f, 1f, 0f, (float) -Math.sin(angleY), 0f, (float) Math.cos(angleY));
                    magneticNorthCompensation.prod(xAxisRotation);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex1) {
            try {
                if (sensorMgr != null) {
                    sensorMgr.unregisterListener(this, sensorGrav);
                    sensorMgr.unregisterListener(this, sensorMag);
                    sensorMgr = null;
                }
                if (locationMgr != null) {
                    try {
                        locationMgr.removeUpdates(this);
                        locationMgr = null;
                    } catch (SecurityException e) {
                        Log.e("PERMISSION_EXCEPTION", "LOCATION PERMISSION_NOT_GRANTED  333");
                    }

                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
                ;
            }
        }
    }

    protected void onStop() {
        super.onStop();
        try {
            try {
                sensorMgr.unregisterListener(this, sensorGrav);
                sensorMgr.unregisterListener(this, sensorMag);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            sensorMgr = null;
            try {
                try {
                    locationMgr.removeUpdates(this);
                } catch (SecurityException e) {
                    Log.e("PERMISSION_EXCEPTION", "LOCATION PERMISSION_NOT_GRANTED 444");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            locationMgr = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onSensorChanged(SensorEvent evt) {
        if (!computing.compareAndSet(false, true)) return;

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //반응하는 센서에 따라서 filter를 거쳐서 움직인 값을 받아옴
            smooth = LowPassFilter.filter(0.5f, 1.0f, evt.values, grav);
            grav[0] = smooth[0];
            grav[1] = smooth[1];
            grav[2] = smooth[2];
        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            smooth = LowPassFilter.filter(2.0f, 4.0f, evt.values, mag);   // 2 4
            mag[0] = smooth[0];
            mag[1] = smooth[1];
            mag[2] = smooth[2];

        }
        SensorManager.getRotationMatrix(temp, null, grav, mag);
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotation);
        worldCoord.set(rotation[0], rotation[1], rotation[2], rotation[3], rotation[4], rotation[5], rotation[6], rotation[7], rotation[8]);

        magneticCompensatedCoord.toIdentity();
        synchronized (magneticNorthCompensation) {
            magneticCompensatedCoord.prod(magneticNorthCompensation);
        }
        magneticCompensatedCoord.prod(worldCoord);

        magneticCompensatedCoord.invert();

        ARData.setRotationMatrix(magneticCompensatedCoord);

        computing.set(false);

    }

    public void onProviderDisabled(String provider) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onLocationChanged(Location location) {
        ARData.setCurrentLocation(location); //현재 위치를 업데이트 한다.

        gmf = new GeomagneticField((float) ARData.getCurrentLocation().getLatitude(), (float) ARData.getCurrentLocation().getLongitude(), (float) ARData.getCurrentLocation().getAltitude(), System.currentTimeMillis());

        double angleY = Math.toRadians(-gmf.getDeclination());

        synchronized (magneticNorthCompensation) {
            magneticNorthCompensation.toIdentity();
            magneticNorthCompensation.set((float) Math.cos(angleY), 0f, (float) Math.sin(angleY), 0f, 1f, 0f, (float) -Math.sin(angleY), 0f, (float) Math.cos(angleY));

            magneticNorthCompensation.prod(xAxisRotation);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == null) throw new NullPointerException();

        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.e(TAG, "Compass data unreliable");
        }
    }
}
