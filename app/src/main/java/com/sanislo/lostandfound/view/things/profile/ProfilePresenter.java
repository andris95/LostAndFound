package com.sanislo.lostandfound.view.things.profile;

import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.view.things.profile.data.source.ProfileDataSource;

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
}
