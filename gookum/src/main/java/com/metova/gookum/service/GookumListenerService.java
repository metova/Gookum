package com.metova.gookum.service;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public abstract class GookumListenerService extends GcmListenerService {

    private static final String TAG = GookumListenerService.class.getSimpleName();

    protected static final String DATA_KEY_MESSAGE = "message";

    protected static final String PREFIX_TOPICS = "/topics/";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString(DATA_KEY_MESSAGE);

        Log.v(TAG, "onMessageReceived(): from = " + from);
        Log.v(TAG, "onMessageReceived(): message = " + message);

        if (from.startsWith(PREFIX_TOPICS)) {
            onMessageReceivedFromTopic(from, message, data);
        } else {
            onMessageReceivedWithoutTopic(from, message, data);
        }
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

    protected abstract void onMessageReceivedFromTopic(String from, String message, Bundle data);

    protected abstract void onMessageReceivedWithoutTopic(String from, String message, Bundle data);
}
