package com.sanislo.lostandfound.view.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

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

    @BindView(R.id.sp_category)
    AppCompatSpinner spCategory;

    @BindView(R.id.sp_type)
    AppCompatSpinner spType;

    @BindView(R.id.sw_exact_match_title)
    SwitchCompat swExactMatch;

    private FilterListener mFilterListener;

    private ArrayAdapter<String> mTypeAdapter;
    private String[] mThingTypeArray;
    private List<Category> mCategories;
    private List<String> mCategoriesStringList;
    private ArrayAdapter<String> mCategoriesAdapter;

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
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initCategories() {
        mCategoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
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
                    mCategories = response.body();
                    mCategoriesStringList = new ArrayList<String>();
                    for(Category c : response.body()) {
                        mCategoriesStringList.add(c.getName());
                    }
                    mCategoriesAdapter.addAll(mCategoriesStringList);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.btn_show_results)
    public void filterDone() {
        boolean isExactMatch = swExactMatch.isChecked();

        FilterQuery filterQuery = new FilterQuery(isExactMatch,
                getSelectedCategoryName(),
                getSelectedType(),
                null);
        mFilterListener.onFilterDone(filterQuery);
        dismiss();
    }

    private String getSelectedType() {
        int selectedTypePosition = spType.getSelectedItemPosition();
        if (selectedTypePosition != 0) {
            return mThingTypeArray[selectedTypePosition];
        } else {
            return null;
        }
    }

    private String getSelectedCategoryName() {
        int selectedCategoryPosition = spCategory.getSelectedItemPosition();
        if (selectedCategoryPosition != 0) {
            return mCategoriesStringList.get(selectedCategoryPosition);
        } else {
            return null;
        }
    }

    public interface FilterListener {
        void onFilterDone(FilterQuery filterQuery);
    }
}
