package com.sanislo.lostandfound.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.firebaseModel.Comment;
import com.sanislo.lostandfound.model.firebaseModel.FirebaseUser;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 28.12.16.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = CommentViewHolder.class.getSimpleName();
    private Comment mComment;
    private Context mContext;

    @BindView(R.id.iv_comment_author_avatar)
    ImageView ivAuthorAvatar;

    @BindView(R.id.tv_comment_text)
    TextView tvCommentText;

    @BindView(R.id.tv_comment_author)
    TextView tvAuthorName;

    public CommentViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void bind(Comment comment) {
        Log.d(TAG, "bind: " + comment);
        mComment = comment;
        setCommentText();
        getUserData(comment);
    }

    private void getUserData(Comment comment) {
        FirebaseUtils.getDatabase().getReference().child(FirebaseConstants.USERS)
                .child(comment.getAuthorUID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseUser author = dataSnapshot.getValue(FirebaseUser.class);
                        setAuthorAvatar(author);
                        setAuthorName(author);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setAuthorName(FirebaseUser author) {
        tvAuthorName.setText(author.getFullName());
    }

    private void setCommentText() {
        tvCommentText.setText(mComment.getText());
    }

    private void setAuthorAvatar(FirebaseUser user) {
        StorageReference photoRef = FirebaseUtils
                .getStorageRef()
                .child(user.getAvatarURL());
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(photoRef)
                .into(ivAuthorAvatar);
    }
}
