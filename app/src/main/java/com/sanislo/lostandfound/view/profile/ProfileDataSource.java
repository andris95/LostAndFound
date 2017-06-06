package com.sanislo.lostandfound.view.profile;

import android.support.annotation.NonNull;

import com.sanislo.lostandfound.model.User;

/**
 * Created by root on 06.06.17.
 */

public interface ProfileDataSource {
    interface LoadProfileCallback {
        void onProfileLoaded(User user);
        void onError();
    }
    void loadProfile(@NonNull String userUID, @NonNull LoadProfileCallback loadProfileCallback);
}
