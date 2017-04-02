package com.sanislo.lostandfound.view.map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.sanislo.lostandfound.model.Thing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 02.04.17.
 */

public class ThingsMapFragment extends SupportMapFragment implements MapView,
        ClusterManager.OnClusterItemClickListener<AbstractMarker>, ClusterManager.OnClusterClickListener<AbstractMarker> {
    private String TAG = ThingsMapFragment.class.getSimpleName();

    private GoogleMap mGoogleMap;
    private LatLngBounds.Builder mBoundsBuilder;
    private Map<String, Thing> mMarkers = new HashMap<>();
    private ClusterManager<AbstractMarker> mClusterManager;
    private MapPresenter mMapPresenter;
    private MarkerClickListener mMarkerClickListener;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mBoundsBuilder = new LatLngBounds.Builder();
        mMapPresenter = new MapPresenterImpl(this);
        getMapAsync(mOnMapReadyCallback);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mMarkerClickListener = (MarkerClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MarkerClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleMap != null) {

        }
    }

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.clear();
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            setupClusterManager();
            mMapPresenter.getThings();
        }
    };

    private void setupClusterManager() {
        mClusterManager = new ClusterManager<AbstractMarker>(getActivity(), mGoogleMap);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setRenderer(new DefaultClusterRenderer<AbstractMarker>(getActivity(), mGoogleMap, mClusterManager));
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mGoogleMap.setOnCameraIdleListener(mClusterManager);
    }

    @Override
    public void onThingsLoaded(List<Thing> thingList) {
        displayAllAvailableMarkers(thingList);
        mClusterManager.cluster();
        //animateCamera();
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
        AbstractMarker abstractMarker = new AbstractMarker(thing);
        mClusterManager.addItem(abstractMarker);
    }

    private void animateCamera() {
        LatLngBounds latLngBounds = mBoundsBuilder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 32);
        mGoogleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public boolean onClusterItemClick(AbstractMarker abstractMarker) {
        /*Toast.makeText(getActivity(), "Cluster item click", Toast.LENGTH_SHORT).show();
        ThingBottomSheet thingBottomSheet = ThingBottomSheet.newInstance(abstractMarker.getThing());
        thingBottomSheet.show(getActivity().getSupportFragmentManager(), ThingBottomSheet.class.getCanonicalName());
        */
        mMarkerClickListener.onClusterItemClick(abstractMarker);
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<AbstractMarker> cluster) {
        Toast.makeText(getActivity(), cluster.getSize() + "", Toast.LENGTH_SHORT).show();
        Iterator<AbstractMarker> it = cluster.getItems().iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.append(it.next().getTitle() + " / ");
        }
        Log.d(TAG, "onClusterClick: " + sb.toString());
        return true;
    }

    public interface MarkerClickListener {
        void onClusterItemClick(AbstractMarker abstractMarker);
    }
}
