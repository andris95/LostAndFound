package com.sanislo.lostandfound.view.profile;

import android.content.Intent;

/**
 * Created by root on 30.03.17.
 */

public interface ProfilePresenter {
    void getProfile(String userUID);
    void updateProfile();
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
