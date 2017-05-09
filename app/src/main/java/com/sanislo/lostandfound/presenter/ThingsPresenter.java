package com.sanislo.lostandfound.presenter;

import android.content.Context;

/**
 * Created by root on 16.03.17.
 */

public interface ThingsPresenter {
    //void getThings();
    void getThings(int page);
    void getProfile(Context context);
}
