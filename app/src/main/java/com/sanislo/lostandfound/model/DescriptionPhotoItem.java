package com.sanislo.lostandfound.model;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.sanislo.lostandfound.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 01.04.17.
 */

public class DescriptionPhotoItem extends AbstractItem<DescriptionPhotoItem, DescriptionPhotoItem.ViewHolder> {
    private Uri mUri;

    public DescriptionPhotoItem(Uri uri) {
        mUri = uri;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_description_photo_preview;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        Glide.with(holder.itemView.getContext())
                .load(getUri())
                .into(holder.getIvDescriptionPhoto());
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_description_photo)
        ImageView ivDescriptionPhoto;

        @BindView(R.id.iv_remove)
        ImageView ivRemove;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        protected ImageView getIvDescriptionPhoto() {
            return ivDescriptionPhoto;
        }

        public ImageView getIvRemove() {
            return ivRemove;
        }
    }
}
