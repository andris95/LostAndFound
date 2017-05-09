package com.sanislo.lostandfound.view.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.sanislo.lostandfound.model.Thing;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 02.04.17.
 */

public class ThingsMapFragment extends SupportMapFragment implements MapView,
        ClusterManager.OnClusterItemClickListener<AbstractMarker>,
        ClusterManager.OnClusterClickListener<AbstractMarker> {
    private String TAG = ThingsMapFragment.class.getSimpleName();
    private static final int RC_FINE_LOCATION = 789;

    private GoogleMap mGoogleMap;
    private ClusterManager<AbstractMarker> mClusterManager;
    private MapPresenter mMapPresenter;
    private MarkerClickListener mMarkerClickListener;

    public ThingsMapFragment() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mMapPresenter = new MapPresenterImpl(this);
        int permissionResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionResult != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_FINE_LOCATION);
        } else {
            getMapAsync(mOnMapReadyCallback);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_FINE_LOCATION) {
            /*if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMapAsync(mOnMapReadyCallback);
            }*/
            getMapAsync(mOnMapReadyCallback);
        }
    }

    private void setupClusterManager() {
        mClusterManager = new ClusterManager<AbstractMarker>(getActivity(), mGoogleMap);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        //SquareClusterRenderer squareClusterRenderer = new SquareClusterRenderer(getActivity(), mGoogleMap, mClusterManager);
        CircleClusterRenderer circleClusterRenderer = new CircleClusterRenderer(getActivity(), mGoogleMap, mClusterManager);
        mClusterManager.setRenderer(circleClusterRenderer);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mGoogleMap.setOnCameraIdleListener(mClusterManager);
    }

    @Override
    public void onThingsLoaded(List<Thing> thingList) {
        displayAllAvailableMarkers(thingList);
        mClusterManager.cluster();
        mMapPresenter.getCurrentLocation(getActivity());
    }
    
    @Override
    public void onLastKnownLocationFound(Location location) {
        Log.d(TAG, "onLastKnownLocationFound: ");
        animateCamera(location);
    }

    private void displayAllAvailableMarkers(List<Thing> thingList) {
        for (Thing thing : thingList) {
            if (thing.getLocation() != null) {
                displayMarker(thing);
            }
        }
    }

    private void displayMarker(Thing thing) {
        AbstractMarker abstractMarker = new AbstractMarker(thing);
        mClusterManager.addItem(abstractMarker);
    }

    private void animateCamera(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mGoogleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: ");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }
        });
    }

    @Override
    public boolean onClusterItemClick(AbstractMarker abstractMarker) {
        mMarkerClickListener.onClusterItemClick(abstractMarker);
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<AbstractMarker> cluster) {
        zoomToClustersItem(cluster);
        return true;
    }

    private void zoomToClustersItem(Cluster<AbstractMarker> cluster) {
        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface MarkerClickListener {
        void onClusterItemClick(AbstractMarker abstractMarker);
    }

    private class CircleClusterRenderer extends DefaultClusterRenderer<AbstractMarker> {
        private final String TAG = SquareClusterRenderer.class.getSimpleName();
        private CircleIconGenerator mCircleIconGenerator;
        //private IconGenerator mClusterIconGenerator;
        private CircleImageView mClusterItemImageView;

        public CircleClusterRenderer(Context context, GoogleMap map, ClusterManager<AbstractMarker> clusterManager) {
            super(context, map, clusterManager);
            mClusterItemImageView = new CircleImageView(context);
            mClusterItemImageView.setLayoutParams(new ViewGroup.LayoutParams(96, 96));
            mCircleIconGenerator = new CircleIconGenerator(getActivity().getApplicationContext());
        }

        @Override
        protected void onBeforeClusterItemRendered(AbstractMarker item, MarkerOptions markerOptions) {
            String title = item.getTitle();
            markerOptions.title(title);
        }

        @Override
        protected void onClusterItemRendered(AbstractMarker clusterItem, final Marker marker) {
            Log.d(TAG, "onClusterItemRendered: ");
            loadClusterItemImage(marker, clusterItem);
        }

        private void loadClusterItemImage(final Marker marker, AbstractMarker clusterItem) {
            Glide.with(getActivity())
                    .load(clusterItem.getThing().getPhoto())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            try {
                                mCircleIconGenerator.setImage(resource);
                                Bitmap icon = mCircleIconGenerator.makeIcon();
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    })
                    .into(96, 96);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    private class SquareClusterRenderer extends DefaultClusterRenderer<AbstractMarker> {
        private final String TAG = SquareClusterRenderer.class.getSimpleName();
        private IconGenerator mClusterItemIconGenerator;
        //private IconGenerator mClusterIconGenerator;
        private ImageView mClusterItemImageView;

        public SquareClusterRenderer(Context context, GoogleMap map, ClusterManager<AbstractMarker> clusterManager) {
            super(context, map, clusterManager);

            /*View view = LayoutInflater.from(context)
                    .inflate(R.layout.marker_coffee_shop, null);
            ivImage = (ImageView) view.findViewById(R.id.iv_coffee_shop_photo);
            mClusterItemIconGenerator = new IconGenerator(getActivity().getApplicationContext());*/

            mClusterItemImageView = new ImageView(context);
            mClusterItemImageView.setLayoutParams(new ViewGroup.LayoutParams(112, 112));
            mClusterItemIconGenerator = new IconGenerator(getActivity().getApplicationContext());
            mClusterItemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mClusterItemIconGenerator.setContentView(mClusterItemImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(AbstractMarker item, MarkerOptions markerOptions) {
            String title = item.getTitle();
            markerOptions.title(title);
        }

        @Override
        protected void onClusterItemRendered(AbstractMarker clusterItem, final Marker marker) {
            Log.d(TAG, "onClusterItemRendered: ");
            Glide.with(getActivity())
                    .load(clusterItem.getThing().getPhoto())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            try {
                                mClusterItemImageView.setImageBitmap(resource);
                                Bitmap icon = mClusterItemIconGenerator.makeIcon();
                                //marker.setIcon(BitmapDescriptorFactory.fromBitmap(resource));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                            } catch (Exception e) {
                                e.printStackTrace();
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resource));
                            }
                            return false;
                        }
                    })
                    .into(128, 128);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}
