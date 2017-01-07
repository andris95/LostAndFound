package com.sanislo.lostandfound.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.ThingViewHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by root on 25.12.16.
 */

public class ThingAdapter extends FirebaseRecyclerAdapter<Thing, ThingViewHolder> {
    private int mExpandedPosition = RecyclerView.NO_POSITION;
    private ArrayList<ThingViewHolder> mThingViewHolders;
    private OnClickListener mOnClickListener;
    private ScrollListener mScrollListener;

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public ThingAdapter(Class<Thing> modelClass, int modelLayout, Class<ThingViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mThingViewHolders = new ArrayList<>();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public int getExpandedPosition() {
        return mExpandedPosition;
    }

    public void setExpandedPosition(int expandedPosition) {
        mExpandedPosition = expandedPosition;
    }

    public void scrollDescriptionPhotosList(int positionToScroll) {
        mThingViewHolders.get(getExpandedPosition()).scrollToPositionPublic(positionToScroll);
    }

    public View getSharedView(int position) {
        return mThingViewHolders.get(getExpandedPosition()).getSharedView(position);
    }

    @Override
    public ThingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        ThingViewHolder viewHolder = new ThingViewHolder(view);
        mThingViewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    protected void populateViewHolder(ThingViewHolder viewHolder, Thing model, int position) {
        viewHolder.setIsExpanded(position == mExpandedPosition);
        viewHolder.setOnClickListener(mOnClickListener);
        viewHolder.populate(model, position);
    }

    public interface ScrollListener {
        void scrollToPosition(int position);
    }

    public interface OnClickListener {
        void onClickAddComment(Thing thing, String text);
        void onClickDescription(int position);
        void onClickDescriptionPhoto(View view, int position, String thingKey);
        void onScrolledDescriptionList();
    }
}
