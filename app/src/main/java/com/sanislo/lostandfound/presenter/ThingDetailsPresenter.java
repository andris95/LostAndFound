package com.sanislo.lostandfound.presenter;

import com.sanislo.lostandfound.model.Thing;

/**
 * Created by root on 15.01.17.
 */

public interface ThingDetailsPresenter {
    void addComment(Thing thing, String text);
    void onPause();
    void onResume();
}
