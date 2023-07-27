import android.content.Context;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

public class NotificationService implements OneSignal.OSRemoteNotificationReceivedHandler{

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {
        OSNotification notification = osNotificationReceivedEvent.getNotification();

        new LogcatHistoryFile().generateZipFile(context, "TestedVIN");

        osNotificationReceivedEvent.complete(notification);

    }
}
