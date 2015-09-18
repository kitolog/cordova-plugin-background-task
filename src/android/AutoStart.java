package com.applurk.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
    private final static String TAG = "AL:AutoStart";
    AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences prefs = context.getSharedPreferences("com.applurk.plugin.BackgroundTask", context.MODE_PRIVATE);
            int refreshFrequency = prefs.getInt("refreshFrequency", 1000);
            alarm.SetAlarm(context, refreshFrequency);
        }
    }
}