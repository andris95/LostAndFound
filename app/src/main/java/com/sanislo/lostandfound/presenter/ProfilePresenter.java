package com.sanislo.lostandfound.presenter;

import android.content.Context;
import android.content.Intent;

/**
 * Created by root on 30.03.17.
 */

public interface ProfilePresenter {
    void getProfile(String userUID);
    void updateUserAvatar(Context context, int userId);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
