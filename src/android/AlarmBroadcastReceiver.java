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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.applurk.plugin.BackgroundTask", context.getApplicationContext().MODE_PRIVATE);
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
            appService.runTask(context.getApplicationContext());
            /**
             * refresh widget data
             */
            wl.release();
        }
    }

    public void SetAlarm(Context context, int seconds) {
        Log.d(TAG, "SetAlarm");

        if (seconds < 300) {
            seconds = 300;
        }

        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), seconds, pendingIntent); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context) {
        Log.d(TAG, "CancelAlarm");
        Intent intent = new Intent(context.getApplicationContext(), AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
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