package com.sanislo.lostandfound.view.map;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 02.04.17.
 */

public class MapPresenterImpl implements MapPresenter {
    private String TAG = MapPresenter.class.getSimpleName();
    private ApiModel mApiModel = new ApiModelImpl();
    private MapView mView;

    public MapPresenterImpl(MapView view) {
        mView = view;
    }

    @Override
    public void getThings() {
        Call<List<Thing>> call = mApiModel.getThings();
        call.enqueue(new Callback<List<Thing>>() {
            @Override
            public void onResponse(Call<List<Thing>> call, Response<List<Thing>> response) {
                if (response.isSuccessful()) {
                    mView.onThingsLoaded(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Thing>> call, Throwable t) {

            }
        });
    }
}
