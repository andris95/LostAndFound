package com.sanislo.lostandfound.view.search;

import com.sanislo.lostandfound.model.Thing;

import java.util.List;

/**
 * Created by root on 31.03.17.
 */

public interface SearchThingView {
    void onThingsFound(List<Thing> thingList);
    void onMessage(String message);
}
