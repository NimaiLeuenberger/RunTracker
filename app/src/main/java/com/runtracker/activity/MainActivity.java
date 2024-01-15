package com.runtracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runtracker.R;
import com.runtracker.service.TrackerService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private TextView stepsTextView;
    private TextView kcalTextView;
    private Button resetBtn;
    private Button saveBtn;
    private Button runningOverviewBtn;
    private int stepCount = 0;
    TrackerService trackerService;
    boolean isBound = false;
    boolean timerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepsTextView = findViewById(R.id.steps_textview);
        kcalTextView = findViewById(R.id.kcal_textview);
        resetBtn = findViewById(R.id.reset_button);
        saveBtn = findViewById(R.id.save_button);
        runningOverviewBtn = findViewById(R.id.running_overview_button);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            stepsTextView.setText("Step sensor not found!");
        }

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsTextView.setText("0");
                kcalTextView.setText("0");
                stepCount = 0;
                timerStarted = false;
                if (isBound) trackerService.timerStop();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    String kcalText = kcalTextView.getText().toString();
                    kcalText = kcalText.replace(',', '.');
                    double kcalValue = Double.parseDouble(kcalText);
                    if (Integer.parseInt(String.valueOf(stepsTextView.getText())) > 0 &&
                            kcalValue > 0) {
                        Intent intent = new Intent(MainActivity.this, RunningOverviewActivity.class);
                        intent.putExtra("date", new SimpleDateFormat("dd.MM.yy").format(new Date()));
                        intent.putExtra("steps", stepsTextView.getText());
                        intent.putExtra("kcal", kcalTextView.getText());
                        intent.putExtra("time",  formatTime((long) trackerService.timerStop()));
                        resetBtn.performClick();
                        startActivity(intent);
                    }
                }
            }
        });

        runningOverviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RunningOverviewActivity.class);
                startActivity(intent);
            }
        });
    }

    public String formatTime(long totalMillis) {
        long hours = totalMillis / 3600000;
        long minutes = (totalMillis % 3600000) / 60000;
        long seconds = (totalMillis % 60000) / 1000;
        long millis = totalMillis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (stepCount == 0) {
                stepCount = (int) event.values[0];
            }

            stepsTextView.setText(String.valueOf((int) event.values[0] - stepCount));
            if (isBound) kcalTextView.setText(
                    String.format("%.2f",
                            trackerService.calculateKcal((int) event.values[0] - stepCount)
                    )
            );
            if (Integer.valueOf(String.valueOf(stepsTextView.getText())) > 0 && !timerStarted && isBound) {
                trackerService.timerStart();
                timerStarted = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //sensorManager.unregisterListener(this);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TrackerService.LocalBinder binder = (TrackerService.LocalBinder) service;
            trackerService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TrackerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}