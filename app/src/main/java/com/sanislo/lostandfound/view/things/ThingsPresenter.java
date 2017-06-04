package com.sanislo.lostandfound.view.things;

import android.util.Log;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.things.data.source.ThingsDataSource;

import java.util.List;

/**
 * Created by root on 25.05.17.
 */

public class ThingsPresenter implements ThingsContract.Presenter {
    private static final String TAG = ThingsPresenter.class.getSimpleName();

    private ThingsRepository mThingsRepository;
    private ThingsContract.View mView;

    public ThingsPresenter(ThingsContract.View view) {
        mThingsRepository = new ThingsRepository();
        mView = view;
    }

    public ThingsPresenter(ThingsRepository thingsRepository, ThingsContract.View view) {
        mThingsRepository = thingsRepository;
        mView = view;
    }

    @Override
    public void loadThings(int page) {
        mThingsRepository.loadThings("timestamp",
                "DESC",
                page,
                new ThingsDataSource.LoadThingsCallback() {
            @Override
            public void onThingsLoaded(List<Thing> thingList) {
                Log.d(TAG, "onThingsLoaded: " + thingList);
                mView.showThings(thingList);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "onDataNotAvailable: ");
                mView.showError();
            }
        });
    }

    @Override
    public void removeThing(int id) {
        mThingsRepository.removeThing(id, new ThingsDataSource.RemoveThingCallback() {
            @Override
            public void onThingRemoved() {
                Log.d(TAG, "onThingRemoved: ");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: ");
            }
        });
    }

    @Override
    public void updateThing(int id, boolean returned) {
        mThingsRepository.updateThing(id, returned, new ThingsDataSource.UpdateThingCallback() {
            @Override
            public void onUpdated() {
                Log.d(TAG, "onUpdated: ");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: ");
            }
        });
    }
}
