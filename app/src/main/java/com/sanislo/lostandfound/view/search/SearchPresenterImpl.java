package com.sanislo.lostandfound.view.search;

import android.text.TextUtils;
import android.util.Log;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 31.03.17.
 */

public class SearchPresenterImpl implements SearchPresenter {
    private String TAG = SearchPresenter.class.getSimpleName();
    private SearchThingView mView;
    private ApiModel mApiModel = new ApiModelImpl();
    private Map<String, List<String>> mQueryOptions;

    public SearchPresenterImpl(SearchThingView view) {
        mView = view;
        mQueryOptions = new HashMap<>();
        List<String> sort = new ArrayList<>();
        sort.add("timestamp");
        List<String> order = new ArrayList<>();
        order.add("DESC");
        mQueryOptions.put("_sort", sort);
        mQueryOptions.put("order", order);
        //mQueryOptions.put("title", new ArrayList<String>());
        //mQueryOptions.put("city", new ArrayList<String>());
    }

    @Override
    public void searchThings(String title) {
        if (!TextUtils.isEmpty(title)) {
            List<String> titles = mQueryOptions.get("title");
            if (titles == null) {
                titles = new ArrayList<>();
                titles.add(title);
                mQueryOptions.put("title", titles);
            } else {
                mQueryOptions.get("title").add(title);
            }
        }
        Log.d(TAG, "searchThings: " + mQueryOptions.toString());
        Call<List<Thing>> call = mApiModel.getThings(mQueryOptions);
        call.enqueue(new Callback<List<Thing>>() {
            @Override
            public void onResponse(Call<List<Thing>> call, Response<List<Thing>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success");
                    mView.onThingsFound(response.body());
                } else {
                    Log.d(TAG, "onResponse: error");
                }
            }

            @Override
            public void onFailure(Call<List<Thing>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
