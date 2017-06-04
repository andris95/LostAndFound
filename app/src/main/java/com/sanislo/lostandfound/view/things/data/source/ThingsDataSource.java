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
    interface RemoveThingCallback {
        void onThingRemoved();
        void onError();
    }
    void loadThings(@NonNull LoadThingsCallback loadThingsCallback);
    void loadThings(@NonNull String sort,
                    @NonNull String order,
                    @NonNull int page,
                    @NonNull LoadThingsCallback loadThingsCallback);
    void removeThing(@NonNull int id, @NonNull RemoveThingCallback removeThingCallback);
    void loadThing(@NonNull String thingId, @NonNull LoadThingsCallback loadThingsCallback);
    void saveThing(@NonNull Thing thing);
}
