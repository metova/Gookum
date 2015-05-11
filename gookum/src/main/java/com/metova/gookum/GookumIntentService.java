package com.metova.gookum;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public abstract class GookumIntentService extends IntentService {

    /**
     * Constructor simply calls super (IntentService) constructor.
     * @param classSimpleName Used to name the worker thread, important only for debugging
     */
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
     * Creates a basic NotificationCompat.Builder, while allowing the user to add to it.
     * @param contentIntent The intent to launch upon clicking the notification
     * @param title The first line of text in the notification
     * @param message The second line of text in the notification
     * @param smallIcon The small icon resource ID, displayed in the status bar and on the left of the notification
     */
    protected NotificationCompat.Builder createNotificationBuilder(PendingIntent contentIntent, String title,
            String message, int smallIcon, NotificationCompat.Style style) {

        return new NotificationCompat.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(smallIcon)
                .setStyle(style);
    }

    /**
     * Requires API level 16. Creates a basic Notification.Builder, while allowing the user to add to it.
     * @param contentIntent The intent to launch upon clicking the notification
     * @param title The first line of text in the notification
     * @param message The second line of text in the notification
     * @param smallIcon The small icon resource ID, displayed in the status bar and on the left of the notification
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected Notification.Builder sendNotification(PendingIntent contentIntent, String title,
            String message, int smallIcon, Notification.Style style) {

        return new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(smallIcon)
                .setStyle(style);
    }

    /**
     * Sends the provided Notification.
     * @param notificationId The ID by which Android will decide whether to show a new notification or replace an existing notification
     * @param notification The Notification to send
     */
    protected void sendNotification(int notificationId, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }
}
