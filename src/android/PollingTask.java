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

import java.util.Arrays;

public class PollingTask extends AsyncTask<Integer, Void, Boolean> {

    private final static String TAG = "AL:PollingTask";

    private Context currentContext;
    private String userId;
    private String versionId;
    private String requestUrl;
    private String uid;
    private int enabled;
    /**
     * debug message
     */
    private String statMessage;

    public PollingTask(Context context) {
        Log.i(TAG, "CONSTRUCT");
        currentContext = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();

        Log.i(TAG, "onPreExecute");

        SharedPreferences prefs = currentContext.getApplicationContext().getSharedPreferences("ALBackgroundTask", currentContext.getApplicationContext().MODE_MULTI_PROCESS);

        userId = prefs.getString("user_id", "");
        versionId = prefs.getString("version_id", "");
        requestUrl = prefs.getString("request_url", "");
        enabled = prefs.getInt("enabled", 0);
        uid = prefs.getString("uid", "8fh04fir7ir");
    }

    protected Boolean doInBackground(Integer... params) {
        boolean result = false;

        Log.i(TAG, "doInBackground");

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
        if ((userId != null) && (versionId != null)) {

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

                                                    String[] statuses = new String[]{"search", "search_free", "search_top", "tender"};

                                                    if (Arrays.asList(statuses).contains(status)) {
                                                        Log.i(TAG, "FOUND NEW ORDER!!!!!");
                                                        Log.i(TAG, addressFrom);
                                                        Log.i(TAG, status);
                                                        Log.i(TAG, String.valueOf(OrderId));
                                                        Log.i(TAG, "--------");

                                                        SharedPreferences prefs = currentContext.getApplicationContext().getSharedPreferences("ALBackgroundTask", currentContext.getApplicationContext().MODE_MULTI_PROCESS);
                                                        int storedOrderId = prefs.getInt("order_id", 0);
                                                        int countRepeats = prefs.getInt("count_repeats", 0);
                                                        long storedOrderTimeout = prefs.getLong("order_timeout", 0);
                                                        long currentTime = System.currentTimeMillis();
                                                        if (((OrderId != storedOrderId) || (countRepeats < 5)) && (currentTime > storedOrderTimeout)) {

                                                            if (OrderId != storedOrderId) {
                                                                countRepeats = 0;
                                                            } else {
                                                                countRepeats++;
                                                            }

                                                            SharedPreferences.Editor edit = prefs.edit();
                                                            edit.putInt("order_id", OrderId);
                                                            edit.putInt("count_repeats", countRepeats);
                                                            edit.putLong("order_timeout", System.currentTimeMillis() + 3000);
                                                            edit.apply();

                                                            Log.i(TAG, "ORDER SAVED!");
                                                            int soid = prefs.getInt("order_id", 0);
                                                            long storedNewOrderTimeout = prefs.getLong("order_timeout", 0);
                                                            if (soid > 0) {
                                                                Log.i(TAG, "Saved ORDER:");
                                                                Log.i(TAG, String.valueOf(soid));
                                                                Log.i(TAG, String.valueOf(storedNewOrderTimeout));
                                                            }

                                                            NotificationUtils n = NotificationUtils.getInstance(currentContext);
                                                            n.createOrderNotification(addressFrom);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (jsonResponse.has("fo")) {
                                    Log.i(TAG, "jsonResponse has fo!");
                                    JSONArray freeOrders = jsonResponse.getJSONArray("fo");
                                    if ((freeOrders != null) && (freeOrders.length() > 0)) {
                                        for (int i = 0; i < freeOrders.length(); i++) {

                                            JSONObject freeOrderData = freeOrders.getJSONObject(i);

                                            if (freeOrderData != null) {
                                                String addressFrom = freeOrderData.getString("addressFrom");
                                                String status = freeOrderData.getString("status");
                                                int OrderId = freeOrderData.getInt("id");

                                                if ((addressFrom != null) && !addressFrom.isEmpty() && (status != null) && !status.isEmpty() && (OrderId > 0)) {

                                                    String[] statuses = new String[]{"search", "search_free", "search_top", "tender"};

                                                    if (Arrays.asList(statuses).contains(status)) {
                                                        Log.i(TAG, "FOUND NEW FREE ORDER!!!!!");
                                                        Log.i(TAG, addressFrom);
                                                        Log.i(TAG, status);
                                                        Log.i(TAG, String.valueOf(OrderId));
                                                        Log.i(TAG, "--------");

                                                        SharedPreferences prefs = currentContext.getApplicationContext().getSharedPreferences("ALBackgroundTask", currentContext.getApplicationContext().MODE_MULTI_PROCESS);
                                                        long storedOrderTimeout = prefs.getLong("free_order_timeout", 0);
                                                        long currentTime = System.currentTimeMillis();

                                                        String foids = prefs.getString("free_ids", "");
                                                        if (foids.isEmpty() || !foids.contains(String.valueOf(OrderId))) {

                                                            foids += "-" + String.valueOf(OrderId);
                                                            SharedPreferences.Editor edit = prefs.edit();
                                                            edit.putString("free_ids", foids);

                                                            Log.i(TAG, "FREE ORDER SAVED!");

                                                            if (currentTime > storedOrderTimeout) {
                                                                edit.putLong("free_order_timeout", System.currentTimeMillis() + 3000);

                                                                Log.i(TAG, "FREE ORDER TIMEOUT SAVED!");

                                                                NotificationUtils n = NotificationUtils.getInstance(currentContext);
                                                                n.createFreeOrderNotification(addressFrom);
                                                            }

                                                            edit.apply();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Log.i(TAG, "fo lenght = 0 in response");
                                        SharedPreferences prefs = currentContext.getApplicationContext().getSharedPreferences("ALBackgroundTask", currentContext.getApplicationContext().MODE_MULTI_PROCESS);
                                        String foids = prefs.getString("free_ids", "");
                                        if (!foids.isEmpty()) {
                                            SharedPreferences.Editor edit = prefs.edit();
                                            edit.remove("free_ids");
                                            edit.apply();
                                            Log.i(TAG, "Removed FOIDS!");
                                        }
                                    }
                                } else {
                                    Log.i(TAG, "NO fo in response");
                                    SharedPreferences prefs = currentContext.getApplicationContext().getSharedPreferences("ALBackgroundTask", currentContext.getApplicationContext().MODE_MULTI_PROCESS);
                                    String foids = prefs.getString("free_ids", "");
                                    if (!foids.isEmpty()) {
                                        SharedPreferences.Editor edit = prefs.edit();
                                        edit.remove("free_ids");
                                        edit.apply();
                                        Log.i(TAG, "Removed FOIDS!");
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