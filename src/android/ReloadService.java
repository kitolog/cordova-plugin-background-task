package com.applurk.plugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class ReloadService extends Service {
    private final static String TAG = "AL:ReloadService";
    AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver();

    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");
        SharedPreferences prefs = getSharedPreferences("com.applurk.plugin.BackgroundTask", MODE_PRIVATE);
        int refreshFrequency = prefs.getInt("refreshFrequency", 1000);
        alarm.SetAlarm(ReloadService.this, refreshFrequency);

        return START_STICKY;
    }


    public void onStart(Context context, Intent intent, int startId)
    {
        Log.d(TAG, "onStart");
        SharedPreferences prefs = getSharedPreferences("com.applurk.plugin.BackgroundTask", MODE_PRIVATE);
        int refreshFrequency = prefs.getInt("refreshFrequency", 1000);
        alarm.SetAlarm(context, refreshFrequency);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}