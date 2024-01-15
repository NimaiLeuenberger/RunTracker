package com.runtracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TrackerService extends Service {
    private final IBinder binder = new LocalBinder();
    private long startTime = 0;

    public class LocalBinder extends Binder {
        public TrackerService getService() {
            return TrackerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double calculateKcal(int steps) {
        return steps * 0.05;
    }

    public void timerStart() {
        this.startTime = System.currentTimeMillis();
    }

    public double timerStop() {
        double elapsedTime = ((double) (System.currentTimeMillis() - startTime));
        startTime = 0;
        return elapsedTime;
    }
}