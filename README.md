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

    private static final String TAG = MyIntentService.class.getSimpleName();

    public static final int REQUEST_CODE = 14;

    private static final String DEFAULT_PUSH_TITLE = "Gookum is real";
    private static final String DEFAULT_PUSH_MESSAGE = "Check out this push notification";

    public MyIntentService() {
        super(MyIntentService.class.getSimpleName());
    }

    @Override
    protected void handleIntentWithGcm(GoogleCloudMessaging googleCloudMessaging, Intent intent) {
        Log.v(TAG, "Received push notification: " + intent.hashCode());

        Bundle extras = intent.getExtras();
        String message;
        if (extras == null) {
            message = DEFAULT_PUSH_MESSAGE;
        } else {
            message = extras.getString("message", DEFAULT_PUSH_MESSAGE);
        }

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int notificationId = (int) System.currentTimeMillis();
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentIntent(contentIntent)
                .setContentTitle(DEFAULT_PUSH_TITLE)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
                .build();

        sendNotification(notificationId, notification);
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

    public MyGcmManager(Context context) {
        mContext = context;
    }

    @Override
    protected boolean isGcmEnabled() {
        return true;
    }

    @Override
    protected String getGcmSenderId() {
        return GCM_SENDER_ID;
    }

    @Override
    protected Context getContext() {
        return mContext;
    }
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

        if (mMyGcmManager.checkIfGooglePlayServicesAreEnabled(this) && !mMyGcmManager.isRegistrationValid()) {
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
     * useful for the app; such as sending the device's registration ID to the push
     * server
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
