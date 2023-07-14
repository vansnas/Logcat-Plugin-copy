package org.apache.cordova.logcat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationsDealer extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        new LogcatHistoryFile().generateZipFile(this, "TESTEDVIN1234");
        super.onMessageReceived(remoteMessage);

        /*if (remoteMessage.getNotification() != null) {
            String vin = remoteMessage.getNotification().getBody();
            new LogcatHistoryFile().generateZipFile(this, vin);
        }*/

    }

}
