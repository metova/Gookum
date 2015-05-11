package com.metova.gookum;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public abstract class GookumIntentService extends IntentService {

    public GookumIntentService(String classSimpleName) {
        super(classSimpleName);
    }

    /**
     * Subclasses should override handleIntentWithGcm() instead of this. This handles boilerplate
     * of getting GCM instance and calling completeWakefulIntent() at the end.
     *
     * @param intent Incoming intent to handle
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        handleIntentWithGcm(intent, GoogleCloudMessaging.getInstance(this));

        GookumBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * Subclasses should implement this in order to handle incoming intents. Called by onHandleIntent(),
     * which handles some boilerplate code. This is where you will parse the content of incoming Intents
     * and send the results as Notifications.
     *
     * @param intent Incoming intent to handle
     * @param gcm Instance of GoogleCloudMessaging instance to use
     */
    protected abstract void handleIntentWithGcm(Intent intent, GoogleCloudMessaging gcm);

    /**
     * Creates and sends a basic notification. This method may be ignored if a more complicated
     * or otherwise different style of notification is required.
     *
     * @param contentIntent The intent to launch upon clicking the notification
     * @param title The first line of text in the notification
     * @param message The second line of text in the notification
     * @param smallIcon The small icon resource ID, displayed in the status bar and on the left of the notification
     * @param notificationId The ID by which Android will decide whether to show a new notification or replace an existing notification
     */
    protected void sendNotification(PendingIntent contentIntent, String title, String message, int smallIcon, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(smallIcon)
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        notificationManager.notify(notificationId, builder.build());
    }
}
