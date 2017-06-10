package com.sanislo.lostandfound.view.profile;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.view.profile.source.ProfileDataSource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 26.05.17.
 */

public class ProfileRepository implements ProfileDataSource {
    public static final String TAG = ProfileRepository.class.getSimpleName();
    private ApiModel mApiModel = new ApiModelImpl();

    @Override
    public void loadProfile(String userId, @NonNull final LoadProfileCallback loadProfileCallback) {
        Call<List<User>> userCall = mApiModel.getUserListByUID(userId);
        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> userList = response.body();
                    if (userList != null && userList.size() != 0) {
                        loadProfileCallback.onProfileLoaded(userList.get(0));
                    } else {
                        loadProfileCallback.onError();
                    }
                } else {
                    loadProfileCallback.onError();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                t.printStackTrace();
                loadProfileCallback.onError();
            }
        });
    }

    @Override
    public void updateProfile(@NonNull User user, @NonNull final UpdateProfileCallback updateProfileCallback) {
        Call<Void> call = mApiModel.updateUser(user.getId(), user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    updateProfileCallback.onProfileUpdated();
                } else {
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                updateProfileCallback.onError();
            }
        });
    }
}
