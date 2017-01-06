package com.sanislo.lostandfound.view;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.CommentsAdapter;
import com.sanislo.lostandfound.adapter.DescriptionPhotosAdapter;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.model.Comment;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.ThingLocation;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.utils.DateUtils;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 25.12.16.
 */

public class ThingViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    public final String TAG = ThingViewHolder.class.getSimpleName();

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

    @BindView(R.id.map_thing_location)
    MapView mMapView;

    @BindView(R.id.ib_description)
    ImageButton ibDescription;

    private AnimatedVectorDrawable mChevronVectorDrawable;
    View mRootView;
    private ThingAdapter.OnClickListener mOnClickListener;
    private boolean mIsExpanded;
    private int mPosition;
    private Thing mThing;
    private User mUser;
    private StorageReference mStorageReference;
    private DescriptionPhotosAdapter mDescriptionPhotosAdapter;
    private CommentsAdapter mCommentsAdapter;
    private GoogleMap mGoogleMap;

    public ThingViewHolder(View itemView) {
        super(itemView);
        mRootView = itemView;
        ButterKnife.bind(this, mRootView);
        edtComment.setImeOptions(EditorInfo.IME_ACTION_DONE);
        initFirebaseStorage();
        initMapView();
        setDescriptionArrow();
    }

    private void initMapView() {
        mMapView.onCreate(null);
        mMapView.getMapAsync(this);
    }

    public void setOnClickListener(ThingAdapter.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void populate(Thing thing, int position) {
        mThing = thing;
        mPosition = position;
        getAuthorUser();
        setTitle();
        setTypeAndDate();
        setDescription();
        setThingPhoto();
        setDescriptionVisibility();
        setDescriptionPhotos();
        setComments();
        setCommentEditorActionListener();
        displayMap();
    }

    private void displayMap() {
        if (!isPlaceKnown()) {
            mMapView.setVisibility(View.GONE);
        }
    }

    private void setCommentEditorActionListener() {
        edtComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && event.getAction() == KeyEvent.ACTION_DOWN) {
                    mOnClickListener.onClickAddComment(mThing, edtComment.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void getAuthorUser() {
        FirebaseUtils.getDatabase().getReference()
                .child(FirebaseConstants.USERS)
                .child(mThing.getUserUID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: " + mUser);
                        setAuthorPhoto();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setTitle() {
        tvTitle.setText(mThing.getTitle());
    }

    private void setDescription() {
        tvDescription.setText(mThing.getDescription());
    }

    private void setTypeAndDate() {
        String type = (mThing.getType() == 0) ? "lost" : "found";
        String time = DateUtils.getDateText(mThing.getTimestamp());
        StringBuilder sb = new StringBuilder();
        sb.append("Posted in ");
        sb.append(type);
        sb.append(" ");
        sb.append(time);
        tvType.setText(sb.toString());
    }

    private void initFirebaseStorage() {
        mStorageReference = FirebaseUtils.getStorageRef();
    }

    private void setAuthorPhoto() {
        if (!TextUtils.isEmpty(mUser.getAvatarURL())) {
            displayPhotoFromStorage(mUser.getAvatarURL(), ivAuthorAvatar);
        }
    }

    private void setThingPhoto() {
        if (!TextUtils.isEmpty(mThing.getPhoto())) {
            displayPhotoFromStorage(mThing.getPhoto(), ivThingPhoto);
        } else {
            displayErrorPhoto(R.drawable.placeholder, ivThingPhoto);
        }
    }

    private void displayPhotoFromStorage(String path, ImageView targetView) {
        StorageReference photoReference = mStorageReference.child(path);
        Glide.with(mRootView.getContext())
                .using(new FirebaseImageLoader())
                .load(photoReference)
                .error(R.drawable.placeholder)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onException: error displaying photo from reference: " + model.getPath());
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.d(TAG, "onResourceReady: " + model.getPath() + " displayed");
                        return false;
                    }
                })
                .into(targetView);
    }

    private void setDescriptionPhotos() {
        if (mThing.getDescriptionPhotos() != null) {
            List<String> descriptionPhotos = mThing.getDescriptionPhotos();
            mDescriptionPhotosAdapter = new DescriptionPhotosAdapter(descriptionPhotos);
            mDescriptionPhotosAdapter.setOnClickListener(new DescriptionPhotosAdapter.OnClickListener() {
                @Override
                public void onClickPhoto(int position) {
                    launchDescriptionPhotosActivity(position);
                }
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(mRootView.getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false);
            rvDescriptionPhotos.setLayoutManager(layoutManager);
            rvDescriptionPhotos.setAdapter(mDescriptionPhotosAdapter);
        } else {
            rvDescriptionPhotos.setVisibility(View.GONE);
        }
    }

    private void launchDescriptionPhotosActivity(int position) {
        Intent intent = new Intent(mRootView.getContext(), DescriptionPhotosActivity.class);
        intent.putExtra("THING_KEY", mThing.getKey());
        intent.putExtra("POSITION", position);
        mRootView.getContext().startActivity(intent);
    }

    private void setComments() {
        Query commentQuery = FirebaseUtils.getDatabase()
                .getReference()
                .child(FirebaseConstants.THINGS_COMMENTS)
                .child(mThing.getKey());
        mCommentsAdapter = new CommentsAdapter(Comment.class,
                R.layout.item_comment,
                CommentViewHolder.class,
                commentQuery);
        rvComments.setLayoutManager(new LinearLayoutManager(mRootView.getContext()));
        rvComments.setAdapter(mCommentsAdapter);
    }

    private void displayErrorPhoto(int drawableID, ImageView targetView) {
        Glide.with(mRootView.getContext())
                .load(drawableID)
                .into(targetView);
    }

    public void setIsExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    private void setDescriptionArrow() {
        mChevronVectorDrawable = mIsExpanded ?
                (AnimatedVectorDrawable) mRootView.getContext()
                .getDrawable(R.drawable.animated_vector_chevron_up)
                : (AnimatedVectorDrawable) mRootView.getContext()
                .getDrawable(R.drawable.animated_vector_chevron_down);
        ibDescription.setImageDrawable(mChevronVectorDrawable);
    }

    private void setDescriptionVisibility() {
        if (mIsExpanded) {
            rlDescription.setVisibility(View.VISIBLE);
        } else {
            rlDescription.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_click_description, R.id.ib_description})
    public void onClickDescription() {
        if (mOnClickListener != null) {
            mOnClickListener.onClickDescription(mPosition);
            mChevronVectorDrawable = mIsExpanded ?
                    (AnimatedVectorDrawable) mRootView.getContext()
                            .getDrawable(R.drawable.animated_vector_chevron_up)
                    : (AnimatedVectorDrawable) mRootView.getContext()
                    .getDrawable(R.drawable.animated_vector_chevron_down);
            ibDescription.setImageDrawable(mChevronVectorDrawable);
            mChevronVectorDrawable.start();
        }
    }

    @OnClick(R.id.btn_send_comment)
    public void sendComment() {
        if (mOnClickListener != null) {
            mOnClickListener.onClickAddComment(mThing, edtComment.getText().toString());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        if (!isPlaceKnown()) return;
        if (mGoogleMap == null) {
            mGoogleMap = googleMap;
            MapsInitializer.initialize(mRootView.getContext());
        }
        checkThingPlace();
    }

    private boolean isPlaceKnown() {
        return !TextUtils.isEmpty(mThing.getThingLocationKey());
    }

    private void checkThingPlace() {
        if (isPlaceKnown()) {
            getThingPlace();
        }
    }

    private void getThingPlace() {
        FirebaseUtils.getDatabase().getReference()
                .child(FirebaseConstants.THINGS_PLACE)
                .child(mThing.getKey())
                .child(mThing.getThingLocationKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ThingLocation thingLocation = dataSnapshot.getValue(ThingLocation.class);
                        Log.d(TAG, "onDataChange: " + thingLocation);
                        displayThingMarker(thingLocation);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void displayThingMarker(ThingLocation thingLocation) {
        mGoogleMap.clear();
        LatLng latLng = new LatLng(thingLocation.getCenterLat(), thingLocation.getCenterLng());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mGoogleMap.addMarker(markerOptions);
        Log.d(TAG, "displayThingMarker: " + latLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        mGoogleMap.moveCamera(cameraUpdate);
    }
}
