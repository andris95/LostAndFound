package com.sanislo.lostandfound.view.things.profile.data.source;

import android.support.annotation.NonNull;

import com.sanislo.lostandfound.model.User;

/**
 * Created by root on 26.05.17.
 */

public interface ProfileDataSource {
    interface LoadProfileCallback {
        void onProfileLoaded(User user);
        void onError();
    }
    void loadProfile(String userId, @NonNull LoadProfileCallback loadProfileCallback);
}
