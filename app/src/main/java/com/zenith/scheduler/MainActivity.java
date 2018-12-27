package com.zenith.scheduler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.zenith.scheduler.api.AsyncDataSender;
import com.zenith.scheduler.functional.MessageListener;
import com.zenith.scheduler.model.DataReport;
import com.zenith.scheduler.scheduler.TaskProcessor;
import com.zenith.scheduler.service.BatteryTracker;
import com.zenith.scheduler.service.GPSTracker;

/**
 * @author Jakub Szolomicki
 *
 * {@link MainActivity} is the application's main activity
 *
 * Listens for the user's input and provides methods to validate requests, start and stop processes.
 * Also handles the messages displayed to the user upon status changes.
 */
public class MainActivity extends AppCompatActivity implements MessageListener<String> {

    private static final String LOG_TAG = "[MainActivity]";
    private static final String DATA_STORAGE_SIZE_CHANGED = "Data storage size: %d";

    private EditText gpsIntervalEditText;
    private EditText batteryIntervalEditText;
    private EditText dataCapacityEditText;
    private EditText reportUrlEditText;

    private ProgressBar progressBar;

    private Button startButton, stopButton;

    private TextView statusTextView;

    private TaskProcessor<String> taskProcessor;
    private GPSTracker gpsTracker;
    private BatteryTracker batteryTracker;

    private int gpsInterval, batteryInterval, maxCapacity;
    private String reportUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        gpsTracker = new GPSTracker(this, this);
        batteryTracker = new BatteryTracker(this);
    }


    /**
     * Initializes the UI components and performs an initial setup of the controls
     */
    private void initializeViews(){
        gpsIntervalEditText = findViewById(R.id.gpsIntervalEditText);
        batteryIntervalEditText = findViewById(R.id.batteryIntervalEditText);
        dataCapacityEditText = findViewById(R.id.dataCapacityEditText);
        reportUrlEditText = findViewById(R.id.reportUrlEditText);

        statusTextView = findViewById(R.id.outputText);

        progressBar = findViewById(R.id.progressBar);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        progressBar.setVisibility(View.INVISIBLE);

        toggleButtons(true, false);

        startButton.setOnClickListener((v) -> startClicked());
        stopButton.setOnClickListener((v) -> stopClicked());
    }


    /**
     * Starts the threads execution.
     *
     * Performs a GPSTracker initialization status a validates the provided input.
     * Initializes a {@link TaskProcessor} and two {@link com.zenith.scheduler.functional.Tracker}'s:
     * {@link GPSTracker} and {@link BatteryTracker}
     */
    private void startClicked(){

        if(!gpsTracker.isInitialized())
        {
            gpsTracker.initialize();

            if(!gpsTracker.isInitialized())
                return;
        }

        if(!parseInput())
            return;

        toggleButtons(false, true);

        taskProcessor = new TaskProcessor<>(this.maxCapacity);
        taskProcessor.addTracker(gpsTracker, this.gpsInterval);
        taskProcessor.addTracker(batteryTracker, this.batteryInterval);

        taskProcessor.setOnDataCapacityExceeded(data -> {

            new AsyncDataSender(MainActivity.this)
                    .execute(new DataReport(this.reportUrl, data));

        });

        taskProcessor.setOnDataSizeChanged(size -> {
            runOnUiThread(() -> {
                statusTextView.setText(String.format(DATA_STORAGE_SIZE_CHANGED, size));
            });
        });

        taskProcessor.start();
        progressBar.setVisibility(View.VISIBLE);


        onMessage("Starting threads execution...");
    }

    /**
     * Stops the threads execution and shutdowns internal scheduled executors
     */
    private void stopClicked(){
        taskProcessor.cancel();
        progressBar.setVisibility(View.INVISIBLE);
        toggleButtons(true, false);
        onMessage("Stopping threads execution");
        statusTextView.setText("Stopped");
    }

    /**
     * Posts a {@link Toast} to the main thread in order to display given status message to the user
     * @param message
     */
    @Override
    public void onMessage(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Toggles the state of the {@link #startButton} and the {@link #stopButton}
     * @param startActive if the {@link #startButton} should be active or inactive
     * @param stopActive if the {@link #stopButton} should be active or inactive
     */
    private void toggleButtons(boolean startActive, boolean stopActive){
        startButton.setEnabled(startActive);
        stopButton.setEnabled(stopActive);
    }

    /**
     * Parses and validates the user input provided through:
     * {@link #batteryIntervalEditText}
     * {@link #gpsIntervalEditText}
     * {@link #reportUrlEditText}
     * {@link #dataCapacityEditText}
     *
     * Ensures that the provided data fits the application limits
     * @return a boolean indicating that the validation was successfull
     */
    private boolean parseInput(){

        try{
            gpsInterval = Integer.parseInt(gpsIntervalEditText.getText().toString());
            if(gpsInterval < 1 || gpsInterval >= Integer.MAX_VALUE)
                throw new Exception();
        }
        catch (Exception ex)
        {
            onMessage("Please type a correct GPS Interval value");
            return false;
        }

        try{
            batteryInterval = Integer.parseInt(batteryIntervalEditText.getText().toString());
            if(batteryInterval < 1 || batteryInterval >= Integer.MAX_VALUE)
                throw new Exception();
        }
        catch (Exception ex)
        {
            onMessage("Please type a correct Battery Interval value");
            return false;
        }

        try{
            maxCapacity = Integer.parseInt(dataCapacityEditText.getText().toString());
            if(maxCapacity < 1 || maxCapacity >= Integer.MAX_VALUE)
                throw new Exception();
        }
        catch (Exception ex)
        {
            onMessage("Please type a correct Data Capacity value");
            return false;
        }

        try{
            reportUrl = reportUrlEditText.getText().toString();
            if(reportUrl.isEmpty() || !reportUrl.toLowerCase().startsWith("http"))
                throw new Exception();
        }
        catch (Exception ex)
        {
            onMessage("Please type a correct Report URL value");
            return false;
        }

        return true;
    }
}
