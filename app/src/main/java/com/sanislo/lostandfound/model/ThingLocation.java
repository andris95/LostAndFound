package com.sanislo.lostandfound.model;

/**
 * Created by root on 01.01.17.
 */

public class ThingLocation {
    private String thingKey;
    private String key;
    private double lat;
    private double lng;

    public ThingLocation() {}

    public ThingLocation(String thingKey, String key, double lat, double lng) {
        this.thingKey = thingKey;
        this.key = key;
        this.lat = lat;
        this.lng = lng;
    }

    public String getThingKey() {
        return thingKey;
    }

    public void setThingKey(String thingKey) {
        this.thingKey = thingKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
