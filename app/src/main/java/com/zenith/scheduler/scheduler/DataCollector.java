package com.zenith.scheduler.scheduler;

import android.util.Log;

import com.zenith.scheduler.functional.MessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jakub Szolomicki
 *
 * A generic thread-safe container collects the data from multiple sources.
 * Uses a {@link BlockingQueue} in order to handle put() requests.
 * @see DataCollector#put(Object)}
 *
 * Provides two interfaces in order to listen on container events:
 * @see DataCollector#onDataSizeChanged invoked whenever there's a change in the container's size
 * @see DataCollector#onCapacityExceeded launched whenthe container's storedData size exceeds the
 * requested {@link DataCollector#capacity}
 *
 * @param <T> the type parameter
 */
public class DataCollector<T> implements Runnable{

    private static final String LOG_TAG = "[DataCollector]";
    private static final String DATA_STORAGE_SIZE_CHANGED = "Data storage size: %d of %d";
    private static final String DATA_STORAGE_CAP_EXCEEDED = "Data storage capacity exceeded";

    private BlockingQueue<T> queue;
    private List<T> storedData;
    private MessageListener<List<T>> onCapacityExceeded;
    private MessageListener<Integer> onDataSizeChanged;

    private AtomicBoolean running;
    private int capacity;

    /**
     * Instantiates a new Data collector.
     *
     * @param capacity The maximum capacity. If exceeded, a {@link #onCapacityExceeded} method will
     *                 be invoked
     */
    DataCollector(int capacity){
        this.queue = new LinkedBlockingQueue<>();
        this.storedData = new ArrayList<>();
        this.running = new AtomicBoolean(true);
        this.capacity = capacity;
    }

    @Override
    public void run() {
        while(running.get())
        {
            try {
                storedData.add(queue.take());

                if(onDataSizeChanged != null)
                    onDataSizeChanged.onMessage(storedData.size());

                Log.i(LOG_TAG, String.format(DATA_STORAGE_SIZE_CHANGED, storedData.size(), capacity));

                if(storedData.size() >= this.capacity)
                {
                    Log.i(LOG_TAG, DATA_STORAGE_CAP_EXCEEDED);

                    if(onCapacityExceeded != null)
                        onCapacityExceeded.onMessage(new ArrayList<>(storedData));

                    storedData.clear();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops the DataCollector's execution by setting the running variable.
     * The next update will not be performed after this operation
     */
    void stop(){
        running.set(false);
    }

    /**
     * Puts the requested data into the BlockingQueue
     *
     * @param data the data
     */
    void put(T data)
    {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets an onCapacityExceeded listener which will be invoked when
     * the {@link #storedData} size exceeds the requested one in a constructor's {@link #capacity}
     *
     * @param onCapacityExceeded the listener callback method
     */
    void setOnCapacityExceeded(MessageListener<List<T>> onCapacityExceeded) {
        this.onCapacityExceeded = onCapacityExceeded;
    }

    /**
     * Sets on data size changed.
     *
     * @param onDataSizeChanged the listener callback method
     */
    void setOnDataSizeChanged(MessageListener<Integer> onDataSizeChanged) {
        this.onDataSizeChanged = onDataSizeChanged;
    }
}
