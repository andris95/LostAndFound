package com.sanislo.lostandfound.view.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.Category;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 03.04.17.
 */

public class FilterDialogFragment extends DialogFragment {
    private String TAG = FilterDialogFragment.class.getSimpleName();
    public static final String EXTRA_FILTER_QUERY = "EXTRA_FILTER_QUERY";

    @BindView(R.id.sp_category)
    AppCompatSpinner spCategory;

    @BindView(R.id.sp_type)
    AppCompatSpinner spType;

    @BindView(R.id.sw_newest_first)
    SwitchCompat swNewestFirst;

    @BindView(R.id.sw_returned)
    SwitchCompat swOnlyReturned;

    private FilterListener mFilterListener;
    private FilterQuery mFilterQuery;
    private ArrayAdapter<String> mTypeAdapter;
    private String[] mThingTypeArray;
    private List<String> mCategoriesStringList;
    private ArrayAdapter<String> mCategoriesAdapter;

    public FilterDialogFragment() {
    }

    public static FilterDialogFragment newInstance(FilterQuery filterQuery) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FILTER_QUERY, filterQuery);
        FilterDialogFragment fragment = new FilterDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mFilterListener = (FilterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FilterListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilterQuery = getArguments().getParcelable(EXTRA_FILTER_QUERY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_filter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initCategories();
        loadCategories();
        initTypeSpinner();
        swNewestFirst.setChecked(mFilterQuery.isNewestFirst());
        swOnlyReturned.setChecked(mFilterQuery.isReturnedOnly());
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initCategories() {
        mCategoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        mCategoriesAdapter.add(getString(R.string.category_any));
        mCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(mCategoriesAdapter);
        //spCategory.setOnItemSelectedListener(mCategorySelectedListener);
    }

    private void initTypeSpinner() {
        mThingTypeArray = getResources().getStringArray(R.array.thing_type_filter);
        mTypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                mThingTypeArray);
        mTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(mTypeAdapter);
        //spType.setOnItemSelectedListener(mTypeSelectedListener);
    }

    private void loadCategories() {
        ApiModel apiModel = new ApiModelImpl();
        Call<List<Category>> call = apiModel.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    parseCategories(response.body());
                    setLastSelectedCategory();
                } else {
                    Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void parseCategories(List<Category> categoryList) {
        mCategoriesStringList = new ArrayList<String>();
        for(Category c : categoryList) {
            mCategoriesStringList.add(c.getName());
        }
        mCategoriesAdapter.addAll(mCategoriesStringList);
    }

    private void setLastSelectedCategory() {
        String category = mFilterQuery.getCategory();
        Log.d(TAG, "setLastSelectedCategory: category: " + category);
        if (TextUtils.isEmpty(category)) {
            spCategory.setSelection(0);
        } else {
            //
            int indexOfCategory = mCategoriesStringList.indexOf(category);
            Log.d(TAG, "setLastSelectedCategory: indexOfCategory: " + indexOfCategory);
            if (indexOfCategory != -1) {
                //Add 1, because the first item is "Any category"!!!
                spCategory.setSelection(indexOfCategory + 1);
            }
        }
    }

    @OnClick(R.id.btn_show_results)
    public void filterDone() {
        boolean newestFirst = swNewestFirst.isChecked();
        boolean returnedOnly = swOnlyReturned.isChecked();
        //String type = (spType.getSelectedItemPosition() == 0) ? "" : (String) spType.getSelectedItem();

        String category = (spCategory.getSelectedItemPosition() == 0) ? "" : (String) spCategory.getSelectedItem();

        FilterQuery filterQuery = new FilterQuery(category,
                getSelectedType(),
                null,
                -1,
                newestFirst,
                returnedOnly);
        mFilterListener.onFilterDone(filterQuery);
        Log.d(TAG, "filterDone: filterQuery: " + filterQuery);
        dismiss();
    }

    private int getSelectedType() {
        int type = spType.getSelectedItemPosition();
        // return -1 because the first item is "Select type"
        return type - 1;
    }

    public interface FilterListener {
        void onFilterDone(FilterQuery filterQuery);
    }
}
