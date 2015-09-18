package com.applurk.plugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.PowerManager;

import android.util.Log;
import android.provider.Settings;
import android.widget.Toast;

import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.app.KeyguardManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

public class BackgroundTask extends CordovaPlugin {
    private final static String TAG = "AL:BackgroundTask";

    public static final String ACTION_ADD_TASK = "addTask";
    public static final String ACTION_REMOVE_TASK = "removeTask";

    /**
     * Constructor.
     */
    public BackgroundTask() {
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.v(TAG, "Init BackgroundTask");
    }

    @Override
    public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        boolean result = false;
// Shows a toast
        Log.v(TAG, "BackgroundTask received:" + action);

        try {
            JSONObject argObject = args.getJSONObject(0);

            if (argObject != null) {
                final String frequency = (String) argObject.getString("frequency");
                final String taskName = (String) argObject.getString("taskName");
                final String callback = (String) argObject.getString("callback");

                if (ACTION_ADD_TASK.equals(action)) {
                    Log.v(TAG, "BackgroundTask received ACTION_ADD_TASK");
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = cordova.getActivity();
                            SharedPreferences prefs = activity.getSharedPreferences("com.applurk.plugin.BackgroundTask", MODE_PRIVATE);
                            if (prefs != null) {
                                prefs.edit().putString("tk:" + taskName, callback).commit();
                                String task = prefs.putString("tk:" + taskName, false);
                                Log.v(TAG, "BackgroundTask addTask SUCCESS");
                                callbackContext.success();
                            } else {
                                Log.v(TAG, "BackgroundTask SharedPreferences NULL");
                            }
                        }
                    });

                    result = true;
                } else if (ACTION_REMOVE_TASK.equals(action)) {
                    Log.v(TAG, "BackgroundTask received ACTION_REMOVE_TASK");
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = cordova.getActivity();
                            SharedPreferences prefs = activity.getSharedPreferences("com.applurk.plugin.BackgroundTask", MODE_PRIVATE);
                            if (prefs != null) {
                                String task = prefs.getString("tk:" + taskName, null);

                                if (task != null) {
                                    prefs.edit().remove("tk:" + taskName)
                                    Log.v(TAG, "BackgroundTask removeTask SUCCESS");
                                }else{
                                    Log.v(TAG, "BackgroundTask removeTask NO TASK");
                                }
                            } else {
                                Log.v(TAG, "BackgroundTask SharedPreferences NULL");
                            }
                        }
                    });

                    result = true;
                } else {
                    callbackContext.error("Invalid action");
                    result = false;
                }
            } else {
                callbackContext.error("Invalid params");
                result = false;
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            result = false;
        }

        return result;
    }
}