package com.sanislo.lostandfound.view.map;

import com.sanislo.lostandfound.model.Thing;

import java.util.List;

/**
 * Created by root on 02.04.17.
 */

public interface MapView {
    void onThingsLoaded(List<Thing> thingList);
}
