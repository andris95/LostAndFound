package com.sanislo.lostandfound.view.map;

import android.content.Context;

/**
 * Created by root on 02.04.17.
 */

public interface MapPresenter {
    void getThings();
    void getCurrentLocation(Context context);
}
