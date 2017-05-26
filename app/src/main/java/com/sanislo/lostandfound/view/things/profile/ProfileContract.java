package com.sanislo.lostandfound.view.things.profile;


import com.sanislo.lostandfound.BasePresenter;
import com.sanislo.lostandfound.BaseView;
import com.sanislo.lostandfound.model.User;

/**
 * Created by root on 26.05.17.
 */

public interface ProfileContract {
    interface Presenter extends BasePresenter {
        void loadProfile(String userId);
    }
    interface View extends BaseView<Presenter> {
        void onProfileLoaded(User user);
        void onError();
    }
}
