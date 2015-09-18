package com.applurk.plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppService {

    private final static String TAG = "AL:AppService";

    public AppService() {
    }

    public void runTask(Context context) {

        Log.i(TAG, "runTask");

        try {

        } catch (Exception e) {
            Log.e(TAG, "runTask exception " + e.toString());
        }
    }
}
