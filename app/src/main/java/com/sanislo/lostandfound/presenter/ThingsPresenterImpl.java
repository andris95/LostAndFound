package com.sanislo.lostandfound.presenter;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.sanislo.lostandfound.interfaces.ThingsView;
import com.sanislo.lostandfound.model.Comment;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.model.api.Thing;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 28.12.16.
 */

public class ThingsPresenterImpl implements ThingsPresenter {
    public final String TAG = ThingsPresenterImpl.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private String mUID;
    private DatabaseReference mDatabaseReference;
    private ApiModel mApiModel = new ApiModelImpl();
    private ThingsView mView;

    public ThingsPresenterImpl(ThingsView view) {
        super();
        initFirebase();
        mView = view;
    }

    private void initFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUID = mFirebaseAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseUtils.getDatabase().getReference();
    }

    @Override
    public void getThings() {
        Call<List<Thing>> call = mApiModel.getThings();
        call.enqueue(new Callback<List<Thing>>() {
            @Override
            public void onResponse(Call<List<Thing>> call, Response<List<Thing>> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    mView.onThingsLoaded(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Thing>> call, Throwable t) {

            }
        });
    }

    /*@Override
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
    }*/

    private OnSuccessListener<Void> mOnCommentAddedListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            Log.d(TAG, "onSuccess:");
        }
    };
}
