package com.sanislo.lostandfound.view.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;

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
    private User mUser;
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
                    mUser = response.body().get(0);
                    Log.d(TAG, "onResponse: " + mUser);
                    mView.onProfileLoaded(mUser);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ProfileActivity.PICK_PROFILE_IMAGE) {
            if (data != null) {
                mProfileImageUri = data.getData();
            }
        }
    }

    @Override
    public void updateUserAvatar(Context context, final int userId) {
        StorageReference storageReference = FirebaseUtils.getStorageRef();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UploadTask uploadTask = storageReference
                .child(FirebaseConstants.USERS)
                .child(uid)
                .child(FirebaseConstants.AVATAR)
                //.child(FileUtils.getFileName(context, mProfileImageUri))
                .child("avatar")
                .putFile(mProfileImageUri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isComplete() && task.isSuccessful()) {
                    String avatarURL = task.getResult().getDownloadUrl().toString();
                    mUser.setAvatarURL(avatarURL);
                    updateUserData(userId);
                }
            }
        });
    }

    private void updateUserData(int userId) {
        Call<Void> call = mApiModel.updateUser(userId, mUser);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success");
                    mView.onAvatarUpdated(mUser.getAvatarURL());
                } else {
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
