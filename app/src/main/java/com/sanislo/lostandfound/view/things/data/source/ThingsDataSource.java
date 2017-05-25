package com.sanislo.lostandfound.view.things.data.source;

import android.support.annotation.NonNull;

import com.sanislo.lostandfound.model.Thing;

import java.util.List;

/**
 * Created by root on 25.05.17.
 */

public interface ThingsDataSource {
    interface LoadThingsCallback {
        void onThingsLoaded(List<Thing> thingList);
        void onDataNotAvailable();
    }
    void loadThings(@NonNull LoadThingsCallback loadThingsCallback);
    void loadThing(@NonNull String thingId, @NonNull LoadThingsCallback loadThingsCallback);
    void saveThing(@NonNull Thing thing);
}
