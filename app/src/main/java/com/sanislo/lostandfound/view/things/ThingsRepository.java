package com.sanislo.lostandfound.view.things;

import android.support.annotation.NonNull;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.view.things.data.source.ThingsDataSource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 25.05.17.
 */

public class ThingsRepository implements ThingsDataSource {
    ApiModel mApiModel = new ApiModelImpl();

    @Override
    public void loadThings(@NonNull final LoadThingsCallback loadThingsCallback) {
        mApiModel.getThings().enqueue(new Callback<List<Thing>>() {
            @Override
            public void onResponse(Call<List<Thing>> call, Response<List<Thing>> response) {
                if (response.isSuccessful()) {
                    loadThingsCallback.onThingsLoaded(response.body());
                } else {
                    loadThingsCallback.onDataNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<List<Thing>> call, Throwable t) {
                loadThingsCallback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void loadThings(@NonNull String sort, @NonNull String order, @NonNull int page, @NonNull final LoadThingsCallback loadThingsCallback) {
        Call<List<Thing>> getThingsCall = mApiModel.getThings(sort, order, page);
        getThingsCall.enqueue(new Callback<List<Thing>>() {
            @Override
            public void onResponse(Call<List<Thing>> call, Response<List<Thing>> response) {
                if (response.isSuccessful()) {
                    loadThingsCallback.onThingsLoaded(response.body());
                } else {
                    loadThingsCallback.onDataNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<List<Thing>> call, Throwable t) {
                t.printStackTrace();
                loadThingsCallback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void removeThing(@NonNull int id, @NonNull final RemoveThingCallback removeThingCallback) {
        Call<Void> call = mApiModel.removeThing(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removeThingCallback.onThingRemoved();
                } else {
                    removeThingCallback.onError();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                removeThingCallback.onError();
            }
        });
    }

    @Override
    public void loadThing(@NonNull String thingId, @NonNull LoadThingsCallback loadThingsCallback) {

    }

    @Override
    public void saveThing(@NonNull Thing thing) {

    }
}
