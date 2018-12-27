package com.zenith.scheduler.scheduler;

import com.zenith.scheduler.functional.MessageListener;
import com.zenith.scheduler.functional.Tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Szolomicki
 *
 * A TaskProcessor retrieves it's tracker's data with an interval specified by each tracker
 * individually.
 *
 * For instance, you can set a {@link Tracker} which retrieves some API data and set the interval
 * in order to fetch continously in a given time.
 *
 * @param <T> the type parameter
 */
public class TaskProcessor<T>{

    private static final String LOG_TAG = "[TaskProcessor]";

    private DataCollector<T> dataCollector;

    private List<Tracker<T>> trackers;
    private List<RepeatedTaskScheduler> schedulers;
    private int maximumDataCapacity;

    private MessageListener<List<T>> onDataCapacityExceeded;
    private MessageListener<Integer> onDataSizeChanged;

    /**
     * Instantiates a new Task processor.
     *
     * @param maximumDataCapacity the maximum data capacity before
     * the {@link #onDataCapacityExceeded} is fired
     */
    public TaskProcessor(int maximumDataCapacity)
    {
        this.trackers = new ArrayList<>();
        this.schedulers = new ArrayList<>();
        this.maximumDataCapacity = maximumDataCapacity;
    }

    /**
     * Adds a specified {@link Tracker} to be fetched with given interval
     *
     * @param tracker  the tracker
     * @param interval the interval
     */
    public void addTracker(Tracker<T> tracker, int interval)
    {
        trackers.add(tracker);
        schedulers.add(new RepeatedTaskScheduler(interval, () -> {
            dataCollector.put(tracker.getTrackerData());
        }));
    }

    /**
     * Initializes the internal {@link DataCollector} and sets it's specified values
     * @see #onDataCapacityExceeded
     * @see #onDataSizeChanged
     *
     * Also initializes and starts the corresponding schedulers
     */
    public void start(){
        cancel();

        dataCollector = new DataCollector<>(maximumDataCapacity);

        dataCollector.setOnCapacityExceeded(onDataCapacityExceeded);
        dataCollector.setOnDataSizeChanged(onDataSizeChanged);

        new Thread(dataCollector).start();

        for(RepeatedTaskScheduler s : schedulers)
            s.start();
    }

    /**
     * Cancel the execution of the {@link DataCollector} and the corresponding task schedulers
     */
    public void cancel(){
        if(dataCollector != null) dataCollector.stop();

        for(RepeatedTaskScheduler s : schedulers)
            s.stop();
    }

    /**
     * Sets an onCapacityExceeded listener which will be invoked when
     * the {@link DataCollector}'s size exceeds the requested one in it's constructor's
     *
     * @param onDataCapacityExceeded the listener callback method
     */
    public void setOnDataCapacityExceeded(MessageListener<List<T>> onDataCapacityExceeded)
    {
        this.onDataCapacityExceeded = onDataCapacityExceeded;
    }

    /**
     * Sets an onDataSizeChanged listener which will be invoked when
     * the {@link DataCollector}'s size changes
     *
     * @param onDataSizeChanged the listener callback method
     */
    public void setOnDataSizeChanged(MessageListener<Integer> onDataSizeChanged) {
        this.onDataSizeChanged = onDataSizeChanged;
    }
}
