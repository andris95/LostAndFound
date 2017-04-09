package com.sanislo.lostandfound.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by root on 09.04.17.
 */

public class LocationUtils {

    public static final float distanceTo(Location from, Location to) {
        float distanceTo = from.distanceTo(to);
        return distanceTo;
    }

    public static final float distanceTo(LatLng latLngFrom, LatLng latLngTo) {
        Location from = new Location("");
        from.setLatitude(latLngFrom.latitude);
        from.setLongitude(latLngFrom.longitude);
        Location dest = new Location("");
        dest.setLongitude(latLngTo.longitude);
        dest.setLatitude(latLngTo.latitude);

        float distanceTo = from.distanceTo(dest);
        return distanceTo;
    }
}
