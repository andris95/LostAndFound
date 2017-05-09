package com.sanislo.lostandfound.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.ChatMessage;
import com.sanislo.lostandfound.utils.FirebaseConstants;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 30.04.17.
 */

public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatMessageAdapter.ViewHolder> {
    public static final String TAG = ChatMessageAdapter.class.getSimpleName();
    private String mUid;

    //TODO check this
    private String mChatPartnerUid;

    public String getChatPartnerUid() {
        return mChatPartnerUid;
    }

    public void setChatPartnerUid(String chatPartnerUid) {
        mChatPartnerUid = chatPartnerUid;
    }

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
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view;
        switch (viewType) {
            case TYPE_LEFT:
                view = inflater.inflate(R.layout.item_chat_message_left, parent, false);
                break;
            case TYPE_RIGHT:
                view = inflater.inflate(R.layout.item_chat_message_right, parent, false);
                break;
            default:
                view = inflater.inflate(R.layout.item_chat_message_left, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = getItem(position);
        if (chatMessage.isMine(mUid)) {
            return TYPE_RIGHT;
        } else {
            return TYPE_LEFT;
        }
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, ChatMessage model, int position) {
        viewHolder.bind(model, mUid, mChatPartnerUid);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private String TAG = ViewHolder.class.getSimpleName();

        @BindView(R.id.tv_chat_message)
        TextView tvChatMessage;

        @BindView(R.id.tv_message_date)
        TextView tvMessageDate;

        @Nullable
        @BindView(R.id.iv_dot)
        ImageView ivDot;

        private ChatMessage mChatMessage;
        private String mChatPartnerUid;
        private String mAuthenticatedUid;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ChatMessage chatMessage, String authenticatedUid, String chatPartnerUid) {
            mChatMessage = chatMessage;
            //TODO check this
            mChatPartnerUid = chatPartnerUid;
            mAuthenticatedUid = authenticatedUid;

            tvChatMessage.setText(chatMessage.getMessage());
            String date = DateUtils.getRelativeTimeSpanString(mChatMessage.getTimestamp(),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();
            tvMessageDate.setText(date);
            if (ivDot != null) {
                checkIsRead();
            }
        }

        private void checkIsRead() {
            if (mChatMessage.isReadByRecipient()) {
                ivDot.setVisibility(View.GONE);
            } else {
                HashMap<String, Object> updateMap = new HashMap<>();
                updateMap.put(FirebaseConstants.CHAT_MESSAGES
                        + "/" + mAuthenticatedUid
                        + "/" + mChatPartnerUid
                        + "/" + mChatMessage.getTimestamp()
                        + "/isReadByRecipient", true);
                updateMap.put(FirebaseConstants.CHAT_MESSAGES
                        + "/" + mChatPartnerUid
                        + "/" + mAuthenticatedUid
                        + "/" + mChatMessage.getTimestamp()
                        + "/isReadByRecipient", true);
                FirebaseDatabase.getInstance().getReference()
                        .updateChildren(updateMap);
            }
        }
    }
}
