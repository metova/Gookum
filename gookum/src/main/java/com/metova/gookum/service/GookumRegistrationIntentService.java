package com.metova.gookum.service;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import com.metova.gookum.GookumManager;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

public abstract class GookumRegistrationIntentService extends IntentService {

    private static final String TAG = GookumRegistrationIntentService.class.getSimpleName();

    public static final String PREFERENCE_DID_SEND_TOKEN_TO_SERVER = "SENT_TOKEN_TO_SERVER";

    public static final String TOPICS_PREFIX = "/topics/";
    private static final String[] TOPICS_DEFAULT = {"global"};

    private SharedPreferences mSharedPreferences;

    public GookumRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mSharedPreferences = GookumManager.getGookumSharedPreferences(this);

        try {
            InstanceID instanceId = InstanceID.getInstance(this);
            String token = instanceId.getToken(getGcmSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.v(TAG, "GCM registration token: " + token);

            onRegistrationTokenRefreshed(token);
            subscribeTopics(token);

            setDidSendTokenToServer(true);
        } catch (Exception exception) {
            Log.w(TAG, "Failed to complete token refresh: " + exception.getMessage());
            onRegistrationTokenRefreshFailed(exception);

            setDidSendTokenToServer(false);
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
    protected boolean didSendTokenToServer() {
        return mSharedPreferences.getBoolean(PREFERENCE_DID_SEND_TOKEN_TO_SERVER, false);
    }

    //region Internal
    private void setDidSendTokenToServer(boolean didSendTokenToServer) {
        mSharedPreferences.edit()
                .putBoolean(PREFERENCE_DID_SEND_TOKEN_TO_SERVER, didSendTokenToServer)
                .apply();
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : getTopics()) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
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
