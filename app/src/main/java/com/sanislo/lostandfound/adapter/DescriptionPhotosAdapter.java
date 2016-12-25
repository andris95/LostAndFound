package com.sanislo.lostandfound.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 25.12.16.
 */

public class DescriptionPhotosAdapter extends RecyclerView.Adapter<DescriptionPhotosAdapter.DescriptionPhotoViewHolder> {
    public static final String TAG = DescriptionPhotoViewHolder.class.getSimpleName();
    private List<String> mDescriptionPhotos;
    private StorageReference mStorageReference;

    public DescriptionPhotosAdapter(List<String> descriptionPhotos) {
        mDescriptionPhotos = descriptionPhotos;
        initFirebaseStorage();
    }

    private void initFirebaseStorage() {
        String storageBucket = "gs://lostandfound-326c3.appspot.com";
        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storageBucket);
    }

    @Override
    public DescriptionPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_description_photo, parent, false);
        DescriptionPhotoViewHolder viewHolder = new DescriptionPhotoViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DescriptionPhotoViewHolder holder, int position) {
        String photoPath = mDescriptionPhotos.get(position);
        holder.bind(photoPath);
    }

    @Override
    public int getItemCount() {
        return mDescriptionPhotos.size();
    }

    class DescriptionPhotoViewHolder extends RecyclerView.ViewHolder {
        private View mRootView;

        @BindView(R.id.iv_description_photo)
        ImageView ivDescriptionPhoto;

        public DescriptionPhotoViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            ButterKnife.bind(this, mRootView);
        }

        public void bind(String photoPath) {
            StorageReference reference = mStorageReference.child(photoPath);
            Glide.with(mRootView.getContext())
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
}
