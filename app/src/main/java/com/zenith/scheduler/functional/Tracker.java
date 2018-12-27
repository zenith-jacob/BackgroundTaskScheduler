package com.zenith.scheduler.functional;


/**
 * @author Jakub Szolomicki
 *
 * A generic implementation of a producer pattern interface
 * Combined with {@link com.zenith.scheduler.scheduler.TaskProcessor} provides
 * and easy way to collect and manage data repeatedly from different sources
 *
 * @param <T> the type parameter
 */
public interface Tracker<T> {

    /**
     * @return the requested tracker data
     */
    T getTrackerData();
}
