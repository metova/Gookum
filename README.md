# Gookum #

## Introduction ##
Gookum is an Android library made to simplify the processes of subscribing to and receiving GCM push notifications.

## Features ##
+ Easily receive incoming Intents and parse them into Notifications as desired
+ Register to GCM with a single method call (same goes for unregistering)
+ Use a default GoogleCloudMessaging instance, or provide you own
+ React to registration/un-registration success and failure via callbacks

## Usage ##

### Example implementations ###

```java
public class MyIntentService extends GookumIntentService {

    public static final String TAG = MyIntentService.class.getSimpleName();

    public static final int SOME_ACTIVITY_REQUEST_CODE = 1000;

    public static final int DEFAULT_NOTIFICATION_ID = 500;

    public MyIntentService() {
        super(TAG);
    }
    
    @Override
    protected void handleIntentWithGcm(Intent intent, GoogleCloudMessaging gcm) {
        Bundle extras = intent.getExtras();
        String messageType = gcm.getMessageType(intent);

        if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
            Intent notificationIntent = new Intent(this, SomeActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, SOME_ACTIVITY_REQUEST_CODE,
                    notificationIntent, PendingIntent.FLAG_ONE_SHOT);

            sendNotification(contentIntent, "Title", "Message", R.drawable.small_icon,
                    DEFAULT_NOTIFICATION_ID);
        }
    }
}
```

```java
public class MyBroadcastReceiver extends GookumBroadcastReceiver {
    @Override
    public Class<? extends IntentService> getIntentServiceClass() {
        return MyIntentService.class;
    }
}
```

```java
public class MyGcmManager extends GookumManager {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String GCM_SENDER_ID = "123456789012";

    private Context mContext;
    private PreferenceHelper mPreferenceHelper;
    
    public MyGcmManager(Context context, PreferenceHelper preferenceHelper) {
        mContext = context;
        mPreferenceHelper = preferenceHelper;
    }

    //region GookumManager abstract method implementations
    @Override
    protected boolean isGcmEnabled() {
        return mPreferenceHelper.isPushEnabled();
    }

    @Override
    protected String getGcmSenderId() {
        return GCM_SENDER_ID;
    }

    @Override
    protected int getPlayServicesResolutionRequestCode() {
        return PLAY_SERVICES_RESOLUTION_REQUEST;
    }

    @Override
    protected Context getContext() {
        return mContext;
    }

    @Override
    protected String getGcmRegistrationId() {
        return mPreferenceHelper.getGcmRegistrationId();
    }

    @Override
    protected void setGcmRegistrationId(String registrationId) {
        mPreferenceHelper.setGcmRegistrationId(registrationId);
    }

    @Override
    protected int getSavedAppVersion() {
        return mPreferenceHelper.getAppVersion();
    }

    @Override
    protected void setSavedAppVersion(int savedAppVersion) {
        mPreferenceHelper.setAppVersion(savedAppVersion);
    }
    //endregion
}
```

### Using GookumManager ###
```java
public class MyActivity extends Activity {

    private MyGcmManager mMyGcmManager;

    /* ... */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content, MyFragment.newInstance());
            ft.commit();
        }

        if (mMyGcmManager.arePlayServicesEnabled(this) && !mMyGcmManager.isRegistrationValid()) {
            mGcmManager.registerGcm(new GookumManager.RegisterGcmCallback() {
                @Override
                public void onGcmRegistered(String registrationId) {
                    new RegisterGcmTokenTask().execute(registrationId);
                }

                @Override
                public void onError() {
                    Toast.makeText(MyActivity.this, getString(R.string.register_push_error),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onGcmDisabled() {
                    Log.e(getClass().getSimpleName(),
                            "GCM notifications are disabled; did not subscribe");
                    /* You could pop up a dialog to turn on notifications here */
                }
            });
        }
    }

    /*
     * In this example, RegisterGcmTokenTask would extend AsyncTask and do something
     * useful for the app
     */
}
```

## License ##
Copyright 2015 Metova

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
