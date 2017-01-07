package com.sanislo.lostandfound.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.sanislo.lostandfound.view.FragmentDescriptionPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 02.01.17.
 */

public class DescriptionPhotoPagerAdapter extends FragmentStatePagerAdapter {
    private PrimaryItemChangeListener mPrimaryItemChangeListener;
    private List<String> mDescriptionPhotoPaths = new ArrayList<>();

    public DescriptionPhotoPagerAdapter(FragmentManager fm, List<String> descriptionPhotoPaths) {
        super(fm);
        mDescriptionPhotoPaths = descriptionPhotoPaths;
    }

    public void setPrimaryItemChangeListener(PrimaryItemChangeListener primaryItemChangeListener) {
        mPrimaryItemChangeListener = primaryItemChangeListener;
    }

    public void setDescriptionPhotoPaths(List<String> descriptionPhotoPaths) {
        mDescriptionPhotoPaths = descriptionPhotoPaths;
    }

    @Override
    public Fragment getItem(int position) {
        String photoPath = mDescriptionPhotoPaths.get(position);
        return FragmentDescriptionPhoto.newInstance(photoPath, position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (mPrimaryItemChangeListener != null) {
            FragmentDescriptionPhoto fragmentDescriptionPhoto = (FragmentDescriptionPhoto) object;
            mPrimaryItemChangeListener.onPrimaryItemChanged(fragmentDescriptionPhoto);
        }
    }

    @Override
    public int getCount() {
        return mDescriptionPhotoPaths.size();
    }

    public interface PrimaryItemChangeListener {
        void onPrimaryItemChanged(FragmentDescriptionPhoto fragmentDescriptionPhoto);
    }
}
