package com.sanislo.lostandfound.view.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.sanislo.lostandfound.model.Thing;

/**
 * Created by root on 02.04.17.
 */

public class AbstractMarker implements ClusterItem {
    private String title;
    private String description;
    private LatLng latLng;
    private Thing thing;
    protected MarkerOptions marker;

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return description;
    }

    public AbstractMarker(Thing thing) {
        this.title = thing.getTitle();
        this.description = thing.getDescription();
        this.latLng = thing.getLocation().getLatLng();
        this.thing = thing;
    }

    public Thing getThing() {
        return thing;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }
    //others getters & setters
}