package com.metova.gookum.service;

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
import android.support.v4.content.WakefulBroadcastReceiver;

@Deprecated
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
    protected final void onHandleIntent(Intent intent) {
        handleIntentWithGcm(GoogleCloudMessaging.getInstance(this), intent);

        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * Subclasses should implement this in order to handle incoming intents. Called by onHandleIntent(),
     * which handles some boilerplate code. This is where you will parse the content of incoming Intents
     * and send the results as Notifications.
     *
     * @param gcm Instance of GoogleCloudMessaging instance to use
     * @param intent Incoming intent to handle
     */
    protected abstract void handleIntentWithGcm(GoogleCloudMessaging gcm, Intent intent);

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
