package com.sanislo.lostandfound.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.CommentsAdapter;
import com.sanislo.lostandfound.adapter.DescriptionPhotosAdapter;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.model.Comment;
import com.sanislo.lostandfound.model.Thing;
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

public class ThingViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = ThingViewHolder.class.getSimpleName();

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

    View mRootView;
    private ThingAdapter.OnClickListener mOnClickListener;
    private boolean mIsExpanded;
    private int mPosition;
    private Thing mThing;
    private User mUser;
    private StorageReference mStorageReference;
    private DescriptionPhotosAdapter mDescriptionPhotosAdapter;
    private CommentsAdapter mCommentsAdapter;

    public ThingViewHolder(View itemView) {
        super(itemView);
        mRootView = itemView;
        ButterKnife.bind(this, mRootView);
        edtComment.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Log.d(TAG, "ThingViewHolder: " + edtComment.getImeActionId());
        Log.d(TAG, "ThingViewHolder: " + edtComment.getImeOptions());
        initFirebaseStorage();
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
        String bucket = "gs://lostandfound-326c3.appspot.com";
        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bucket);
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
            LinearLayoutManager layoutManager = new LinearLayoutManager(mRootView.getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false);
            rvDescriptionPhotos.setLayoutManager(layoutManager);
            rvDescriptionPhotos.setAdapter(mDescriptionPhotosAdapter);
        } else {
            rvDescriptionPhotos.setVisibility(View.GONE);
        }
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
        }
    }

    @OnClick(R.id.btn_send_comment)
    public void sendComment() {
        if (mOnClickListener != null) {
            mOnClickListener.onClickAddComment(mThing, edtComment.getText().toString());
        }
    }
}
