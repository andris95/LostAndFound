package com.sanislo.lostandfound.view.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnChipClickListener;
import com.robertlevonyan.views.chip.OnCloseClickListener;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 31.03.17.
 */

public class SearchActivity extends BaseActivity implements SearchThingView {
    private String TAG = SearchActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.ll_chip_container)
    LinearLayout chipContainer;

    @BindView(R.id.rv_things)
    RecyclerView rvThings;

    private SearchView mSearchView;
    private SearchPresenter mSearchPresenter;
    private ThingAdapter mThingAdapter;

    private SearchView.OnQueryTextListener mSearchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // newText is text entered by user to SearchThingView
            mThingAdapter.clear();
            mThingAdapter.notifyDataSetChanged();
            mSearchPresenter.searchThings(newText);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupThingsAdapter();
        mSearchPresenter = new SearchPresenterImpl(this);
        mSearchPresenter.searchThings("");
    }

    private void setupThingsAdapter() {
        mThingAdapter = new ThingAdapter(SearchActivity.this, null, null);
        rvThings.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        rvThings.setAdapter(mThingAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setOnQueryTextListener(mSearchListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onThingsFound(List<Thing> thingList) {
        mThingAdapter.setThingList(thingList);
        mThingAdapter.notifyDataSetChanged();

        String query = mSearchView.getQuery().toString();
        if (!TextUtils.isEmpty(query) && thingList != null && !thingList.isEmpty()) {
            Chip chip = new Chip(SearchActivity.this);
            chip.setChipText(query);
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            chipParams.setMargins(8, 8, 8, 8);
            chip.setLayoutParams(chipParams);
            chip.setClosable(true);
            chip.setOnCloseClickListener(new OnCloseClickListener() {
                @Override
                public void onCloseClick(View v) {
                    Toast.makeText(getApplicationContext(), "Close", Toast.LENGTH_SHORT).show();
                }
            });
            chip.setOnChipClickListener(new OnChipClickListener() {
                @Override
                public void onChipClick(View v) {
                    try {
                        Chip c = (Chip) v;
                        Toast.makeText(getApplicationContext(), c.getChipText(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            chipContainer.addView(chip);
        }
    }
}
