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

            SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.applurk.plugin.BackgroundTask", context.getApplicationContext().MODE_PRIVATE);
            String userId = prefs.getString("user_id", "");
            String versionId = prefs.getString("version_id", "");
            String requestUrl = prefs.getString("request_url", "");
            int enabled = prefs.getInt("enabled", 0);
            String uid = prefs.getString("uid", "8fh04fir7ir");

            Log.i(TAG, "userId:" + userId);
            Log.i(TAG, "versionId:" + versionId);
            Log.i(TAG, "enabled:" + String.valueOf(enabled));
            Log.i(TAG, "-------------");

//            PollingTask pollingTask = new PollingTask(context, requestUrl, userId, versionId, uid, enabled);
            PollingTask pollingTask = new PollingTask(context);
            pollingTask.execute(1);

        } catch (Exception e) {
            Log.e(TAG, "runTask exception " + e.toString());
        }
    }
}
