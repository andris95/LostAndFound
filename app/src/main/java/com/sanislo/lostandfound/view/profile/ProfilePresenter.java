package com.sanislo.lostandfound.view.profile;

import android.util.Log;

import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.view.profile.source.ProfileDataSource;

/**
 * Created by root on 26.05.17.
 */

public class ProfilePresenter implements ProfileContract.Presenter {
    public static final String TAG = ProfilePresenter.class.getSimpleName();

    private ProfileRepository mProfileRepository;
    private ProfileContract.View mView;

    public ProfilePresenter(ProfileContract.View view) {
        mProfileRepository = new ProfileRepository();
        mView = view;
    }

    @Override
    public void loadProfile(String userId) {
        mProfileRepository.loadProfile(userId, new ProfileDataSource.LoadProfileCallback() {
            @Override
            public void onProfileLoaded(User user) {
                mView.onProfileLoaded(user);
            }

            @Override
            public void onError() {
                mView.onError();
            }
        });
    }

    @Override
    public void updateProfile(User user) {
        mProfileRepository.updateProfile(user, new ProfileDataSource.UpdateProfileCallback() {
            @Override
            public void onProfileUpdated() {
                Log.d(TAG, "onProfileUpdated: ");
                mView.onProfileUpdated();
            }

            @Override
            public void onError() {
                mView.onError();
            }
        });
    }
}
