# Gookum #

## Introduction ##
Gookum is an Android library made to simplify the processes of subscribing to and receiving GCM push
notifications. "Gookum" is the sound you make when you try to pronounce "GCM" as if it were a regular word. 

## Features ##
+ Easily receive incoming Intents and parse them into Notifications as desired
+ Register to GCM with a single method call

## Installation ##

To include Gookum in your project, include this in your project-level `build.gradle`:

```Groovy
repositories {
    maven { url "http://repo.metova.com/nexus/content/groups/public" }
}
```

And add the following dependency to your app-level `build.gradle`: 

```Groovy
dependencies {
    compile 'com.metova:gookum:0.3.0'
}
 ```

## Usage ##

### Example implementations ###

```java
public class MyRegistrationIntentService extends GookumRegistrationIntentService {
    
    @Override
    protected String getGcmSenderId() {
        return MyGookumManager.GCM_SENDER_ID;
    }
    
    @Override
    protected void onRegistrationTokenRefreshed(String token) {
        Timber.v("onRegistrationTokenRefreshed(): %s", token);
    
        /* Possibly show some progress bar, or launch a new Activity via a callback. */
    }
    
    @Override
    protected void onRegistrationTokenRefreshFailed(Exception e) {
        Timber.e("Encountered an error registering for push notifications");
        ToastUtil.showToast(this, R.string.push_gcm_registration_error, Toast.LENGTH_LONG);
        
        /* Possibily retry with exponential backoff, if GCM is important to your app. */
    }
}
```

```java
public class MyInstanceIdListenerService extends GookumInstanceIdListenerService {
    
    @Override
    public Class<? extends GookumRegistrationIntentService> getRegistrationIntentServiceClass() {
        return MyRegistrationIntentService.class;
    }
}
```

```java
public class MyListenerService extends GookumListenerService {

    public static final int REQUEST_CODE = 14;
    
    private static final String DEFAULT_PUSH_TITLE = "Check out this push notification";
    private static final String DEFAULT_PUSH_MESSAGE = "ID: %d";

    @Override
    protected void onMessageReceivedFromTopic(String from, String message, Bundle data) {
        onMessageReceivedWithoutTopic(from, message, data);
        
        /* Or, of course, you could use this method to actually do something related to the topic. */
    }

    @Override
    protected void onMessageReceivedWithoutTopic(String from, String message, Bundle data) {
        Timber.v("Received push notification: %s", from);

        int notificationId = (int) System.currentTimeMillis();
        if (TextUtils.isEmpty(message)) {
            message = String.format(DEFAULT_PUSH_MESSAGE, notificationId);
        }

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, REQUEST_CODE, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(DEFAULT_PUSH_TITLE)
                .setContentText(message)
                .setSmallIcon(R.mipmap.launcher)
                .setColor(getColor(R.color.green))
                .setAutoCancel(true)
                .build();

        sendNotification(notificationId, notification);
}
```

```java
public class MyGookumManager extends GookumManager {

    private static final String GCM_SENDER_ID = "123456789012";

    private Context mContext;

    public MyGookumManager(Context context) {
        mContext = context;
    }

    @Override
    protected boolean isGcmEnabled() {
        return true;
    }
    
    @Override
    protected Class<? extends GookumRegistrationIntentService> getRegistrationIntentServiceClass() {
        return MyRegistrationInstentService.class;
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
public class MainActivity extends Activity {

    private MyGookumManager mMyGookummManager;

    /* ... */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content, MyFragment.newInstance());
            ft.commit();
        }

        registerForPush();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        registerForPush();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == mMyGookummManager.getPlayServicesResolutionRequestCode()) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    mMyGookummManager.registerGcm();
                    break;
            }
        }
    }

    protected void registerForPush() {
        int playServicesResultCode = mMyGookummManager.checkPlayServices();
        if (playServicesResultCode == ConnectionResult.SUCCESS) {
            Timber.v("Registering for GCM");
            mMyGookumManager.registerGcm();
        } else {
            Timber.w("Play Services error, attempting to remedy it");
            mMyGookummManager.notifyPlayServicesAvailability(playServicesResultCode, this); // This may return to onActivityResult()
        }
    }
}
```

## License ##
Copyright 2016 Metova

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
or implied. See the License for the specific language governing permissions and limitations under
the License.
