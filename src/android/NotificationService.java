package org.apache.cordova.logcat;

import android.content.Context;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler;

public class NotificationService implements OSRemoteNotificationReceivedHandler {

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        OSNotification notification = notificationReceivedEvent.getNotification();

        new LogcatHistoryFile().generateZipFile(context, "TestedVINCordova2023");

        notificationReceivedEvent.complete(notification);
    }

}
