package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "LogCatReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: action=" + intent.getAction());
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, MyForegroundService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
