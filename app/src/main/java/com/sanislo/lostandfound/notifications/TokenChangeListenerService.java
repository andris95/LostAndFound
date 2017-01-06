package com.sanislo.lostandfound.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sanislo.lostandfound.utils.PreferencesManager;

/**
 * Created by root on 05.01.17.
 */

public class TokenChangeListenerService extends FirebaseInstanceIdService {
    private String TAG = "FirebaseInstanceId";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        PreferencesManager.setPushToken(getApplicationContext(), refreshedToken);
    }
}
