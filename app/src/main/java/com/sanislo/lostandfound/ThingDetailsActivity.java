package com.sanislo.lostandfound;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.adapter.CommentsAdapter;
import com.sanislo.lostandfound.adapter.DescriptionPhotosAdapter;
import com.sanislo.lostandfound.model.Comment;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.ThingLocation;
import com.sanislo.lostandfound.presenter.ThingDetailsPresenter;
import com.sanislo.lostandfound.presenter.ThingDetailsPresenterImpl;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.view.CommentViewHolder;
import com.sanislo.lostandfound.view.ThingDetailsView;

import java.util.List;

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

    public static final String EXTRA_THING_PATH = "EXTRA_THING_PATH";

    private String mUID;
    private String mThingPath;
    private Thing mThing;
    private ThingLocation mThingLocation;
    private GoogleMap mGoogleMap;

    private DescriptionPhotosAdapter mDescriptionPhotosAdapter;
    private CommentsAdapter mCommentsAdapter;

    private StorageReference mStorageReference;

    private ThingDetailsPresenter mThingDetailsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_thing_details);
        postponeEnterTransition();
        ButterKnife.bind(this);
        fetchIntentExtras();
        initFirebase();
        ivThingPhoto.setTransitionName(getString(R.string.transition_description_photo));
        mThingDetailsPresenter = new ThingDetailsPresenterImpl(this, mThingPath);
    }

    private void fetchIntentExtras() {
        Bundle extras = getIntent().getExtras();
        mThingPath = extras.getString(EXTRA_THING_PATH);
    }

    private void initFirebase() {
        mStorageReference = FirebaseUtils.getStorageRef();
        mUID = getAuthenticatedUserUID();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThingDetailsPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mThingDetailsPresenter.onPause();
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
        return !TextUtils.isEmpty(mThing.getThingLocationKey());
    }

    private void setMap() {
        Log.d(TAG, "onThingLoaded: " + hasLocation());
        if (hasLocation() && mGoogleMap == null) {
            initMapView();
        } else {
            hideMapView();
        }
    }

    @Override
    public void onThingLocationLoaded(ThingLocation thingLocation) {
        mThingLocation = thingLocation;
        Log.d(TAG, "onThingLocationLoaded: " + mThingLocation);
        if (mGoogleMap != null) {
            displayThingMarker();
        } else {
            initMapView();
        }
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(ThingDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        //Snackbar.make(null, errorMessage, Snackbar.LENGTH_SHORT);
    }

    private void initMapView() {
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map_thing_location);
        Log.d(TAG, "initMapView: " + (mapFragment == null));
        if (mapFragment != null) {
            mapFragment.getMapAsync(mOnMapReadyCallback);
        }
    }

    private void hideMapView() {
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map_thing_location);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(mapFragment);
        ft.commit();
    }

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG, "onMapReady: ");
            if (mGoogleMap == null) {
                mGoogleMap = googleMap;
            }
            if (hasLocation() && mThingLocation != null) {
                displayThingMarker();
            }
        }
    };


    private void displayThingMarker() {
        mGoogleMap.clear();
        LatLng latLng = new LatLng(mThingLocation.getCenterLat(), mThingLocation.getCenterLng());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mGoogleMap.addMarker(markerOptions);
        Log.d(TAG, "displayThingMarker: " + latLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    private void setAuthorPhoto() {
        String authorPhotoPath = mThing.getUserAvatar();
        if (TextUtils.isEmpty(authorPhotoPath)) return;
        StorageReference authorPhotoRef = mStorageReference
                .child(authorPhotoPath);
        Glide.with(ThingDetailsActivity.this)
                .using(new FirebaseImageLoader())
                .load(authorPhotoRef)
                .error(R.drawable.error_placeholder)
                .into(ivAuthorAvatar);
        Log.d(TAG, "setAuthorPhoto: authorPhotoRef: " + authorPhotoRef);
    }

    private void setThingPhoto() {
        String thingPhotoPath = mThing.getPhoto();
        StorageReference thingPhotoRef = mStorageReference.child(thingPhotoPath);
        Log.d(TAG, "setThingPhoto: " + thingPhotoRef.getPath());
        Glide.with(ThingDetailsActivity.this)
                .using(new FirebaseImageLoader())
                .load(thingPhotoRef)
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
        String type = (mThing.getType() == 0) ? "lost" : "found";
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
        if (mCommentsAdapter != null) return;
        Query commentQuery = FirebaseUtils.getDatabase()
                .getReference()
                .child(FirebaseConstants.THINGS_COMMENTS)
                .child(mThing.getKey());
        mCommentsAdapter = new CommentsAdapter(Comment.class,
                R.layout.item_comment,
                CommentViewHolder.class,
                commentQuery);
        rvComments.setLayoutManager(new LinearLayoutManager(ThingDetailsActivity.this));
        rvComments.setAdapter(mCommentsAdapter);
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
            rvDescriptionPhotos.setVisibility(View.GONE);
        }
    }

    private void launchDescriptionPhotosActivity(View view, int position) {
        Intent intent = new Intent(ThingDetailsActivity.this, DescriptionPhotosActivity.class);
        intent.putExtra(DescriptionPhotosActivity.KEY_THING_KEY, mThingPath);
        intent.putExtra(DescriptionPhotosActivity.KEY_POSITION, position);

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
