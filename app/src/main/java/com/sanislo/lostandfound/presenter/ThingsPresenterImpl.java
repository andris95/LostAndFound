package com.sanislo.lostandfound.presenter;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.sanislo.lostandfound.interfaces.ThingsView;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.utils.PreferencesManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 28.12.16.
 */

public class ThingsPresenterImpl implements ThingsPresenter {
    public final String TAG = ThingsPresenterImpl.class.getSimpleName();
    private ApiModel mApiModel = new ApiModelImpl();
    private ThingsView mView;

    public ThingsPresenterImpl(ThingsView view) {
        super();
        mView = view;
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

    @Override
    public void getProfile(Context context) {
        final String userUID = PreferencesManager.getUserUID(context);
        Log.d(TAG, "getUser: userUID: " + userUID);
        //Call<User> userCall = mApiModel.getUser(userUID);
        Call<List<User>> userCall = mApiModel.getUserListByUID(userUID);
        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    User user = response.body().get(0);
                    Log.d(TAG, "onResponse: " + user);
                    mView.onProfileLoaded(user);
                } else {
                    Log.d(TAG, "onResponse: error");
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

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
