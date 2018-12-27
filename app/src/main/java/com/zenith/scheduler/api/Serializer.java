package com.zenith.scheduler.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Jakub Szolomicki
 *
 * Helper class providing tools for object's serialization
 */
class Serializer {

    /**
     * Creates a Gson object used to serialize data into and from JSON format.
     *
     * @return the requested {@link Gson} object
     */
    static Gson gson(){
        return new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
    }
}
