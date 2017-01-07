package com.sanislo.lostandfound.view;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private int mPosition;
    private String mPhotoPath;
    private StorageReference mStorageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt("KEY_POSITION");
        mPhotoPath = getArguments().getString(KEY_PHOTO_PATH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description_photo, container, false);
        ButterKnife.bind(this, view);
        ivDescriptionPhoto.setTransitionName(getString(R.string.transition_description_photo) + "_" + mPosition);
        displayDescriptionPhoto();
        return view;
    }

    public static FragmentDescriptionPhoto newInstance(String photoPath, int position) {
        Bundle args = new Bundle();
        args.putString("KEY_DESCRIPTION_PHOTO_PATH", photoPath);
        args.putInt("KEY_POSITION", position);
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
                        getActivity().startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(ivDescriptionPhoto);
    }

    /**
     * Returns the shared element that should be transitioned back to the previous Activity,
     * or null if the view is not visible on the screen.
     */
    @Nullable
    public ImageView getDescriptionImageView() {
        if (isViewInBounds(getActivity().getWindow().getDecorView(), ivDescriptionPhoto)) {
            return ivDescriptionPhoto;
        }
        return null;
    }

    /**
     * Returns true if {@param view} is contained within {@param container}'s bounds.
     */
    private static boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
    }
}
