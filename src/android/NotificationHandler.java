package org.apache.cordova.logcat;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OSNotificationPayload;

import java.util.Map;

public class NotificationHandler extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Perform automatic actions here based on the received notification

        OSNotificationPayload payload = receivedResult.payload;

        // Extract information from the notification payload
        String title = payload.title;
        String message = payload.body;
        Map<String, String> additionalData = payload.additionalData;

        // Perform automatic actions based on the received data
        // For example, open a specific screen, update data, or trigger a functionality

        // Return true to display the modified notification
        return true;
    }

}
