package com.sanislo.lostandfound.view;

/**
 * Created by root on 18.03.17.
 */

public interface SignupView {
    void onUserCreated();
    void onUserSignedIn();
    void onError(String errorMessage);
}
