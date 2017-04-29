package com.sanislo.lostandfound.view.map;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.DescriptionPhotosAdapter;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.thingDetails.DescriptionPhotosActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 02.04.17.
 */

public class MapActivity extends BaseActivity implements ThingsMapFragment.MarkerClickListener {
    private String TAG = MapActivity.class.getSimpleName();
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 999;
    private ThingsMapFragment mThingsMapFragment;
    private BottomSheetBehavior mBottomSheetBehavior;
    private View bottomSheetView;

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

    public static final String EXTRA_START_POSITION = "EXTRA_START_POSITION";
    public static final String EXTRA_UPDATED_POSITION = "EXTRA_UPDATED_POSITION";
    public static final String EXTRA_THING = "EXTRA_THING";

    private Thing mThing;
    private DescriptionPhotosAdapter mDescriptionPhotosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        if (checkPlayServices()) {
            initThingsMapFragment();
        }
        bottomSheetView = findViewById(R.id.bottom_sheet_thing);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                googleAPI.makeGooglePlayServicesAvailable(MapActivity.this);
            }
            return false;
        }
        return true;
    }

    private void initThingsMapFragment() {
        mThingsMapFragment = new ThingsMapFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_map_container, mThingsMapFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClusterItemClick(AbstractMarker abstractMarker) {
        mThing = abstractMarker.getThing();
        displayThing();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void displayThing() {
        setTitle();
        setDescription();
        setTypeAndDate();
        setAuthorPhoto();
        setThingPhoto();
        setDescriptionPhotos();
    }

    private void setAuthorPhoto() {
        String authorPhotoPath = mThing.getUserAvatar();
        Glide.with(MapActivity.this)
                .load(authorPhotoPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivAuthorAvatar);
    }

    private void setThingPhoto() {
        Glide.with(MapActivity.this)
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
        StringBuilder sb = new StringBuilder();
        CharSequence date = DateUtils.getRelativeTimeSpanString(mThing.getTimestamp(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS).toString();
        String type = convertType();
        sb.append(type);
        sb.append(" ");
        sb.append(date);
        tvType.setText(sb.toString());
    }

    private String convertType() {
        String type = (mThing.getType() == Thing.TYPE_LOST) ?
                getString(R.string.type_lost)
                : getString(R.string.type_found);
        return type;
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
            LinearLayoutManager layoutManager = new LinearLayoutManager(MapActivity.this,
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
        Intent intent = new Intent(MapActivity.this, DescriptionPhotosActivity.class);
        intent.putExtra(EXTRA_START_POSITION, position);
        ArrayList<String> descriptionPhotos = new ArrayList<>();
        descriptionPhotos.addAll(mThing.getDescriptionPhotos());
        intent.putExtra(DescriptionPhotosActivity.EXTRA_DESCRIPTION_PHOTOS, descriptionPhotos);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(MapActivity.this,
                        view,
                        view.getTransitionName());
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
