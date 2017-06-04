package com.sanislo.lostandfound.view.things.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.Thing;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 25.12.16.
 */

public class ThingAdapter extends RecyclerView.Adapter<ThingAdapter.ViewHolder> {
    private String TAG = ThingAdapter.class.getSimpleName();
    private Context mContext;
    private OnClickListener mOnClickListener;
    private List<Thing> mThingList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public ThingAdapter(Context context, OnClickListener onClickListener, List<Thing> thingList) {
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

    public void clear() {
        if (mThingList != null) {
            mThingList.clear();
        }
    }

    public void removeItem(int position) {
        mThingList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mThingList.size());
    }

    public Thing getItem(int position) {
        return mThingList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_thing_swipeable, null);
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

        @BindView(R.id.tv_thing_author)
        TextView tvAuthor;

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
            tvAuthor.setText(thing.getUserName());
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
            Log.d(TAG, "setAuthorPhoto: " + mThing.getUserAvatar());
            Glide.with(itemView.getContext())
                    .load(mThing.getUserAvatar())
                    //.placeholder(R.drawable.avatar_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if (e != null) e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivAuthorAvatar);
        }

        private void setThingPhoto() {
            Glide.with(itemView.getContext())
                    .load(mThing.getPhoto())
                    //.placeholder(R.drawable.thing_cover_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if (e != null) e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivThingPhoto);
        }

        @OnClick(R.id.rl_thing_root_view)
        public void onClickRootView() {
            if (mOnClickListener == null) return;
            mOnClickListener.onClickRootView(mRootView, mThingList.get(getAdapterPosition()));
        }
    }
}
