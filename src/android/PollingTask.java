package com.applurk.plugin;

import android.content.Context;
import android.content.SharedPreferences;
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
    /**
     * debug message
     */
    private String statMessage;

    public PollingTask(Context context) {
        currentContext = context;
        setupTask();
    }

    protected void setupTask() {
        SharedPreferences prefs = currentContext.getSharedPreferences("com.applurk.plugin.BackgroundTask", currentContext.MODE_PRIVATE);
        userId = prefs.getString("user_id", "12");
        versionId = prefs.getString("version_id", "1.1.1");
        requestUrl = prefs.getString("request_url", "");

        if ((requestUrl == null) || requestUrl.isEmpty()) {
            requestUrl = "http://flashtaxi.applurk.com/api/polling/driver";
        }
//        http://flashtaxi.applurk.com/api/polling/driver?lo=2015-09-18+09:38:50&timeout=3000&try=1&ts=1442584773609&ty=d&ui=12&uid=default&v=1.1.0

        Log.i(TAG, "userId:" + userId);
        Log.i(TAG, "versionId:" + versionId);
    }

    protected Boolean doInBackground(Integer... params) {
        boolean result = false;

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
        if ((userId != null) && (versionId != null)) {

            RequestParams requestParams = new RequestParams();
            requestParams.add("ui", userId);
            requestParams.add("v", versionId);
            requestParams.add("ty", "d");
            requestParams.add("ts", "2936423948394");

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

                                                if ((addressFrom != null) && !addressFrom.isEmpty() && (status != null) && !status.isEmpty()) {
                                                    Log.i(TAG, "FOUND NEW ORDER!!!!!");
                                                    Log.i(TAG, addressFrom);
                                                    Log.i(TAG, status);
                                                    Log.i(TAG, "--------");

                                                    NotificationUtils n = NotificationUtils.getInstance(currentContext);
                                                    n.createOrderNotification(addressFrom);
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
            ConnectionClient.get("sync/products", requestParams, jsonHttpResponseHandler);
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