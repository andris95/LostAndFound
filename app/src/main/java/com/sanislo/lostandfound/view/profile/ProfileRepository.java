package com.sanislo.lostandfound.view.profile;

import android.support.annotation.NonNull;

import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 06.06.17.
 */

public class ProfileRepository implements ProfileDataSource {
    @Override
    public void loadProfile(@NonNull String userUID, @NonNull final LoadProfileCallback loadProfileCallback) {
        ApiModel apiModel = new ApiModelImpl();
        Call<List<User>> call = apiModel.getUserListByUID(userUID);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    loadProfileCallback.onProfileLoaded(response.body().get(0));
                } else {
                    loadProfileCallback.onError();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
                loadProfileCallback.onError();
            }
        });
    }
}
