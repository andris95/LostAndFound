package com.sanislo.lostandfound.model.firebaseModel;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by root on 01.01.17.
 */

public class ThingLocation {
    private String thingKey;
    private String address;

    private double centerLat;
    private double centerLng;

    private double southWestLat;
    private double southWestLng;

    private double northEastLat;
    private double northEastLng;

    public ThingLocation() {}

    public ThingLocation(String thingKey, String address,
                         LatLng center,
                         LatLng northEast,
                         LatLng southWest) {
        this.thingKey = thingKey;
        this.address = address;
        this.centerLat = center.latitude;
        this.centerLng = center.longitude;
        this.southWestLat = southWest.latitude;
        this.southWestLng = southWest.longitude;
        this.northEastLat = northEast.latitude;
        this.northEastLng = northEast.longitude;
    }

    public ThingLocation(String thingKey,
                         Place place) {
        this.thingKey = thingKey;
        this.address = place.getAddress().toString();
        this.centerLat = place.getLatLng().latitude;
        this.centerLng = place.getLatLng().longitude;
        if (place.getViewport() != null) {
            setNorthEastLat(place.getViewport().northeast.latitude);
            setNorthEastLng(place.getViewport().northeast.longitude);
            setSouthWestLat(place.getViewport().southwest.latitude);
            setSouthWestLng(place.getViewport().southwest.longitude);
        }
    }

    public String getThingKey() {
        return thingKey;
    }

    public void setThingKey(String thingKey) {
        this.thingKey = thingKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public void setCenterLng(double centerLng) {
        this.centerLng = centerLng;
    }

    public double getSouthWestLat() {
        return southWestLat;
    }

    public void setSouthWestLat(double southWestLat) {
        this.southWestLat = southWestLat;
    }

    public double getSouthWestLng() {
        return southWestLng;
    }

    public void setSouthWestLng(double southWestLng) {
        this.southWestLng = southWestLng;
    }

    public double getNorthEastLat() {
        return northEastLat;
    }

    public void setNorthEastLat(double northEastLat) {
        this.northEastLat = northEastLat;
    }

    public double getNorthEastLng() {
        return northEastLng;
    }

    public void setNorthEastLng(double northEastLng) {
        this.northEastLng = northEastLng;
    }

    @Override
    public String toString() {
        return "ThingLocation{" +
                "thingKey='" + thingKey + '\'' +
                ", address='" + address + '\'' +
                ", centerLat=" + centerLat +
                ", centerLng=" + centerLng +
                ", southWestLat=" + southWestLat +
                ", southWestLng=" + southWestLng +
                ", northEastLat=" + northEastLat +
                ", northEastLng=" + northEastLng +
                '}';
    }
}
