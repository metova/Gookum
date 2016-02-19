package com.metova.gookum.service;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import com.metova.gookum.GookumManager;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

public abstract class GookumRegistrationIntentService extends IntentService {

    private static final String TAG = GookumRegistrationIntentService.class.getSimpleName();

    public static final String TOPICS_PREFIX = "/topics/";
    private static final String[] TOPICS_DEFAULT = {"global"};

    public GookumRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String token = getInstanceID().getToken(getGcmSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.v(TAG, "GCM registration token: " + token);

            onRegistrationTokenRefreshed(token);
            subscribeTopics(token);

            saveToken(token);
        } catch (Exception exception) {
            Log.w(TAG, "Failed to complete token refresh: " + exception.getMessage());
            onRegistrationTokenRefreshFailed(exception);

            saveToken(null);
        }
    }

    /**
     * Get the topics to which this instance should subscribe.
     *
     * @return Only "global" by default.
     */
    public String[] getTopics() {
        return TOPICS_DEFAULT;
    }

    /**
     * @return True if this Service successfully sent the registration token to the server, otherwise false.
     */
    @Deprecated
    protected boolean didSendTokenToServer() {
        return false;
    }

    protected InstanceID getInstanceID() {
        return InstanceID.getInstance(this);
    }

    //region Internal
    private void saveToken(String token) {
        GookumManager.setRegistrationToken(this, token);
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);

        for (String topic : getTopics()) {
            pubSub.subscribe(token, TOPICS_PREFIX + topic, null);
        }
    }
    //endregion

    //region Abstract methods
    /**
     * @return The "Project Number" of your API project on the Google Developers Console
     */
    protected abstract String getGcmSenderId();

    /**
     * Generally used to persist registration to a third-party server. Implement this method to associate the
     * user's GCM registration token with any server-side account maintained by your application.
     *
     * @param token The InstanceID's GCM registration token.
     */
    protected abstract void onRegistrationTokenRefreshed(String token);

    protected abstract void onRegistrationTokenRefreshFailed(Exception exception);
    //endregion
}
