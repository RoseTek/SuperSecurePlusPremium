package com.example.supersecurepluspremium;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.accessibilityservice.AccessibilityServiceInfo;

public class BackgroundSecureService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v("secme","Service event - service");
        final int eventType = event.getEventType();
        String eventText = null;
        switch(eventType) {
                //case AccessibilityEvent.TYPE_VIEW_CLICKED:
                 //    break;
                //case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                //     break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                Log.v("secme","Touch start - service");
                break;
            default:
                break;
        }
        //eventText = eventText + event.getText();

    }

    @Override
    public void onInterrupt() {
        Log.v("secme","Service interrupted - service");
    }

    @Override
    public void onServiceConnected() {
        Log.v("secme","Service connected - service");
        AccessibilityServiceInfo info=getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_TOUCH_INTERACTION_START;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        this.setServiceInfo(info);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("secme","Service created - service");
    }

    /*
    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v("secme", "Service onStart - service");
    }
    */
}
