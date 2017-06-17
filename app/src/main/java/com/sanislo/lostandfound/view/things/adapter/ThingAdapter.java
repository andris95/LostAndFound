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

public class ThingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = ThingAdapter.class.getSimpleName();
    private Context mContext;
    private OnClickListener mOnClickListener;
    private OnEditListener mOnEditListener;
    private List<Thing> mThingList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private boolean mIsEditableLayout;

    public ThingAdapter(Context context, OnClickListener onClickListener, List<Thing> thingList) {
        mContext = context;
        mOnClickListener = onClickListener;
        mThingList = thingList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setOnEditListener(OnEditListener onEditListener) {
        mOnEditListener = onEditListener;
    }

    public void setEditableLayout(boolean editableLayout) {
        mIsEditableLayout = editableLayout;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = mLayoutInflater.inflate(R.layout.item_thing_swipeable, null);
        View view;
        switch (viewType) {
            case R.layout.item_thing_simple:
                view = mLayoutInflater.inflate(R.layout.item_thing_simple, null);
                return new BaseThingViewHolder(view);
            case R.layout.item_thing_editable:
                view = mLayoutInflater.inflate(R.layout.item_thing_editable, null);
                return new ThingEditableThingViewHolder(view);
            default:
                view = mLayoutInflater.inflate(R.layout.item_thing_simple, null);
                return new BaseThingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Thing thing = mThingList.get(position);
        if (holder instanceof BaseThingViewHolder) {
            ((BaseThingViewHolder) holder).populate(thing);
        } else if (holder instanceof ThingEditableThingViewHolder) {
            ((ThingEditableThingViewHolder) holder).populate(thing);
        }
        /*int viewType = getItemViewType(position);
        switch (viewType) {
            case R.layout.item_thing_simple:
                ((BaseThingViewHolder) holder).populate(thing);
            case R.layout.item_thing_editable:
                ((ThingEditableThingViewHolder) holder).populate(thing);
        }*/
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsEditableLayout) {
            return R.layout.item_thing_editable;
        } else {
            return R.layout.item_thing_simple;
        }
    }

    @Override
    public int getItemCount() {
        return mThingList == null ? 0 : mThingList.size();
    }

    public interface OnClickListener {
        void onClickRootView(View view, Thing thing);
    }

    public interface OnEditListener {
        void onClickEdit(Thing thing, int position);
    }

    class BaseThingViewHolder extends RecyclerView.ViewHolder {
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

        public BaseThingViewHolder(View itemView) {
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

    class ThingEditableThingViewHolder extends BaseThingViewHolder {

        public ThingEditableThingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void populate(Thing thing) {
            super.populate(thing);
        }

        @OnClick(R.id.iv_edit)
        public void onClickEdit() {
            if (mOnEditListener != null) mOnEditListener.onClickEdit(
                    mThingList.get(getAdapterPosition()), getAdapterPosition());
        }
    }
}
