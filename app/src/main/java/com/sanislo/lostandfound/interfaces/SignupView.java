package com.sanislo.lostandfound.interfaces;

/**
 * Created by root on 18.03.17.
 */

public interface SignupView {
    void onUserCreated();
    void onUserSignedIn();
    void onError(String errorMessage);
}
