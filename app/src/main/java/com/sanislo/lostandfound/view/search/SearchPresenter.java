package com.sanislo.lostandfound.view.search;

/**
 * Created by root on 31.03.17.
 */

public interface SearchPresenter {
    void searchThings(String title);
    void filter(FilterQuery filterQuery);
    FilterQuery getFilterQuery();
}
