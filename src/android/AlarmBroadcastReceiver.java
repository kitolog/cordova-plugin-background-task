package com.applurk.plugin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import com.applurk.plugin.AppService;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private final static String TAG = "AL:Alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.applurk.plugin.BackgroundTask", context.getApplicationContext().MODE_PRIVATE);
        int refreshStatus = prefs.getInt("refreshStatus", 1);
        if (refreshStatus == 0) {
            Log.d(TAG, "refreshStatus == 0. Cancel alarm");
            Log.d(TAG, String.valueOf(refreshStatus));
            AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver();
            alarm.CancelAlarm(context.getApplicationContext());
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();

            AppService appService = new AppService();
            /**
             * refresh servers data
             */
            appService.runTask(context);
            /**
             * refresh widget data
             */
            wl.release();
        }
    }

    public void SetAlarm(Context context, int intervalIndex) {
        Log.d(TAG, "SetAlarm");
        int minutes = 1;
        switch (intervalIndex) {
            case 0:
                minutes = minutes * 10;
                break;

            case 1:
                minutes = minutes * 30;
                break;

            case 2:
                minutes = minutes * 60;
                break;

            case 3:
                minutes = minutes * 60 * 2;
                break;

            case 4:
                minutes = minutes * 60 * 3;
                break;

            case 5:
                minutes = minutes * 60 * 5;
                break;

            case 6:
                minutes = minutes * 60 * 7;
                break;

            case 7:
                minutes = minutes * 60 * 10;
                break;

            case 8:
                minutes = minutes * 60 * 15;
                break;

            case 9:
                minutes = minutes * 60 * 24;
                break;

            case 10:
                minutes = minutes * 60 * 24 * 3;
                break;
            default:
                minutes = minutes * 30;
                break;
        }

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * minutes, pendingIntent); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context) {
        Log.d(TAG, "CancelAlarm");
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public boolean isAlarmRunning(Context context) {

        Log.d(TAG, "isAlarmRunning");
        boolean isAlarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent(context, AlarmBroadcastReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        return isAlarmUp;
    }
}