package org.apache.cordova.logcat;

package com.example.mytestlogcat;

import android.content.Context;

import com.onesignal.*;

public class NotificationService implements OneSignal.OSRemoteNotificationReceivedHandler{

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {
        OSNotification notification = osNotificationReceivedEvent.getNotification();

        new LogcatHistoryFile().generateZipFile(context, "TestedVIN");

        osNotificationReceivedEvent.complete(notification);

    }
}
