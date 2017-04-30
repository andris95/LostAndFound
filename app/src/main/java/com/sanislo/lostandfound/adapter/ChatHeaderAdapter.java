package com.sanislo.lostandfound.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.ChatHeader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 30.04.17.
 */

public class ChatHeaderAdapter extends FirebaseRecyclerAdapter<ChatHeader, ChatHeaderAdapter.ViewHolder> {
    private OnClickListener mOnClickListener;

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public ChatHeaderAdapter(Class<ChatHeader> modelClass, int modelLayout, Class<ChatHeaderAdapter.ViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(ChatHeaderAdapter.ViewHolder viewHolder, ChatHeader model, int position) {
        viewHolder.bind(model, mOnClickListener);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(ChatHeader chatHeader);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_chat_partner_avatar)
        ImageView ivChatPartnerAvatar;

        @BindView(R.id.tv_chat_partner_name)
        TextView tvChatPartnerName;

        @BindView(R.id.iv_me_avatar)
        ImageView ivMe;

        @BindView(R.id.tv_last_message)
        TextView tvLastMessage;

        @BindView(R.id.tv_message_date)
        TextView tvMessageDate;

        private ChatHeader mChatHeader;
        private ChatHeaderAdapter.OnClickListener mOnClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ChatHeader chatHeader, OnClickListener onClickListener) {
            mChatHeader = chatHeader;
            mOnClickListener = onClickListener;

            tvChatPartnerName.setText(chatHeader.getRecipientName());
            tvLastMessage.setText(chatHeader.getMessage());
            setMessageDate();
            Glide.with(itemView.getContext())
                    .load(mChatHeader.getRecipientAvatarUrl())
                    .placeholder(R.drawable.avatar_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivChatPartnerAvatar);
            checkLastMessageAuthor();
        }

        private void checkLastMessageAuthor() {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (mChatHeader.isLastMessageMine(uid)) {
                ivMe.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(mChatHeader.getLastMessageAuthorAvatarUrl())
                        .placeholder(R.drawable.avatar_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivMe);
            } else {
                ivMe.setVisibility(View.GONE);
            }
        }

        private void setMessageDate() {
            CharSequence date = DateUtils.getRelativeTimeSpanString(mChatHeader.getTimestamp(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS).toString();
            tvMessageDate.setText(date);
        }

        @OnClick(R.id.rl_chat_Header_root)
        public void onClick() {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(mChatHeader);
            }
        }
    }
}
