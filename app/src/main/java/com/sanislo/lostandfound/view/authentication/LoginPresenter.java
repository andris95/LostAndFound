package com.sanislo.lostandfound.view.authentication;

import android.content.Intent;

/**
 * Created by root on 26.05.17.
 */

public interface LoginPresenter {
    public static final int RC_GOOGLE_SIGNIN = 228;
    void login(String email, String password);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
