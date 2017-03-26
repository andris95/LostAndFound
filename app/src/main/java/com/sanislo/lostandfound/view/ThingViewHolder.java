package com.sanislo.lostandfound.view;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.firebaseModel.User;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 25.12.16.
 */

public class ThingViewHolder extends RecyclerView.ViewHolder {
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

    View mRootView;
    private ThingAdapter.OnClickListener mOnClickListener;
    private Thing mThing;
    private User mUser;
    private StorageReference mStorageReference;

    public ThingViewHolder(View itemView) {
        super(itemView);
        mRootView = itemView;
        ButterKnife.bind(this, mRootView);
        ivThingPhoto.setTransitionName(mRootView.getContext().getString(R.string.transition_description_photo));
        initFirebaseStorage();
    }

    public void setOnClickListener(ThingAdapter.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void populate(Thing thing) {
        mThing = thing;
        getAuthorUser();
        setTitle();
        setTypeAndDate();
        setDescription();
        setThingPhoto();
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
        String type = mThing.getType();
        String time = DateUtils.formatDateTime(mRootView.getContext(),
                mThing.getTimestamp(),
                DateUtils.FORMAT_SHOW_DATE);
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

    private void displayErrorPhoto(int drawableID, ImageView targetView) {
        Glide.with(mRootView.getContext())
                .load(drawableID)
                .into(targetView);
    }

    @OnClick(R.id.rl_thing_root_view)
    public void onClickRootView() {
        //mOnClickListener.onClickRootView(mRootView, mThing.getKey());
    }
}
