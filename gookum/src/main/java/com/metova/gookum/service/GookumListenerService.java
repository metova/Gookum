package com.metova.gookum.service;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public abstract class GookumListenerService extends GcmListenerService {

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
