package com.sanislo.lostandfound.view.profile.source;

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
    interface UpdateProfileCallback {
        void onProfileUpdated();
        void onError();
    }
    void loadProfile(String userId, @NonNull LoadProfileCallback loadProfileCallback);
    void updateProfile(@NonNull User user, @NonNull UpdateProfileCallback updateProfileCallback);
}
