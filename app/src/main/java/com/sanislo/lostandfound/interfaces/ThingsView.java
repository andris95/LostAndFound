package com.sanislo.lostandfound.interfaces;


import com.sanislo.lostandfound.model.Thing;

import java.util.List;

/**
 * Created by root on 28.12.16.
 */

public interface ThingsView {
    void onThingsLoaded(List<Thing> thingList);
}
