package com.sanislo.lostandfound.presenter;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.sanislo.lostandfound.interfaces.ThingsView;
import com.sanislo.lostandfound.model.Comment;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;

import java.util.Date;

/**
 * Created by root on 28.12.16.
 */

public class ThingsPresenterImpl implements ThingsPresenter {
    public final String TAG = ThingsPresenterImpl.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private String mUID;
    private DatabaseReference mDatabaseReference;

    public ThingsPresenterImpl() {
        super();
        initFirebase();
    }

    private void initFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUID = mFirebaseAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseUtils.getDatabase().getReference();
    }

    @Override
    public void addComment(Thing thing, String text) {
        DatabaseReference commentRef = mDatabaseReference.child(FirebaseConstants.THINGS_COMMENTS)
                .child(thing.getKey())
                .push();
        Log.d(TAG, "addComment: commentRef: " + commentRef.toString());
        String commentKey = commentRef.getKey();
        long timestamp = new Date().getTime();
        Comment comment = new Comment(commentKey,
                thing.getKey(),
                mUID,
                text,
                timestamp);
        commentRef.setValue(comment, 0 - timestamp).addOnSuccessListener(mOnCommentAddedListener);
    }

    private OnSuccessListener<Void> mOnCommentAddedListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            Log.d(TAG, "onSuccess:");
        }
    };
}
