package com.sanislo.lostandfound.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanislo.lostandfound.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 25.12.16.
 */

public class DescriptionPhotosAdapter extends RecyclerView.Adapter<DescriptionPhotosAdapter.DescriptionPhotoViewHolder> {
    public final String TAG = DescriptionPhotoViewHolder.class.getSimpleName();
    private List<String> mDescriptionPhotos;
    private OnClickListener mOnClickListener;

    public DescriptionPhotosAdapter(List<String> descriptionPhotos) {
        mDescriptionPhotos = descriptionPhotos;
    }

    public void setDescriptionPhotos(List<String> descriptionPhotos) {
        mDescriptionPhotos = descriptionPhotos;
        notifyDataSetChanged();
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
        holder.setOnClickListener(mOnClickListener);
        holder.bind(photoPath, position);
    }

    @Override
    public int getItemCount() {
        return mDescriptionPhotos.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClickPhoto(View view, int position);
    }

    public class DescriptionPhotoViewHolder extends RecyclerView.ViewHolder {
        private View mRootView;
        private DescriptionPhotosAdapter.OnClickListener mOnClickListener;
        private int mPosition;

        @BindView(R.id.iv_description_photo)
        ImageView ivDescriptionPhoto;

        public DescriptionPhotoViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            ButterKnife.bind(this, mRootView);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }

        public void bind(String photoPath, int position) {
            mPosition = position;
            setTransitionName();
            setDescriptionPhoto(photoPath);
        }

        private void setTransitionName() {
            String transName = itemView.getContext().getString(R.string.transition_description_photo) + "_" + mPosition;
            ivDescriptionPhoto.setTransitionName(transName);
        }

        private void setDescriptionPhoto(String photoPath) {
            Glide.with(mRootView.getContext())
                    .load(photoPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivDescriptionPhoto);
        }

        @OnClick(R.id.iv_description_photo)
        public void onClickDescriptionPhoto() {
            if (mOnClickListener != null) {
                mOnClickListener.onClickPhoto(ivDescriptionPhoto, mPosition);
            }
        }

        public View getSharedView() {
            return ivDescriptionPhoto;
        }
    }
}
