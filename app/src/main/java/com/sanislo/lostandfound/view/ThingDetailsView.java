package com.sanislo.lostandfound.view;

import com.sanislo.lostandfound.model.firebaseModel.Thing;

/**
 * Created by root on 15.01.17.
 */

public interface ThingDetailsView {
    void onThingLoaded(Thing thing);
    void onError(String errorMessage);
}
