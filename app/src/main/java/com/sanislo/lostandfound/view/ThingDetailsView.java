package com.sanislo.lostandfound.view;

import com.arellomobile.mvp.MvpView;
import com.sanislo.lostandfound.model.Thing;

/**
 * Created by root on 15.01.17.
 */

public interface ThingDetailsView extends MvpView {
    void onThingLoaded(Thing thing);
    void onError(String errorMessage);
}
