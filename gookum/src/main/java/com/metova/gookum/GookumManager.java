package com.metova.gookum;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

public abstract class GookumManager {

    private static final String TAG = GookumManager.class.getSimpleName();

    public static final int APP_VERSION_NOT_SAVED = -1;

    private GoogleCloudMessaging mGcm;

    /**
     * Allows the user to set the GoogleCloudMessaging instance used for registration and un-registration (rather than
     * using the default), which is useful primarily for testing purposes.
     * @param gcmInstance The GoogleCloudMessaging instance used to register and unregister the app. Can be
     *                    GoogleCloudMessaging.getInstance() or a mocked instance.
     */
    public void setGcmInstance(GoogleCloudMessaging gcmInstance) {
        Log.v(TAG, "setGcmInstance()");
        mGcm = gcmInstance;
    }

    /**
     * @return True if registration ID is stored and the current app version is registered, otherwise false
     */
    public boolean isRegistrationValid() {
        Log.v(TAG, "isRegistrationValid()");
        return !TextUtils.isEmpty(getGcmRegistrationId()) && (getSavedAppVersion() == getCurrentAppVersion());
    }

    /**
     * @return The current version number of the app
     */
    protected int getCurrentAppVersion() {
        Log.v(TAG, "getCurrentAppVersion()");
        try {
            Context context = getContext();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the current installation of the app with GCM.
     * @param callback The RegisterGcmCallback to call upon completion of attempted GCM registration
     */
    public void registerGcm(final RegisterGcmCallback callback) {
        Log.v(TAG, "registerGcm()");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (!isGcmEnabled()) {
                    cancel(true);
                    return null;
                }

                String registrationId;
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(getContext());
                    }
                    registrationId = mGcm.register(getGcmSenderId());
                    setGcmRegistrationId(registrationId);
                    setSavedAppVersion(getCurrentAppVersion());
                } catch (IOException e) {
                    Log.e(TAG, "GCM registration error: " + e.getMessage());
                    return null;
                }

                return registrationId;
            }

            @Override
            protected void onPostExecute(String registrationId) {
                if (TextUtils.isEmpty(registrationId)) {
                    callback.onError();
                } else {
                    callback.onGcmRegistered(registrationId);
                }
            }

            @Override
            protected void onCancelled(String registrationId) {
                Log.w(TAG, "Attempted GCM registration when GCM is not enabled");
                callback.onGcmDisabled();
            }
        }.execute();
    }

    /**
     * Unregisters the current installation of the app from GCM.
     * @param callback The UnregisterGcmCallback to call upon completion of the attempted GCM un-registration
     */
    public void unregisterGcm(final UnregisterGcmCallback callback) {
        Log.v(TAG, "unregisterGcm()");
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(getContext());
                    }
                    mGcm.unregister();
                    setGcmRegistrationId("");
                    setSavedAppVersion(APP_VERSION_NOT_SAVED);
                    return true;
                } catch (IOException e) {
                    Log.e(TAG, "GCM un-registration error: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean didSucceed) {
                if (didSucceed) {
                    callback.onGcmUnregistered();
                } else {
                    callback.onError();
                }
            }
        }.execute();
    }

    /**
     * @param activity Activity on which to possibly display an error dialog
     * @return True if the device supports Google Play Services, otherwise false
     */
    public boolean arePlayServicesEnabled(Activity activity) {
        Log.v(TAG, "arePlayServicesEnabled()");
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, getPlayServicesResolutionRequestCode()).show();
            } else {
                Log.i(TAG, "This device does not support Google Play Services.");
            }

            return false;
        }

        return true;
    }

    /**
     * If GCM is not enabled at all, this class won't try to register the app
     * @return True if push notifications are enabled in general; false otherwise
     */
    protected abstract boolean isGcmEnabled();

    /**
     * @return The "Project Number" of your API project on the Google Developers Console
     */
    protected abstract String getGcmSenderId();

    /**
     * @return The request code used to call startActivityForResult() upon a Google Play Services error
     */
    protected abstract int getPlayServicesResolutionRequestCode();

    /**
     * @return The Context of the app
     */
    protected abstract Context getContext();

    /**
     * @return The app instance's stored GCM registration ID
     */
    protected abstract String getGcmRegistrationId();

    /**
     * @param gcmRegistrationId The app instance's GCM registration ID, to store
     */
    protected abstract void setGcmRegistrationId(String gcmRegistrationId);

    /**
     * @return The version number of the app last time it registered to GCM
     */
    protected abstract int getSavedAppVersion();

    /**
     * @param appVersion The current app version, to save for checking in the future
     */
    protected abstract void setSavedAppVersion(int appVersion);

    public interface RegisterGcmCallback {

        /**
         * Called when GCM registration succeeds
         * @param registrationId The app instance's registration ID, returned by GCM
         */
        void onGcmRegistered(String registrationId);

        /**
         * Called when GCM registration fails
         */
        void onError();

        /**
         * Called when, upon attempting to register the app instance for GCM, it is determined
         * that GCM is not enabled for the app in general.
         */
        void onGcmDisabled();
    }

    public interface UnregisterGcmCallback {

        /**
         * Called when GCM un-registration succeeds
         */
        void onGcmUnregistered();

        /**
         * Called when GCM un-registration fails
         */
        void onError();
    }
}
