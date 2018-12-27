package com.zenith.scheduler.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.zenith.scheduler.functional.Tracker;

/**
 * @author Jakub Szolomicki
 *
 * {@link BatteryTracker} tracks the device's percentage battery level
 * Data is then accessible through the implemented {@link Tracker}
 */
public class BatteryTracker extends BroadcastReceiver implements Tracker<String>{

    private Context context;
    private int batteryLevel = -1;

    /**
     * Instantiates a new Battery tracker.
     *
     * @param context the application context
     */
    public BatteryTracker(Context context)
    {
        this.context = context;
        context.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
    }

    /**
     * Returns a cached battery level
     *
     * @return the battery level
     */
    public int getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * Sets a cached battery level
     *
     * @param batteryLevel the battery level
     */
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }


    @Override
    public String getTrackerData() {
        return String.format("[Battery] Level: %d", this.batteryLevel);
    }
}
