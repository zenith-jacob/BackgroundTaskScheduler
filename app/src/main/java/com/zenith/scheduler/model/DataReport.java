package com.zenith.scheduler.model;


import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author Jakub Szolomicki
 *
 * End-server report data structure
 * Used by {@link com.zenith.scheduler.api.AsyncDataSender} in order to send request
 * to the requested target
 *
 * @apiNote The targetUrl property won't be serialized into the request's data packet
 */
public class DataReport {

    private transient String targetUrl;

    @SerializedName("reportData")
    private List<String> data;

    /**
     * Instantiates a new Data report.
     */
    public DataReport(){}

    /**
     * Instantiates a new Data report.
     *
     * @param targetUrl the target url
     * @param data      the data
     */
    public DataReport(String targetUrl, List<String> data) {
        this.targetUrl = targetUrl;
        this.data = data;
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public List<String> getData() {
        return data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(List<String> data) {
        this.data = data;
    }

    /**
     * Gets target url.
     *
     * @return the target url
     */
    public String getTargetUrl() {
        return targetUrl;
    }

    /**
     * Sets target url.
     *
     * @param targetUrl the target url
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
