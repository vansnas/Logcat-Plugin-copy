package org.apache.cordova.logcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        if(message == "LOGCATTTTTTTTTT") {
            new LogcatHistoryFile().generateZipFile(context, "TESTEDVIN1234");
        }
    }
}
