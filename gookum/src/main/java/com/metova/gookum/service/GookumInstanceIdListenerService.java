package com.metova.gookum.service;

import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;

public class GookumInstanceIdListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
