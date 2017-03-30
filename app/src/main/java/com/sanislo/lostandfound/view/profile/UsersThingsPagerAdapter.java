package com.sanislo.lostandfound.view.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sanislo.lostandfound.model.Thing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 30.03.17.
 */

public class UsersThingsPagerAdapter extends FragmentStatePagerAdapter {
    private String TAG = UsersThingsPagerAdapter.class.getSimpleName();
    private List<Fragment> mFragmentList;

    public UsersThingsPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    public UsersThingsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(UsersThingsFragment.newInstance(Thing.TYPE_LOST));
        mFragmentList.add(UsersThingsFragment.newInstance(Thing.TYPE_FOUND));
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //TODO try this later
        //return ((UsersThingsFragment) mFragmentList.get(position)).getTitleForTab();
        switch (position) {
            case 0:
                return "Lost";
            case 1:
                return "Found";
            default:
                return null;
        }
    }
}
