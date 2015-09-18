package com.applurk.plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.applurk.plugin.PollingTask;

public class AppService {

    private final static String TAG = "AL:AppService";

    public AppService() {
    }

    public void runTask(Context context) {

        Log.i(TAG, "runTask");
        try {

            PollingTask pollingTask = new PollingTask(context);
            pollingTask.execute();

        } catch (Exception e) {
            Log.e(TAG, "runTask exception " + e.toString());
        }
    }
}
