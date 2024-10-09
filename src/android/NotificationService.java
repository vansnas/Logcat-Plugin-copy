package org.apache.cordova.logcat;

import android.content.Context;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService implements OSRemoteNotificationReceivedHandler {

    private static final String TAG = "NotificationReceivedHandler";

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        OSNotification notification = notificationReceivedEvent.getNotification();
        JSONObject data = notification.getAdditionalData();

        if (data == null) {
            Log.e(TAG, "Notification data is null");
            return;
        }

        String innerJsonString = data.optString("this", null);

        if (innerJsonString == null) {
            Log.e(TAG, "Expected 'this' field in additional data, but it was missing");
            return;
        }

        try {
            JSONObject innerJson = new JSONObject(innerJsonString);

            String VIN = innerJson.optString("VIN", null);
            String ClientId = innerJson.optString("ClientId", null);
            String ClientSecret = innerJson.optString("ClientSecret", null);
            String TennantId = innerJson.optString("TennantId", null);

            if (VIN != null && ClientId != null && ClientSecret != null && TennantId != null) {
                new LogcatHistoryFile().generateZipFile(context, VIN, ClientId, ClientSecret, TennantId);
            } else {
                Log.e(TAG, "Missing necessary fields for generating ZIP file");
            }

        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON from notification data", e);
        }

        notificationReceivedEvent.complete(notification);
    }
}
