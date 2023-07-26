package org.apache.cordova.logcat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationServiceExtension extends FirebaseMessagingService{

    private final String TAG = "Push Notifications Dealer";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        new LogcatHistoryFile().generateZipFile(this, "TestedVIN");
    }


}
