package com.zenith.scheduler.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Jakub Szolomicki
 *
 * Uses an interal {@link ScheduledExecutorService} with fixed ThreadPool's size set to 1
 * in order to invoke a requested {@link Runnable} {@link #task} with a target {@link #interval}
 */
public class RepeatedTaskScheduler{

    private ScheduledExecutorService executor;
    private long interval;
    private Runnable task;

    /**
     * Instantiates a new Repeated task scheduler.
     *
     * @param interval The interval between runs in seconds
     * @param task     the task to be performed with each execution
     */
    RepeatedTaskScheduler(long interval, Runnable task)
    {
        this.task = task;
        this.interval = interval;
    }

    /**
     * Initializes the {@link #executor} and schedules it's work with a requested
     * {@link #interval} using the {@link TimeUnit#SECONDS}
     */
    public void start(){

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(task, 0, interval, TimeUnit.SECONDS);
    }

    /**
     * Stop's the execution immediately discarding all of the remaining work
     */
    void stop(){

        if(executor != null)
            executor.shutdownNow();
    }
}
