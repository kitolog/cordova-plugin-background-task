package com.applurk.plugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import com.applurk.plugin.AlarmBroadcastReceiver;

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
import android.preference.PreferenceManager;

public class BackgroundTask extends CordovaPlugin {
    private final static String TAG = "AL:BackgroundTask";

    public static final String ACTION_ADD_TASK = "add";
    public static final String ACTION_REMOVE_TASK = "remove";
    public static final String ACTION_ENABLED_TASK = "enabled";

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
                String frequency = "";
                String url = "";
                String user = "";
                String version = "";
                int enabled = 1;

                if(argObject.has("frequency")){
                    frequency = (String) argObject.getString("frequency");
                }

                if(argObject.has("url")){
                    url = (String) argObject.getString("url");
                }

                if(argObject.has("user")){
                    user = (String) argObject.getString("user");
                }

                if(argObject.has("version")){
                    version = (String) argObject.getString("version");
                }

                if(argObject.has("enabled")){
                    enabled = (Integer) argObject.getInt("enabled");
                }

                if (ACTION_ADD_TASK.equals(action)) {
                    Log.v(TAG, "BackgroundTask received ACTION_ADD_TASK");
                    SharedPreferences prefs = cordova.getActivity().getApplicationContext().getSharedPreferences("ALBackgroundTask", cordova.getActivity().getApplicationContext().MODE_WORLD_WRITEABLE);
//                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cordova.getActivity().getApplicationContext());
//                    final SharedPreferences prefs = cordova.getActivity().getSharedPreferences("com.applurk.plugin.BackgroundTask", cordova.getActivity().MODE_PRIVATE);
                    if (prefs != null) {
                        prefs.edit().putString("user_id", user);
                        prefs.edit().putString("request_url", url);
                        prefs.edit().putString("version_id", version)
                        prefs.edit().putInt("enabled", 1).commit();
                        Log.v(TAG, "user_id");
                        Log.v(TAG, user);
                        Log.v(TAG, "request_url");
                        Log.v(TAG, url);
                        Log.v(TAG, "version_id");
                        Log.v(TAG, version);
                        Log.v(TAG, "BackgroundTask addTask SUCCESS");
                        callbackContext.success();
                        /*
                        cordova.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                        */
                    } else {
                        Log.v(TAG, "BackgroundTask SharedPreferences NULL");
                    }
                    result = true;
                } else if (ACTION_REMOVE_TASK.equals(action)) {
                    Log.v(TAG, "BackgroundTask received ACTION_REMOVE_TASK");

//                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cordova.getActivity().getApplicationContext());
                    SharedPreferences prefs = cordova.getActivity().getApplicationContext().getSharedPreferences("ALBackgroundTask", cordova.getActivity().getApplicationContext().MODE_WORLD_WRITEABLE);
                    if (prefs != null) {
                        String userId = prefs.getString("user_id", null);

                        if (userId != null) {
                            prefs.edit().remove("user_id");
                            prefs.edit().remove("request_url");
                            prefs.edit().remove("version_id");
                            Log.v(TAG, "BackgroundTask removeTask SUCCESS");
                        }else{
                            Log.v(TAG, "BackgroundTask removeTask NO TASK");
                        }
                        /*
                        cordova.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                        */
                    } else {
                        Log.v(TAG, "BackgroundTask SharedPreferences NULL");
                    }

                    result = true;
                }else if (ACTION_ENABLED_TASK.equals(action)) {
                    Log.v(TAG, "BackgroundTask received ACTION_ENABLED_TASK");

//                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cordova.getActivity().getApplicationContext());
                    SharedPreferences prefs = cordova.getActivity().getApplicationContext().getSharedPreferences("ALBackgroundTask", cordova.getActivity().getApplicationContext().MODE_WORLD_WRITEABLE);
                    if (prefs != null) {

                        prefs.edit().putInt("enabled", enabled).commit();

                        AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver();
                        alarm.CancelAlarm(cordova.getActivity().getApplicationContext());
                        alarm.SetAlarm(cordova.getActivity().getApplicationContext(), 1000);

                        Log.v(TAG, "BackgroundTask enable SUCCESS");

                    } else {
                        Log.v(TAG, "BackgroundTask SharedPreferences NULL");
                    }

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