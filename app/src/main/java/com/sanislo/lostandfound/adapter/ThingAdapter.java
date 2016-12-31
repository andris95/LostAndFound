package com.sanislo.lostandfound.adapter;

import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.ThingViewHolder;

/**
 * Created by root on 25.12.16.
 */

public class ThingAdapter extends FirebaseRecyclerAdapter<Thing, ThingViewHolder> {
    private int mExpandedPosition = RecyclerView.NO_POSITION;
    private OnClickListener mOnClickListener;

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

    @Override
    protected void populateViewHolder(ThingViewHolder viewHolder, Thing model, int position) {
        viewHolder.setIsExpanded(position == mExpandedPosition);
        viewHolder.setOnClickListener(mOnClickListener);
        viewHolder.populate(model, position);
    }

    public interface OnClickListener {
        void onClickAddComment(Thing thing, String text);
        void onClickDescription(int position);
    }
}
