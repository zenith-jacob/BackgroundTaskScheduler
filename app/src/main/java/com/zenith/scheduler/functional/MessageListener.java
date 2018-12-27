package com.zenith.scheduler.functional;

/**
 * @author Jakub Szolomicki
 *
 * A generic implementation of a classical Java's Listener.
 * Provides functionality to pass messages anonymously into methods.
 *
 * @param <T> the type parameter
 */
public interface MessageListener<T>{

    /**
     * @param param The requested message parameter
     */
    void onMessage(T param);
}
