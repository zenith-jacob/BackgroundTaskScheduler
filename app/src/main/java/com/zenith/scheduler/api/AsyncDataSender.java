package com.zenith.scheduler.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zenith.scheduler.functional.MessageListener;
import com.zenith.scheduler.model.DataReport;

import java.util.List;


/**
 * @author Jakub Szolomicki
 *
 * An asynchronous task making POST requests to the requested target.
 * Takes a {@link DataReport} as an AsyncTask's execute() method parameter
 * @see DataReport
 */
public class AsyncDataSender extends AsyncTask<DataReport, Void, String> {

    private static final String LOG_TAG = "[AsyncDataSender]";

    private static final String INVALID_DATA_ERR = "Invalid data provided to the async execution";

    private static final String DATA_INFO = "Sending application/json payload: %s";
    private static final String DATA_SENT_SUCCESS = "Data delivered successfully. Status: %s";
    private static final String DATA_SENT_API_ERR = "Data delivery error. Status: %s";
    private static final String DATA_SENT_INTERNAL_ERR = "Data delivery error.";

    private Gson mapper;
    private MessageListener<String> onComplete;

    /**
     * Instantiates a new Async data sender.
     *
     * @param onComplete a callback method invoked on the successfull or unsuccessfull data delivery
     *                   containing a received status message
     *
     *
     */
    public AsyncDataSender(MessageListener<String> onComplete){
        this.onComplete = onComplete;
        mapper = Serializer.gson();
    }

    @Override
    protected String doInBackground(DataReport[] reports) {

        if(reports == null || reports.length < 1)
            return INVALID_DATA_ERR;

        DataReport report = reports[0];

        final String url = report.getTargetUrl();
        final List<String> data = report.getData();

        if(url == null || url.isEmpty() || data == null)
            return INVALID_DATA_ERR;

        try{

            final String json = mapper.toJson(report);
            Log.i(LOG_TAG, String.format(DATA_INFO, json));

            HttpResponse<String> response = post(url, json);

            final String outputMsg = String.format(
                    response.getCode() == 200 ? DATA_SENT_SUCCESS : DATA_SENT_API_ERR,
                    response.getCode());

            Log.i(LOG_TAG, outputMsg);

            return outputMsg;

        }
        catch (Exception ex)
        {
            Log.e(LOG_TAG, DATA_SENT_INTERNAL_ERR, ex);
            return DATA_SENT_INTERNAL_ERR;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        onComplete.onMessage(s);
    }


    /**
     * Instantiates a new Async data sender.
     *
     * @param url a target url to send a POST request to eg. (https://someapi.net)
     * @param body an application/json encoded body string eg. "{ "data": 123 }"
     *
     * @return a HttpResponse containing the request's status code
     *
     */
    private HttpResponse<String> post(final String url, final String body) throws UnirestException {

        return Unirest.post(url)
                .header("accept", "application/json")
                .body(body)
                .asString();
    }
}
