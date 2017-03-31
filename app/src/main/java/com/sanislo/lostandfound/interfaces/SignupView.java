package com.sanislo.lostandfound.interfaces;

/**
 * Created by root on 18.03.17.
 */

public interface SignupView {
    void onUserSaved();
    void onUserSignedIn();
    void onError(String errorMessage);
}
