package com.zenith.scheduler.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.zenith.scheduler.functional.MessageListener;
import com.zenith.scheduler.functional.Tracker;

import static android.content.Context.LOCATION_SERVICE;


/**
 *  @author Jakub Szolomicki
 *
 * {@link GPSTracker} tracks and caches the device's location using both:
 * {@link LocationManager#GPS_PROVIDER} and {@link LocationManager#NETWORK_PROVIDER}
 *
 * Data is then accessible through the implemented {@link Tracker}
 */
public class GPSTracker implements LocationListener, Tracker<String> {

    private static final int PERMISSION_REQUEST_ID_COARSE_LOCATION = 600;
    private static final int PERMISSION_REQUEST_ID_FINE_LOCATION = 601;

    private static final String GPS_TRACKER_LOG_TAG = "[GPS_TRACKER]";

    private static final String GPS_NOT_ENABLED_ERR = "Please enable GPS and/or Mobile Carrier data transfer";
    private static final String GPS_PERMISSION_NOT_GRANTED_ERR = "Please grant the GPS permission for the application";

    private Context context;
    private LocationManager mLocationManager;
    private MessageListener<String> mMessageListener;

    private double latitude, longitude;

    private boolean initialized = false;

    /**
     * Instantiates a new GPSTracker.
     *
     * @param context          the application context
     * @param mMessageListener a message listener invoked on a GPS status change
     */
    public GPSTracker(Context context, MessageListener<String> mMessageListener)
    {
        this.context = context;
        this.mMessageListener = mMessageListener;
        this.initialize();
    }

    /**
     * Initializes the {@link GPSTracker} and it's internal GPS tracking service.
     *
     * Checks if the required permissions are granted and if not - requests them.
     */
    @SuppressLint("MissingPermission")
    public void initialize(){

        if (!hasCoarsePermission() || !hasFinePermission()) {
            mMessageListener.onMessage(GPS_PERMISSION_NOT_GRANTED_ERR);

            if(!hasFinePermission()) requestFinePermission();
            else requestCoarsePermission();

            return;
        }

        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if(mLocationManager == null)
        {
            mMessageListener.onMessage(GPS_NOT_ENABLED_ERR);
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);


        if(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null
            && mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) == null)
        {
            mMessageListener.onMessage(GPS_NOT_ENABLED_ERR);
            return;
        }

        this.initialized = true;
    }

    /**
     * @return if the {@link Manifest.permission#ACCESS_COARSE_LOCATION} permission has been
     * granted for the application
     */
    private boolean hasCoarsePermission(){
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);
       // return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
             //   != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @return if the {@link Manifest.permission#ACCESS_FINE_LOCATION} permission has been
     * granted for the application
     */
    private boolean hasFinePermission(){
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
       // return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
               // != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests the {@link Manifest.permission#ACCESS_COARSE_LOCATION} permission to the user
     */
    private void requestCoarsePermission(){

        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_ID_COARSE_LOCATION);
    }

    /**
     * Requests the {@link Manifest.permission#ACCESS_FINE_LOCATION} permission to the user
     */
    private void requestFinePermission(){
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_ID_FINE_LOCATION);
    }

    /**
     * @return if the {@link GPSTracker} has been initialized properly
     */
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void onLocationChanged(Location location) {


        if(location != null)
        {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }

        Log.i(GPS_TRACKER_LOG_TAG, String.format("Location changed: %4.3f, %4.3f", latitude, longitude));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(GPS_TRACKER_LOG_TAG, String.format("Location status changed: %d", status));
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public String getTrackerData() {
        return String.format("[Location] Latitude: %4.3f, Longitude: %4.3f", this.latitude, this.longitude);
    }
}
