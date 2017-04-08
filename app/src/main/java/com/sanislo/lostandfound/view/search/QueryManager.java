package com.sanislo.lostandfound.view.search;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 02.04.17.
 */

public class QueryManager {
    public static final String TAG = QueryManager.class.getSimpleName();
    public static final String _SORT = "_sort";
    public static final String _ORDER = "_order";
    public static final String TITLE = "title";
    public static final String TITLE_LIKE = "title_like";
    public static final String TYPE = "type";
    public static final String ORDER_ASC = "ASC";
    public static final String ORDER_DESC = "DESC";

    private FilterQuery mFilterQuery;
    private Set<String> mTitles = new HashSet<>();
    private String title;
    private String type;
    private String category;
    private ArrayList<String> mCities = new ArrayList<>();
    private boolean newestFirst;
    private String order = ORDER_DESC;
    private String sort = "timestamp";

    public QueryManager() {
    }

    public Map<String, String> toQueryOptions() {
        Map<String, String> queryOptions = new HashMap<>();
        queryOptions.put(_SORT, sort);
        queryOptions.put(_ORDER, order);
        if (!isTitleEmpty()) queryOptions.put(TITLE_LIKE, title);
        if (!isAnyType()) {
            queryOptions.put(TYPE, getTypeOption());
        }
        return queryOptions;
    }

    public boolean addTitle(String title) {
        return mTitles.add(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private boolean isTitleEmpty() {
        return TextUtils.isEmpty(title);
    }

    public void filter(FilterQuery filterQuery) {
        mFilterQuery = filterQuery;
        type = filterQuery.getType();
        category = filterQuery.getCategory();
        order = filterQuery.isNewestFirst() ? ORDER_DESC : ORDER_ASC;
    }

    private String getTitleOptions() {
        StringBuilder sb = new StringBuilder();
        String title = "title_like";
        boolean isFirst = true;
        for (String titleQuery : mTitles) {
            if (isFirst) {
                sb.append(titleQuery);
                isFirst = false;
                continue;
            }
            sb.append("&");
            sb.append(title);
            sb.append("=");
            sb.append(titleQuery);
        }
        Log.d(TAG, "getTitleOptions: " + sb.toString());
        String titleOptions = sb.toString();
        return titleOptions;
    }

    private String getTypeOption() {
        return type.toLowerCase();
    }

    public FilterQuery getFilterQuery() {
        if (mFilterQuery == null) {
            mFilterQuery = new FilterQuery(null,
                    null,
                    null,
                    -1,
                    true,
                    false);
        }
        return mFilterQuery;
    }

    private boolean isAnyType() {
        return TextUtils.isEmpty(type);
    }
}
