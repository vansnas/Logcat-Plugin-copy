package org.apache.cordova.logcat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.onesignal.OneSignal;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.util.Log;

public class LogCat extends CordovaPlugin {

    private static final String TAG = "LogCatPlugin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("sendLogs")) {
            if (!foregroundServiceRunning()) {
                Activity activity = cordova.getActivity();
                Intent serviceIntent = new Intent(activity, MyForegroundService.class);
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For Android O and above, use startForegroundService
                    activity.getApplicationContext().startForegroundService(serviceIntent);
                } else {
                    activity.getApplicationContext().startService(serviceIntent);
                }
            }
            return true;
        } else if (action.equals("registerDevice")) {
            Activity activityCordova = cordova.getActivity();
            OneSignal.initWithContext(activityCordova);
            OneSignal.setAppId(args.getString(0));
            return true;
        } else {
            return false;
        }
    }

    // Checks if the foreground service is already running
    public boolean foregroundServiceRunning() {
        Context context = cordova.getActivity().getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
