package com.sanislo.lostandfound.view.thingDetails;

import android.app.FragmentTransaction;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.CommentsAdapter;
import com.sanislo.lostandfound.adapter.DescriptionPhotosAdapter;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.presenter.ThingDetailsPresenter;
import com.sanislo.lostandfound.presenter.ThingDetailsPresenterImpl;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.ThingDetailsView;

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

    public static final String EXTRA_THING_PATH = "EXTRA_THING_PATH";
    public static final String EXTRA_START_POSITION = "EXTRA_START_POSITION";
    public static final String EXTRA_UPDATED_POSITION = "EXTRA_UPDATED_POSITION";
    public static final String EXTRA_THING = "EXTRA_THING";

    private String mThingPath;
    private Thing mThing;
    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;

    private DescriptionPhotosAdapter mDescriptionPhotosAdapter;
    private CommentsAdapter mCommentsAdapter;

    private StorageReference mStorageReference;

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
        setEnterTransition();
        setContentView(R.layout.activity_thing_details);
        postponeEnterTransition();
        ButterKnife.bind(this);
        fetchIntentExtras();
        initFirebase();
        Thing thing = getIntent().getParcelableExtra(EXTRA_THING);
        Log.d(TAG, "onCreate: thing: " + thing);
        ivThingPhoto.setTransitionName(getString(R.string.transition_description_photo));
        mThingDetailsPresenter = new ThingDetailsPresenterImpl(this, mThingPath);
    }

    private void setEnterTransition() {
        Transition transition = new Slide();
        transition.excludeTarget(android.R.id.statusBarBackground, true);
        transition.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(transition);
    }

    private void fetchIntentExtras() {
        Bundle extras = getIntent().getExtras();
        mThingPath = extras.getString(EXTRA_THING_PATH);
    }

    private void initFirebase() {
        mStorageReference = FirebaseUtils.getStorageRef();
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

    @Override
    public void onThingLoaded(Thing thing) {
        mThing = thing;
        setTitle();
        setDescription();
        setTypeAndDate();
        setAuthorPhoto();
        setThingPhoto();
        setDescriptionPhotos();
        setComments();
        setMap();
        Log.d(TAG, "onThingLoaded: " + thing);
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
        //Snackbar.make(null, errorMessage, Snackbar.LENGTH_SHORT);
    }

    private void initMapView() {
        //mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_thing_location);
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
        /*mGoogleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        Map<String, Double> latLngMap = mThing.getLocation();
        LatLng latLng = new LatLng(latLngMap.get("lat"),
                latLngMap.get("lng"));
        markerOptions.position(latLng);
        mGoogleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        mGoogleMap.moveCamera(cameraUpdate);*/
    }

    private void setAuthorPhoto() {
        String authorPhotoPath = mThing.getUserAvatar();
        if (TextUtils.isEmpty(authorPhotoPath)) return;
        StorageReference authorPhotoRef = mStorageReference
                .child(authorPhotoPath);
        Glide.with(ThingDetailsActivity.this)
                .using(new FirebaseImageLoader())
                .load(authorPhotoRef)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.error_placeholder)
                .into(ivAuthorAvatar);
        Log.d(TAG, "setAuthorPhoto: authorPhotoRef: " + authorPhotoRef);
    }

    private void setThingPhoto() {
        String thingPhotoPath = mThing.getPhoto();
        if (TextUtils.isEmpty(thingPhotoPath)) return;
        StorageReference thingPhotoRef = mStorageReference.child(thingPhotoPath);
        Glide.with(ThingDetailsActivity.this)
                .using(new FirebaseImageLoader())
                .load(thingPhotoRef)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        scheduleStartPostponedTransition(ivThingPhoto);
                        return false;
                    }
                })
                .into(ivThingPhoto);
    }

    private void setTitle() {
        tvTitle.setText(mThing.getTitle());
    }

    private void setDescription() {
        tvDescription.setText(mThing.getDescription());
    }

    private void setTypeAndDate() {
        String type = mThing.getType();
        String time = DateUtils.formatDateTime(ThingDetailsActivity.this,
                mThing.getTimestamp(),
                DateUtils.FORMAT_SHOW_DATE);
        StringBuilder sb = new StringBuilder();
        sb.append("Posted in ");
        sb.append(type);
        sb.append(" ");
        sb.append(time);
        tvType.setText(sb.toString());
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
            if (mDescriptionPhotosAdapter != null) {
                updateDescriptionPhotosAdapter(mThing.getDescriptionPhotos());
            } else {
                createNewDescriptionPhotosAdapter();
            }
        } else {
            hideDescriptionPhotos();
        }
    }

    private void updateDescriptionPhotosAdapter(List<String> descriptionPhotos) {
        mDescriptionPhotosAdapter.setDescriptionPhotos(descriptionPhotos);
    }

    private void createNewDescriptionPhotosAdapter() {
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
    }

    private void hideDescriptionPhotos() {
        rvDescriptionPhotos.setVisibility(View.GONE);
        mDescriptionPhotosAdapter = null;
        rvDescriptionPhotos.setAdapter(null);
    }

    private void launchDescriptionPhotosActivity(View view, int position) {
        Intent intent = new Intent(ThingDetailsActivity.this, DescriptionPhotosActivity.class);
        intent.putExtra(DescriptionPhotosActivity.KEY_THING_KEY, mThingPath);
        intent.putExtra(EXTRA_START_POSITION, position);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(ThingDetailsActivity.this,
                        view,
                        view.getTransitionName());
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCommentsAdapter != null) {
            mCommentsAdapter.cleanup();
        }
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
}
