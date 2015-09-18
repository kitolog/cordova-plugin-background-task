package com.applurk.plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.applurk.plugin.NotificationUtils;
import com.applurk.plugin.ConnectionClient;

public class PollingTask extends AsyncTask<Integer, Void, Boolean> {

    private final static String TAG = "AL:PollingTask";

    private Context currentContext;
    private String userId;
    private String versionId;
    private String requestUrl;
    private String uid;
    private int enabled;
//    private SharedPreferences prefs;
    /**
     * debug message
     */
    private String statMessage;

    public PollingTask(Context context) {
        Log.i(TAG, "CONSTRUCT");
        currentContext = context;
        setupTask();
    }

    protected void setupTask() {
        Log.i(TAG, "setupTask");
//        prefs = currentContext.getSharedPreferences("com.applurk.plugin.BackgroundTask", currentContext.MODE_PRIVATE);

    }

    protected Boolean doInBackground(Integer... params) {
        boolean result = false;

        Log.i(TAG, "doInBackground");

//        prefs = PreferenceManager.getDefaultSharedPreferences(currentContext.getApplicationContext());
        SharedPreferences prefs = currentContext.getApplicationContext().getSharedPreferences("ALBackgroundTask", currentContext.getApplicationContext().MODE_WORLD_WRITEABLE);

//        prefs = PreferenceManager.getDefaultSharedPreferences(currentContext);
        userId = prefs.getString("user_id", "");
        versionId = prefs.getString("version_id", "");
        requestUrl = prefs.getString("request_url", "");
        enabled = prefs.getInt("enabled", 0);
        uid = prefs.getString("uid", "8fh04fir7ir");
        prefs.edit().putString("uid", String.valueOf(enabled + 1));

        if ((requestUrl == null) || requestUrl.isEmpty()) {
            requestUrl = "http://flashtaxi.applurk.com/api/polling/driver";
        }

        Log.i(TAG, "userId:" + userId);
        Log.i(TAG, "versionId:" + versionId);
        Log.i(TAG, "enabled:" + String.valueOf(enabled));
        Log.i(TAG, "uid:" + uid);

        if (!isNetworkConnected()) {
            Log.e(TAG, "NOT isNetworkConnected");
            return result;
        }

        if (!isInternetAvailable()) {
            Log.e(TAG, "NOT isInternetAvailable");
            return result;
        }

        try {
            result = runPolling();
        } catch (JSONException e) {
            Log.i(TAG, "JSONException");
        }

        return result;
    }

    private boolean runPolling() throws JSONException {
        boolean result = true;
        Log.i(TAG, "Run Polling!");

//        ui: DriverService.getId(),
//                ty: 'd',
//                ts: Date.now(),
//                v: versionConfig.version
        if ((userId != null) && (versionId != null) && (enabled > 0)) {

            RequestParams requestParams = new RequestParams();
            requestParams.add("ui", userId);
            requestParams.add("v", versionId);
            requestParams.add("ty", "d");
            requestParams.add("ts", "2936423948394");
            requestParams.add("uid", uid);

            JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "onFailure.Code: " + String.valueOf(statusCode) + " Error: " + throwable.getLocalizedMessage());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
                    Log.i(TAG, "Success =" + jsonResponse.toString());

                    if (statusCode == 200) {
                        if ((jsonResponse != null) && (jsonResponse.length() > 0)) {
                            try {
                                if (jsonResponse.has("errors")) {
                                    Log.e(TAG, "Errors: " + jsonResponse.getString("errors"));
                                }

                                if (jsonResponse.has("co")) {
                                    Log.i(TAG, "jsonResponse has co!");
                                    JSONArray currentOrders = jsonResponse.getJSONArray("co");
                                    if ((currentOrders != null) && (currentOrders.length() > 0)) {
                                        //                                            co: [{id: 1435, status: "search",addressFrom: "улица Кедышко, 14Б"}]
                                        for (int i = 0; i < currentOrders.length(); i++) {

                                            JSONObject orderData = currentOrders.getJSONObject(i);

                                            if (orderData != null) {
                                                String addressFrom = orderData.getString("addressFrom");
                                                String status = orderData.getString("status");
                                                int OrderId = orderData.getInt("id");

                                                if ((addressFrom != null) && !addressFrom.isEmpty() && (status != null) && !status.isEmpty() && (OrderId > 0)) {
                                                    Log.i(TAG, "FOUND NEW ORDER!!!!!");
                                                    Log.i(TAG, addressFrom);
                                                    Log.i(TAG, status);
                                                    Log.i(TAG, String.valueOf(OrderId));
                                                    Log.i(TAG, "--------");

                                                    SharedPreferences prefs = currentContext.getApplicationContext().getSharedPreferences("ALBackgroundTask", currentContext.getApplicationContext().MODE_WORLD_WRITEABLE);
                                                    int storedOrderId  = prefs.getInt("order_id", 0);
                                                    if(OrderId != storedOrderId){
                                                        prefs.edit().putInt("order_id", OrderId);
                                                        NotificationUtils n = NotificationUtils.getInstance(currentContext);
                                                        n.createOrderNotification(addressFrom);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "PARSE Exception: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.e(TAG, "statusCode: " + String.valueOf(statusCode));
                    }
                }
            };

            jsonHttpResponseHandler.setUseSynchronousMode(false);
            ConnectionClient.getAbsolute(requestUrl, requestParams, jsonHttpResponseHandler);
        }

        return result;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            return !ipAddr.equals("");

        } catch (Exception e) {
            Log.e(TAG, "isInternetAvailable Exception " + e.getMessage());
            return false;
        }

    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Boolean result) {
    }
}