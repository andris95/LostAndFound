package com.sanislo.lostandfound.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sanislo.lostandfound.view.FragmentDescriptionPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 02.01.17.
 */

public class DescriptionPhotoPagerAdapter extends FragmentPagerAdapter {
    private List<String> mDescriptionPhotoPaths = new ArrayList<>();

    public DescriptionPhotoPagerAdapter(FragmentManager fm, List<String> descriptionPhotoPaths) {
        super(fm);
        mDescriptionPhotoPaths = descriptionPhotoPaths;
    }

    public void setDescriptionPhotoPaths(List<String> descriptionPhotoPaths) {
        mDescriptionPhotoPaths = descriptionPhotoPaths;
    }

    @Override
    public Fragment getItem(int position) {
        String photoPath = mDescriptionPhotoPaths.get(position);
        return FragmentDescriptionPhoto.newInstance(photoPath);
    }

    @Override
    public int getCount() {
        return mDescriptionPhotoPaths.size();
    }
}
