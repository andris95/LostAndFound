package com.sanislo.lostandfound.view.authentication;

import com.sanislo.lostandfound.model.User;

/**
 * Created by root on 26.05.17.
 */

public interface LoginView {
    void onUserSignedIn(User user);
    void onError(String message);
    void onError(int errorMessageResId);
}
