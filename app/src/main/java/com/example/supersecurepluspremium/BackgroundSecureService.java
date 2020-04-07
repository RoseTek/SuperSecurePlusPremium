package com.example.supersecurepluspremium;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.accessibilityservice.AccessibilityServiceInfo;

import androidx.annotation.Nullable;

public class BackgroundSecureService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v("secme","Service event - service");

    }

    @Override
    public void onInterrupt() {
        Log.v("secme","Service interrupted - service");
    }

    @Override
    public void onServiceConnected() {
        Log.v("secme","Service connected - service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("secme","Service created - service");
    }

}
