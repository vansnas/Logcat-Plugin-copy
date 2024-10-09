package org.apache.cordova.logcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "LogCatReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.e(TAG, "onReceive: Null intent or action");
            return;
        }

        String action = intent.getAction();
        Log.i(TAG, "onReceive: action=" + action);

        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent serviceIntent = new Intent(context, MyForegroundService.class);
                context.startForegroundService(serviceIntent);
                Log.i(TAG, "Started foreground service: MyForegroundService");
            } else {
                Intent serviceIntent = new Intent(context, MyForegroundService.class);
                context.startService(serviceIntent);
                Log.i(TAG, "Started service: MyForegroundService");
            }
        } else {
            Log.w(TAG, "Unhandled action: " + action);
        }
    }
}
