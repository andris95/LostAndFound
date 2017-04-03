package com.sanislo.lostandfound.view.search;

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
    private Set<String> mTitles = new HashSet<>();
    private String type;
    private ArrayList<String> mCities = new ArrayList<>();
    private boolean mIsExactMatch;
    private String mOrder = "DESC";
    private String mSort = "timestamp";

    public QueryManager() {
    }

    public Map<String, String> toQueryOptions() {
        Map<String, String> queryOptions = new HashMap<>();
        queryOptions.put(_SORT, mSort);
        queryOptions.put(_ORDER, mOrder);
        queryOptions.put(TITLE, getTitleOptions());
        return queryOptions;
    }

    public boolean addTitle(String title) {
        return mTitles.add(title);
    }

    private String getTitleOptions() {
        StringBuilder sb = new StringBuilder();
        String title = mIsExactMatch ? "title" : "title_like";
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

    public String toQuery() {
        String query = null;
        return query;
    }
}
