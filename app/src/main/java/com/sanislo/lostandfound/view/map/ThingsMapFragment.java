package com.sanislo.lostandfound.view.map;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.sanislo.lostandfound.model.Thing;

import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 02.04.17.
 */

public class ThingsMapFragment extends SupportMapFragment implements MapView,
        ClusterManager.OnClusterItemClickListener<AbstractMarker>,
        ClusterManager.OnClusterClickListener<AbstractMarker> {
    private String TAG = ThingsMapFragment.class.getSimpleName();

    private GoogleMap mGoogleMap;
    private LatLngBounds.Builder mBoundsBuilder;
    private ClusterManager<AbstractMarker> mClusterManager;
    private MapPresenter mMapPresenter;
    private MarkerClickListener mMarkerClickListener;
    private Location mLocation;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mBoundsBuilder = new LatLngBounds.Builder();
        mMapPresenter = new MapPresenterImpl(this);
        mMapPresenter.getCurrentLocation(getActivity());
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
        mItemsClustered = true;
        if (!mAnimatedToCurrentLocation) {
            animateCamera(mLocation);
        }
        //animateCamera();
    }

    private boolean mItemsClustered;
    private boolean mAnimatedToCurrentLocation;
    @Override
    public void onLastKnownLocationFound(Location location) {
        mLocation = location;
        if (mGoogleMap != null && mItemsClustered) {
            animateCamera(location);
        }
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

    private void animateCamera(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mGoogleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                mAnimatedToCurrentLocation = true;
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
