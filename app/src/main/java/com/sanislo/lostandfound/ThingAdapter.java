package com.sanislo.lostandfound;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.ThingViewHolder;

/**
 * Created by root on 25.12.16.
 */

public class ThingAdapter extends FirebaseRecyclerAdapter<Thing, ThingViewHolder> {
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

    @Override
    protected void populateViewHolder(ThingViewHolder viewHolder, Thing model, int position) {
        viewHolder.populate(model);
    }
}
