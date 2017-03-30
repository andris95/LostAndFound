package com.sanislo.lostandfound.view.profile;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by root on 30.03.17.
 */

public class ProfilePresenterImpl implements ProfilePresenter {
    private String TAG = ProfilePresenter.class.getSimpleName();
    private ApiModel mApiModel = new ApiModelImpl();
    private ProfileView mView;
    private Uri mProfileImageUri;

    public ProfilePresenterImpl(ProfileView view) {
        mView = view;
    }

    @Override
    public void getProfile(String userUID) {
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
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }

    @Override
    public void updateProfile() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ProfileActivity.PICK_PROFILE_IMAGE) {
            if (data != null) {
                mProfileImageUri = data.getData();
            }
        }
    }
}
