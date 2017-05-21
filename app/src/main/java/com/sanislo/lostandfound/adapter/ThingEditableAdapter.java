package com.sanislo.lostandfound.adapter;

/**
 * Created by root on 17.04.17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.things.adapter.ThingAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 25.12.16.
 */

public class ThingEditableAdapter extends RecyclerView.Adapter<ThingEditableAdapter.ViewHolder> {
    private String TAG = ThingAdapter.class.getSimpleName();
    private Context mContext;
    private OnClickListener mOnClickListener;
    private List<Thing> mThingList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public ThingEditableAdapter(Context context, OnClickListener onClickListener, List<Thing> thingList) {
        mContext = context;
        mOnClickListener = onClickListener;
        mThingList = thingList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setThingList(List<Thing> thingList) {
        mThingList = thingList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_thing_simple, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Thing thing = mThingList.get(position);
        holder.populate(thing);
    }

    @Override
    public int getItemCount() {
        return mThingList == null ? 0 : mThingList.size();
    }

    public interface OnClickListener {
        void onClickRootView(View view, Thing thing);
        void onClickEdit(Thing thing);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final String TAG = "ThingViewHolder";

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
        private Thing mThing;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            ButterKnife.bind(this, mRootView);
            ivThingPhoto.setTransitionName(mRootView.getContext().getString(R.string.transition_description_photo));
        }

        public void populate(Thing thing) {
            mThing = thing;
            setTitle();
            setAuthorPhoto();
            setTypeAndDate();
            setDescription();
            setThingPhoto();
        }

        private void setTitle() {
            tvTitle.setText(mThing.getTitle());
        }

        private void setDescription() {
            tvDescription.setText(mThing.getDescription());
        }

        private void setTypeAndDate() {
            StringBuilder sb = new StringBuilder();
            CharSequence date = DateUtils.getRelativeTimeSpanString(mThing.getTimestamp(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS).toString();
            String type = convertType();
            sb.append(type);
            sb.append(" ");
            sb.append(date);
            tvType.setText(sb.toString());
        }

        private String convertType() {
            String type = (mThing.getType() == Thing.TYPE_LOST) ?
                    mContext.getString(R.string.type_lost)
                    : mContext.getString(R.string.type_found);
            return type;
        }
        private void setAuthorPhoto() {
            displayPhoto(mThing.getUserAvatar(), ivAuthorAvatar);
        }

        private void setThingPhoto() {
            if (!TextUtils.isEmpty(mThing.getPhoto())) {
                displayPhoto(mThing.getPhoto(), ivThingPhoto);
            } else {
                displayErrorPhoto(R.drawable.thing_cover_placeholder, ivThingPhoto);
            }
        }

        private void displayPhoto(String path, ImageView targetView) {
            Glide.with(mContext)
                    .load(path)
                    .error(R.drawable.thing_cover_placeholder)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(targetView);
        }

        private void displayErrorPhoto(int drawableID, ImageView targetView) {
            Glide.with(mContext)
                    .load(drawableID)
                    .into(targetView);
        }

        @OnClick(R.id.rl_thing_root_view)
        public void onClickRootView() {
            if (mOnClickListener == null) return;
            mOnClickListener.onClickRootView(mRootView, mThingList.get(getAdapterPosition()));
        }

        @OnClick(R.id.iv_edit)
        public void onClickEdit() {
            if (mOnClickListener != null) {
                mOnClickListener.onClickEdit(mThing);
            }
        }
    }
}

