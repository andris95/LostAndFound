package com.sanislo.lostandfound.view.usersThings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.things.adapter.ThingAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 30.03.17.
 */

public class UsersThingsFragment extends Fragment implements UsersThingsView {
    private String TAG = UsersThingsFragment.class.getSimpleName();
    private UsersThingsPresenter mUsersThingsPresenter;

    public static final String EXTRA_THINGS_TYPE = "EXTRA_THINGS_TYPE";

    @BindView(R.id.rv_things)
    RecyclerView rvThings;

    private int mType;
    private ThingAdapter mThingAdapter;

    public UsersThingsFragment() {};

    public static UsersThingsFragment newInstance(int thingType) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_THINGS_TYPE, thingType);
        UsersThingsFragment fragment = new UsersThingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(EXTRA_THINGS_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thing_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupThingList();
        mUsersThingsPresenter = new UsersThingsPresenterImpl(this);
        mUsersThingsPresenter.getUsersThings(PreferencesManager.getUserUID(getActivity()),
                mType);
    }

    private void setupThingList() {
        mThingAdapter = new ThingAdapter(getActivity(), null, null);
        rvThings.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvThings.setAdapter(mThingAdapter);
    }

    private ThingAdapter.OnClickListener mOnClickListener = new ThingAdapter.OnClickListener() {
        @Override
        public void onClickRootView(View view, Thing thing) {

        }
    };

    @Override
    public void onThingsLoaded(List<Thing> thingList) {
        Log.d(TAG, "onThingsLoaded: thingList: " + thingList);
        mThingAdapter.setThingList(thingList);
        mThingAdapter.notifyDataSetChanged();
    }

    public String getTitleForTab() {
        if (mType == Thing.TYPE_FOUND) {
            return getString(R.string.type_found);
        } else {
            return getString(R.string.type_lost);
        }
    }
}
