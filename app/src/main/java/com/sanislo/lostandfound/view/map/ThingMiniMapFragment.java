package com.sanislo.lostandfound.view.map;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sanislo.lostandfound.model.Location;
import com.sanislo.lostandfound.model.Thing;

/**
 * Created by root on 17.04.17.
 */

public class ThingMiniMapFragment extends SupportMapFragment {
    public static final String TAG = ThingMiniMapFragment.class.getSimpleName();
    public static final String EXTRA_THING = "EXTRA_THING";

    private GoogleMap mGoogleMap;
    private Thing mThing;

    public ThingMiniMapFragment() {
    }

    public static ThingMiniMapFragment newInstance(Thing thing) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_THING, thing);
        ThingMiniMapFragment fragment = new ThingMiniMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mThing = getArguments().getParcelable(EXTRA_THING);
        getMapAsync(mOnMapReadyCallback);
    }

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG, "onMapReady: ");
            mGoogleMap = googleMap;
            mGoogleMap.clear();
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            displayThingMarker();
        }
    };

    private void displayThingMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        Location location = mThing.getLocation();
        LatLng latLng = new LatLng(location.getLat(),
                location.getLng());
        markerOptions.position(latLng);
        Marker m = mGoogleMap.addMarker(markerOptions);
        Log.d(TAG, "displayThingMarker: m == null? " + (m == null));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        mGoogleMap.moveCamera(cameraUpdate);
    }
}
