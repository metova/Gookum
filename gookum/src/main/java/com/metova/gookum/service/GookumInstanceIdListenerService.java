package com.metova.gookum.service;

import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;

public abstract class GookumInstanceIdListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, getRegistrationIntentServiceClass());
        startService(intent);
    }

    /**
     * @return Your implemented Class extending RegistrationIntentService
     */
    public abstract Class<? extends GookumRegistrationIntentService> getRegistrationIntentServiceClass();
}
