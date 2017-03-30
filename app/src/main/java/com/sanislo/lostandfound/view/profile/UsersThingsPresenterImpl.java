package com.sanislo.lostandfound.view.profile;

import android.util.Log;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 30.03.17.
 */

public class UsersThingsPresenterImpl implements UsersThingsPresenter {
    private String TAG = UsersThingsPresenter.class.getSimpleName();
    private ApiModel mApiModel = new ApiModelImpl();
    private UsersThingsView mView;

    public UsersThingsPresenterImpl(UsersThingsView view) {
        mView = view;
    }

    @Override
    public void getUsersThings(String uid, String type) {
        Call<List<Thing>> call = mApiModel.getUsersThingsByType(uid, type);
        call.enqueue(new Callback<List<Thing>>() {
            @Override
            public void onResponse(Call<List<Thing>> call, Response<List<Thing>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success");
                    mView.onThingsLoaded(response.body());
                } else {
                    Log.d(TAG, "onResponse: error: " + response.message());
                    Log.d(TAG, "onResponse: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Thing>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
