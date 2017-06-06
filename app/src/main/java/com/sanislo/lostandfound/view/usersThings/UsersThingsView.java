package com.sanislo.lostandfound.view.usersThings;

import com.sanislo.lostandfound.model.Thing;

import java.util.List;

/**
 * Created by root on 30.03.17.
 */

public interface UsersThingsView {
    void onThingsLoaded(List<Thing> thingList);
}
