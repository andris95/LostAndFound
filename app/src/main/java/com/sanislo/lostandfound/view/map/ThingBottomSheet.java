package com.sanislo.lostandfound.view.map;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.DescriptionPhotosAdapter;
import com.sanislo.lostandfound.model.Location;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.thingDetails.DescriptionPhotosActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 02.04.17.
 */

public class ThingBottomSheet extends BottomSheetDialogFragment {
    private String TAG = ThingBottomSheet.class.getSimpleName();

    @BindView(R.id.iv_thing_author_avatar)
    ImageView ivAuthorAvatar;

    @BindView(R.id.iv_thing_photo)
    ImageView ivThingPhoto;

    @BindView(R.id.tv_thing_title)
    TextView tvTitle;

    @BindView(R.id.tv_thing_type)
    TextView tvType;

    @BindView(R.id.tv_thing_description)
    TextView tvDescription;

    @BindView(R.id.rl_thing_description)
    RelativeLayout rlDescription;

    @BindView(R.id.rv_things_photos)
    RecyclerView rvDescriptionPhotos;

    @BindView(R.id.rv_things_comments)
    RecyclerView rvComments;

    @BindView(R.id.edt_thing_comment)
    EditText edtComment;

    @BindView(R.id.fl_map_container)
    FrameLayout flMapContainer;

    public static final String EXTRA_START_POSITION = "EXTRA_START_POSITION";
    public static final String EXTRA_UPDATED_POSITION = "EXTRA_UPDATED_POSITION";
    public static final String EXTRA_THING = "EXTRA_THING";

    private Thing mThing;
    private GoogleMap mThingMiniMap;
    private SupportMapFragment mMapFragment;
    private DescriptionPhotosAdapter mDescriptionPhotosAdapter;

    public ThingBottomSheet() {
    }

    public static ThingBottomSheet newInstance(Thing thing) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_THING, thing);
        ThingBottomSheet fragment = new ThingBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            Log.d(TAG, "onStateChanged: " + newState);
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                Log.d(TAG, "onStateChanged: hidden, dismiss");
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThing = getArguments().getParcelable(EXTRA_THING);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_thing, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        ButterKnife.bind(this, contentView);
        displayThing();
    }

    private void displayThing() {
        setTitle();
        setDescription();
        setTypeAndDate();
        setAuthorPhoto();
        setThingPhoto();
        setDescriptionPhotos();
        setComments();
        setMap();
    }

    /** check if thing has location */
    private boolean hasLocation() {
        return mThing.getLocation() != null;
    }

    private void setMap() {
        if (hasLocation()) {
            initMapView();
        } else {
            hideMapView();
        }
    }

    private void initMapView() {
        if (mMapFragment == null) {
            GoogleMapOptions googleMapOptions = new GoogleMapOptions();
            googleMapOptions.liteMode(true);
            mMapFragment = SupportMapFragment.newInstance(googleMapOptions);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_map_container, mMapFragment);
            ft.commit();
            mMapFragment.getMapAsync(mOnMapReadyCallback);
        } else {
            displayThingMarker();
        }
    }

    private void hideMapView() {
        flMapContainer.setVisibility(View.GONE);
        if (mThingMiniMap == null) return;
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.hide(mMapFragment);
        ft.commit();
    }

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG, "onMapReady: " + (googleMap == null));
            if (mThingMiniMap == null) {
                mThingMiniMap = googleMap;
            }
            displayThingMarker();
        }
    };

    private void displayThingMarker() {
        mThingMiniMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        Location location = mThing.getLocation();
        LatLng latLng = new LatLng(location.getLat(),
                location.getLng());
        markerOptions.position(latLng);
        mThingMiniMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        mThingMiniMap.moveCamera(cameraUpdate);
    }

    private void setAuthorPhoto() {
        String authorPhotoPath = mThing.getUserAvatar();
        Glide.with(getActivity())
                .load(authorPhotoPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivAuthorAvatar);
    }

    private void setThingPhoto() {
        Glide.with(getActivity())
                .load(mThing.getPhoto())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivThingPhoto);
    }

    private void setTitle() {
        tvTitle.setText(mThing.getTitle());
    }

    private void setDescription() {
        tvDescription.setText(mThing.getDescription());
    }

    private void setTypeAndDate() {
        String type = convertType();
        String time = DateUtils.formatDateTime(getActivity(),
                mThing.getTimestamp(),
                DateUtils.FORMAT_SHOW_DATE);
        StringBuilder sb = new StringBuilder();
        sb.append("Posted in ");
        sb.append(type);
        sb.append(" ");
        sb.append(time);
        tvType.setText(sb.toString());
    }

    private String convertType() {
        String type = (mThing.getType() == Thing.TYPE_LOST) ?
                getString(R.string.type_lost)
                : getString(R.string.type_found);
        return type;
    }

    private void setComments() {
       /* if (mCommentsAdapter != null) return;
        Query commentQuery = FirebaseUtils.getDatabase()
                .getReference()
                .child(FirebaseConstants.THINGS_COMMENTS)
                .child(mThing.getKey());
        mCommentsAdapter = new CommentsAdapter(Comment.class,
                R.layout.item_comment,
                CommentViewHolder.class,
                commentQuery);
        rvComments.setLayoutManager(new LinearLayoutManager(ThingDetailsActivity.this));
        rvComments.setAdapter(mCommentsAdapter);*/
    }

    private void setDescriptionPhotos() {
        if (mThing.getDescriptionPhotos() != null) {
            List<String> descriptionPhotos = mThing.getDescriptionPhotos();
            mDescriptionPhotosAdapter = new DescriptionPhotosAdapter(descriptionPhotos);
            mDescriptionPhotosAdapter.setOnClickListener(new DescriptionPhotosAdapter.OnClickListener() {
                @Override
                public void onClickPhoto(View view, int position) {
                    launchDescriptionPhotosActivity(view, position);
                }
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false);
            rvDescriptionPhotos.setLayoutManager(layoutManager);
            rvDescriptionPhotos.setAdapter(mDescriptionPhotosAdapter);
        } else {
            hideDescriptionPhotos();
        }
    }

    private void hideDescriptionPhotos() {
        rvDescriptionPhotos.setVisibility(View.GONE);
        mDescriptionPhotosAdapter = null;
        rvDescriptionPhotos.setAdapter(null);
    }

    private void launchDescriptionPhotosActivity(View view, int position) {
        Intent intent = new Intent(getActivity(), DescriptionPhotosActivity.class);
        intent.putExtra(EXTRA_START_POSITION, position);
        ArrayList<String> descriptionPhotos = new ArrayList<>();
        descriptionPhotos.addAll(mThing.getDescriptionPhotos());
        intent.putExtra(DescriptionPhotosActivity.EXTRA_DESCRIPTION_PHOTOS, descriptionPhotos);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(),
                        view,
                        view.getTransitionName());
        startActivity(intent);
    }
}
