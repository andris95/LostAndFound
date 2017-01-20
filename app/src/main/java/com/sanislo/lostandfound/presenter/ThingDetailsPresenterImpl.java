package com.sanislo.lostandfound.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sanislo.lostandfound.model.Comment;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.ThingLocation;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.view.ThingDetailsView;

import java.util.Date;

/**
 * Created by root on 15.01.17.
 */

public class ThingDetailsPresenterImpl implements ThingDetailsPresenter {
    public final String TAG = ThingDetailsPresenterImpl.class.getSimpleName();

    private ThingDetailsView mView;
    private FirebaseAuth mFirebaseAuth;
    private String mUID;

    private String mThingKey;
    private Thing mThing;
    private DatabaseReference mDatabaseReference;

    private ValueEventListener mThingListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mThing = dataSnapshot.getValue(Thing.class);
            Log.d(TAG, "onDataChange: " + mThing);
            mView.onThingLoaded(mThing);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public ThingDetailsPresenterImpl(ThingDetailsView view, String thingKey) {
        super();
        mView = view;
        mThingKey = thingKey;
        initFirebase();
    }

    private void initFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUID = mFirebaseAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseUtils.getDatabase().getReference();
    }

    @Override
    public void onResume() {
        getThing();
    }

    @Override
    public void onPause() {
        mDatabaseReference.removeEventListener(mThingListener);
    }

    private void getThing() {
        Log.d(TAG, "getThing: " + mThingKey);
        Log.d(TAG, "getThing: " + mDatabaseReference.child(FirebaseConstants.THINGS).child(mThingKey).toString());
        mDatabaseReference.child(FirebaseConstants.THINGS)
                .child(mThingKey)
                .addValueEventListener(mThingListener);
    }

    @Override
    public void addComment(Thing thing, String text) {
        if (TextUtils.isEmpty(text)) {
            text = "Keys found on Aigburth Road, Liverpool, " +
                    "between the Maccies and the garage. " +
                    "It’s got two keyrings on as well but I’ve covered them up for the owner to identify.";
            mView.onError("Text can't be blank!");
            //return;
        }
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
        commentRef.setValue(comment, 0 - timestamp);
    }
}
