package com.sanislo.lostandfound.adapter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.model.firebaseModel.Comment;
import com.sanislo.lostandfound.view.CommentViewHolder;

/**
 * Created by root on 28.12.16.
 */

public class CommentsAdapter extends FirebaseRecyclerAdapter<Comment, CommentViewHolder> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public CommentsAdapter(Class<Comment> modelClass, int modelLayout, Class<CommentViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {
        viewHolder.bind(model);
    }
}
