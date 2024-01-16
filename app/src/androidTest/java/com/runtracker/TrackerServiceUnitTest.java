package com.runtracker;

import static org.junit.Assert.*;

import android.content.Intent;
import android.os.IBinder;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import com.runtracker.service.TrackerService;

@RunWith(AndroidJUnit4.class)
public class TrackerServiceUnitTest {
    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();

    @Test
    public void calculateKcal_isCorrect() throws TimeoutException {
        Intent serviceIntent = new Intent(ApplicationProvider.getApplicationContext(), TrackerService.class);
        IBinder binder = serviceRule.bindService(serviceIntent);
        TrackerService service = ((TrackerService.LocalBinder) binder).getService();

        double kcal = service.calculateKcal(400);
        assertEquals(20, kcal, 0);
    }

    @Test
    public void timerStart_andStop() throws TimeoutException, InterruptedException {
        Intent serviceIntent = new Intent(ApplicationProvider.getApplicationContext(), TrackerService.class);
        IBinder binder = serviceRule.bindService(serviceIntent);
        TrackerService service = ((TrackerService.LocalBinder) binder).getService();

        service.timerStart();
        Thread.sleep(5000);
        double elapsedTime = service.timerStop();
        assertTrue("Elapsed time should be close to 5000 milliseconds", elapsedTime >= 4900 && elapsedTime <= 5100);
    }
}
