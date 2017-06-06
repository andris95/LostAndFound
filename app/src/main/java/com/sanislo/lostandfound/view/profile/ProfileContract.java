package com.sanislo.lostandfound.view.profile;

import com.sanislo.lostandfound.BasePresenter;
import com.sanislo.lostandfound.BaseView;
import com.sanislo.lostandfound.model.User;

/**
 * Created by root on 06.06.17.
 */

public interface ProfileContract {
    interface Presenter extends BasePresenter {
        void getProfile(String userUID);
    }
    interface View extends BaseView<Presenter> {
        void onProfileLoaded(User user);
        void onError();
    }
}
