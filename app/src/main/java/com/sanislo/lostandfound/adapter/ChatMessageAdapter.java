package com.sanislo.lostandfound.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.ChatMessage;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 30.04.17.
 */

public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatMessageAdapter.ViewHolder> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public ChatMessageAdapter(Class<ChatMessage> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, ChatMessage model, int position) {
        viewHolder.bind(model);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private String TAG = ViewHolder.class.getSimpleName();

        @BindView(R.id.tv_chat_message)
        TextView tvChatMessage;

        private ChatMessage mChatMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ChatMessage chatMessage) {
            mChatMessage = chatMessage;
            tvChatMessage.setText(chatMessage.getMessage());
        }
    }
}
