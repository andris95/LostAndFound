package com.sanislo.lostandfound.view.thingDetails;

import android.app.FragmentTransaction;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.CommentsAdapter;
import com.sanislo.lostandfound.adapter.ContactsAdapter;
import com.sanislo.lostandfound.adapter.DescriptionPhotosAdapter;
import com.sanislo.lostandfound.model.Location;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.presenter.ThingDetailsPresenter;
import com.sanislo.lostandfound.presenter.ThingDetailsPresenterImpl;
import com.sanislo.lostandfound.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 15.01.17.
 */

public class ThingDetailsActivity extends BaseActivity implements ThingDetailsView {
    private String TAG = ThingDetailsActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

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

    @BindView(R.id.rv_contacts)
    RecyclerView rvContacts;

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
    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;

    private DescriptionPhotosAdapter mDescriptionPhotosAdapter;
    private CommentsAdapter mCommentsAdapter;
    private ThingDetailsPresenter mThingDetailsPresenter;
    private Bundle mTmpReenterState;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                int startingPosition = mTmpReenterState.getInt(EXTRA_START_POSITION);
                int currentPosition = mTmpReenterState.getInt(EXTRA_UPDATED_POSITION);
                if (startingPosition != currentPosition) {
                    // If startingPosition != currentPosition the user must have swiped to a
                    // different page in the DetailsActivity. We must update the shared element
                    // so that the correct one falls into place.
                    updateSharedView(names, sharedElements, currentPosition);
                }

                mTmpReenterState = null;
            } else {
                // If mTmpReenterState is null, then the activity is exiting.
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View statusBar = findViewById(android.R.id.statusBarBackground);
                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
            }
        }
    };

    private void updateSharedView(List<String> names, Map<String, View> sharedElements, int currentPosition) {
        String newTransitionName = getString(R.string.transition_description_photo) + "_" + currentPosition;
        DescriptionPhotosAdapter.DescriptionPhotoViewHolder viewHolder = (DescriptionPhotosAdapter.DescriptionPhotoViewHolder) rvDescriptionPhotos.findViewHolderForAdapterPosition(currentPosition);
        View newSharedElement = viewHolder.getSharedView();
        if (newSharedElement != null) {
            names.clear();
            names.add(newTransitionName);
            sharedElements.clear();
            sharedElements.put(newTransitionName, newSharedElement);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setExitSharedElementCallback(mCallback);
        //setEnterTransition();
        setContentView(R.layout.activity_thing_details);
        postponeEnterTransition();
        ButterKnife.bind(this);
        setupToolbar();
        ivThingPhoto.setTransitionName(getString(R.string.transition_description_photo));
        ivAuthorAvatar.setTransitionName(getString(R.string.transition_avatar));
        mThingDetailsPresenter = new ThingDetailsPresenterImpl(this);

        mThing = getIntent().getParcelableExtra(EXTRA_THING);
        displayThing();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setEnterTransition() {
        Transition transition = new Slide();
        transition.excludeTarget(android.R.id.statusBarBackground, true);
        transition.excludeTarget(android.R.id.navigationBarBackground, true);
        //transition.addTarget(R.id.rl_thing_description);
        getWindow().setEnterTransition(transition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThingDetailsPresenter.onResume();
        if (mMapFragment != null) {
            mMapFragment.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mThingDetailsPresenter.onPause();
        if (mMapFragment != null) mMapFragment.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        mTmpReenterState = new Bundle(data.getExtras());
        int startingPosition = mTmpReenterState.getInt(EXTRA_START_POSITION);
        int currentPosition = mTmpReenterState.getInt(EXTRA_UPDATED_POSITION);
        Log.d(TAG, "onActivityReenter: " + currentPosition);
        if (startingPosition != currentPosition) {
            rvDescriptionPhotos.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        rvDescriptionPhotos.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rvDescriptionPhotos.getViewTreeObserver().removeOnPreDrawListener(this);
                // TODO: figure out why it is necessary to request layout here in order to get a smooth transition.
                rvDescriptionPhotos.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    private void displayThing() {
        setTitle();
        setDescription();
        setTypeAndDate();
        setAuthorPhoto();
        setThingPhoto();
        setDescriptionPhotos();
        setContacts();
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

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(ThingDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void initMapView() {
        if (mMapFragment == null) {
            GoogleMapOptions googleMapOptions = new GoogleMapOptions();
            googleMapOptions.liteMode(true);
            mMapFragment = MapFragment.newInstance(googleMapOptions);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fl_map_container, mMapFragment);
            ft.commit();
            mMapFragment.getMapAsync(mOnMapReadyCallback);
        } else {
            displayThingMarker();
        }
    }

    private void hideMapView() {
        flMapContainer.setVisibility(View.GONE);
        if (mGoogleMap == null) return;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(mMapFragment);
        ft.commit();
    }

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG, "onMapReady: ");
            if (mGoogleMap == null) {
                mGoogleMap = googleMap;
            }
            displayThingMarker();
        }
    };

    private void displayThingMarker() {
        mGoogleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        Location location = mThing.getLocation();
        LatLng latLng = new LatLng(location.getLat(),
                location.getLng());
        markerOptions.position(latLng);
        mGoogleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    private void setAuthorPhoto() {
        String authorPhotoPath = mThing.getUserAvatar();
        Glide.with(ThingDetailsActivity.this)
                .load(authorPhotoPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivAuthorAvatar);
    }

    private void setThingPhoto() {
        Glide.with(ThingDetailsActivity.this)
                .load(mThing.getPhoto())
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        scheduleStartPostponedTransition(ivThingPhoto);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        scheduleStartPostponedTransition(ivThingPhoto);
                        return false;
                    }
                })
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
            LinearLayoutManager layoutManager = new LinearLayoutManager(ThingDetailsActivity.this,
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
        Intent intent = new Intent(ThingDetailsActivity.this, DescriptionPhotosActivity.class);
        intent.putExtra(EXTRA_START_POSITION, position);
        ArrayList<String> descriptionPhotos = new ArrayList<>();
        descriptionPhotos.addAll(mThing.getDescriptionPhotos());
        intent.putExtra(DescriptionPhotosActivity.EXTRA_DESCRIPTION_PHOTOS, descriptionPhotos);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(ThingDetailsActivity.this,
                        view,
                        view.getTransitionName());
        startActivity(intent, options.toBundle());
    }

    @OnClick(R.id.btn_send_comment)
    public void onClickAddComment() {
        mThingDetailsPresenter.addComment(mThing, edtComment.getText().toString());
    }

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy. Some common places where it might make
     * sense to call this method are:
     *
     * (1) Inside a Fragment's onCreateView() method (if the shared element
     *     lives inside a Fragment hosted by the called Activity).
     *
     * (2) Inside a Picasso Callback object (if you need to wait for Picasso to
     *     asynchronously load/scale a bitmap before the transition can begin).
     *
     * (3) Inside a LoaderCallback's onLoadFinished() method (if the shared
     *     element depends on data queried by a Loader).
     */
    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    private void setContacts() {
        ContactsAdapter contactsAdapter = new ContactsAdapter(mThing.getUserContantcs());
        rvContacts.setLayoutManager(new LinearLayoutManager(ThingDetailsActivity.this));
        rvContacts.setAdapter(contactsAdapter);
    }
}
