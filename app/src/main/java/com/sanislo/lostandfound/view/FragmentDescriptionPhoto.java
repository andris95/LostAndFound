package com.sanislo.lostandfound.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.utils.FirebaseUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 02.01.17.
 */

public class FragmentDescriptionPhoto extends Fragment {
    private final String TAG = FragmentDescriptionPhoto.class.getSimpleName();
    public final String KEY_PHOTO_PATH = "KEY_DESCRIPTION_PHOTO_PATH";

    @BindView(R.id.iv_description_photo)
    ImageView ivDescriptionPhoto;

    private String mPhotoPath;
    private StorageReference mStorageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoPath = getArguments().getString(KEY_PHOTO_PATH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description_photo, container, false);
        ButterKnife.bind(this, view);
        displayDescriptionPhoto();
        return view;
    }

    public static FragmentDescriptionPhoto newInstance(String photoPath) {
        Bundle args = new Bundle();
        args.putString("KEY_DESCRIPTION_PHOTO_PATH", photoPath);
        FragmentDescriptionPhoto fragment = new FragmentDescriptionPhoto();
        fragment.setArguments(args);
        return fragment;
    }

    private void displayDescriptionPhoto() {
        Log.d(TAG, "displayDescriptionPhoto: " + mPhotoPath);
        mStorageReference = FirebaseUtils.getStorageRef();
        StorageReference reference = mStorageReference.child(mPhotoPath);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(reference)
                .error(R.drawable.error_placeholder)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onException: error displaying " + model.getPath());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(ivDescriptionPhoto);
    }
}
