package com.sanislo.lostandfound.view.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

public class MapPresenterImpl implements MapPresenter,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private String TAG = MapPresenter.class.getSimpleName();
    private ApiModel mApiModel = new ApiModelImpl();
    private MapView mView;
    private GoogleApiClient mGoogleApiClient;

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

    private void initGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
    }

    @Override
    public void getCurrentLocation(Context context) {
        initGoogleApiClient(context);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            mView.onLastKnownLocationFound(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
    }
}
