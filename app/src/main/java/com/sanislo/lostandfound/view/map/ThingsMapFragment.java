package com.sanislo.lostandfound.view.map;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sanislo.lostandfound.model.Thing;

import java.util.List;

/**
 * Created by root on 02.04.17.
 */

public class ThingsMapFragment extends MapFragment implements MapView {
    private String TAG = ThingsMapFragment.class.getSimpleName();

    private GoogleMap mGoogleMap;
    private LatLngBounds.Builder mBoundsBuilder;
    private MapPresenter mMapPresenter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getMapAsync(mOnMapReadyCallback);
        mBoundsBuilder = new LatLngBounds.Builder();
        mMapPresenter = new MapPresenterImpl(this);
    }

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMapPresenter.getThings();
        }
    };

    @Override
    public void onThingsLoaded(List<Thing> thingList) {
        displayAllAvailableMarkers(thingList);
        animateCamera();
    }

    private void displayAllAvailableMarkers(List<Thing> thingList) {
        for (Thing thing : thingList) {
            if (thing.getLocation() != null) {
                displayMarker(thing);
            }
        }
    }

    private void displayMarker(Thing thing) {
        MarkerOptions markerOptions = new MarkerOptions()
                .title(thing.getTitle())
                .position(thing.getLocation().getLatLng());
        mGoogleMap.addMarker(markerOptions);
        mBoundsBuilder.include(thing.getLocation().getLatLng());
    }

    private void animateCamera() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 32);
        mGoogleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });
    }
}
