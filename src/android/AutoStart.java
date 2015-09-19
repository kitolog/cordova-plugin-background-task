package com.applurk.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
    private final static String TAG = "AL:AutoStart";
    AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("ALBackgroundTask", context.getApplicationContext().MODE_MULTI_PROCESS);
            int refreshFrequency = prefs.getInt("refreshFrequency", 1000);
            alarm.SetAlarm(context.getApplicationContext(), refreshFrequency);
        }
    }
}