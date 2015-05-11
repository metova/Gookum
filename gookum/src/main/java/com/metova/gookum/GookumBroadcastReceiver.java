package com.metova.gookum;

import android.app.Activity;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public abstract class GookumBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), getIntentServiceClass().getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

    /**
     * Used to launch the appropriate subclass of IntentService
     *
     * @return The class of the IntentService you are using (probably a subclass of GookumIntentService)
     */
    public abstract Class<? extends IntentService> getIntentServiceClass();
}
