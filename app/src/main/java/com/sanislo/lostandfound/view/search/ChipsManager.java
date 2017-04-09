package com.sanislo.lostandfound.view.search;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnChipClickListener;
import com.sanislo.lostandfound.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 09.04.17.
 */

public class ChipsManager extends LinearLayout {
    public static final String TAG = ChipsManager.class.getSimpleName();
    private FilterQuery mFilterQuery;
    private List<Chip> mChipList = new ArrayList<>();

    public ChipsManager(Context context) {
        super(context);
    }

    public ChipsManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChipsManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChipsManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {

    }

    //public static final int CHIP_TYPE = 0;
    public static final int CHIP_CATEGORY = 0;
    public static final int CHIP_CITY = 2;
    public static final int CHIP_RADIUS = 3;
    public static final int CHIP_ORDER = 4;
    public static final int CHIP_RETURNED = 5;

    public void setChips(FilterQuery filterQuery) {
        mFilterQuery = filterQuery;
        Log.d(TAG, "setChips: " + mFilterQuery);
        setChipCategory();
    }

    private void setChipCategory() {
        Chip categoryChip = getChipAt(CHIP_CATEGORY);
        if (categoryChip == null) {
            categoryChip = new Chip(getContext());
            categoryChip.setTag(CHIP_CATEGORY);
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, 60);
            chipParams.setMargins(8, 8, 8, 8);
            categoryChip.setLayoutParams(chipParams);
            categoryChip.setClosable(false);
            categoryChip.setOnChipClickListener(mOnChipClickListener);
            if (!TextUtils.isEmpty(mFilterQuery.getCategory())) {
                categoryChip.setChipText(mFilterQuery.getCategory());
            } else {
                categoryChip.setChipText(getContext().getString(R.string.category_any));
            }
            addView(categoryChip, CHIP_CATEGORY);
        } else {
            if (!TextUtils.isEmpty(mFilterQuery.getCategory())) {
                categoryChip.setChipText(mFilterQuery.getCategory());
            } else {
                categoryChip.setChipText(getContext().getString(R.string.category_any));
            }
        }
        Log.d(TAG, "setChipCategory: chipText: " + categoryChip.getChipText());
        Log.d(TAG, "setChipCategory: chipText: " + getChipAt(CHIP_CATEGORY).getChipText());
        categoryChip.invalidate();
    }

    private Chip getChipAt(int chipPos) {
        Chip chip = (Chip) getChildAt(chipPos);
        return chip;
    }

    private OnChipClickListener mOnChipClickListener = new OnChipClickListener() {
        @Override
        public void onChipClick(View v) {
            Log.d(TAG, "onChipClick: " + v.getTag());
        }
    };
}
