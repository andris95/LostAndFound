package com.sanislo.lostandfound.view.search;

import android.util.Log;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

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
    private QueryManager mQueryManager = new QueryManager();

    public SearchPresenterImpl(SearchThingView view) {
        mView = view;
    }

    @Override
    public void searchThings(String title) {
        mQueryManager.setTitle(title);
        doQuery();
    }

    @Override
    public void filter(FilterQuery filterQuery) {
        mQueryManager.filter(filterQuery);
        doQuery();
    }

    @Override
    public FilterQuery getFilterQuery() {
        return mQueryManager.getFilterQuery();
    }

    private void doQuery() {
        Map<String, String> queryOptions = mQueryManager.toQueryOptions();
        Log.d(TAG, "searchThings: queryOptions: " + queryOptions.toString());
        Call<List<Thing>> call = mApiModel.getThings(queryOptions);
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
