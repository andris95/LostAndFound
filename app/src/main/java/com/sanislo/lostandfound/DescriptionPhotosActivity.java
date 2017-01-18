package com.sanislo.lostandfound;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sanislo.lostandfound.adapter.DescriptionPhotoPagerAdapter;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.view.FragmentDescriptionPhoto;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 02.01.17.
 */

public class DescriptionPhotosActivity extends BaseActivity {
    private final String TAG = DescriptionPhotosActivity.class.getSimpleName();
    public static final String KEY_THING_KEY = "THING_KEY";

    private String mThingKey;
    private int mStartPosition;
    private int mCurrentPosition;
    private FragmentDescriptionPhoto mFragmentDescriptionPhoto;
    private boolean mIsReturning;
    private DatabaseReference mThingReference;
    private DescriptionPhotoPagerAdapter mDescriptionPhotoPagerAdapter;
    private List<String> mDescriptionPhotosList;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning) {
                ImageView sharedElement = mFragmentDescriptionPhoto.getDescriptionImageView();
                Log.d(TAG, "onMapSharedElements: sharedElement: " + (sharedElement == null));
                if (sharedElement == null) {
                    // If shared element is null, then it has been scrolled off screen and
                    // no longer visible. In this case we cancel the shared element transition by
                    // removing the shared element from the shared elements map.
                    names.clear();
                    sharedElements.clear();
                } else if (mStartPosition != mCurrentPosition) {
                    // If the user has swiped to a different ViewPager page, then we need to
                    // remove the old shared element and replace it with the new shared element
                    // that should be transitioned instead.
                    names.clear();
                    names.add(sharedElement.getTransitionName());
                    sharedElements.clear();
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                }
            }
        }
    };

    private ValueEventListener mThingListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Thing thing = dataSnapshot.getValue(Thing.class);
            Log.d(TAG, "onDataChange: " + thing);
            mDescriptionPhotosList = thing.getDescriptionPhotos();
            displayDescriptionPhotosPager();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @BindView(R.id.vp_description_photos)
    ViewPager vpDescriptionPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_photos);
        ButterKnife.bind(this);
        postponeEnterTransition();
        setEnterSharedElementCallback(mCallback);
        fetchIntent(savedInstanceState);
        mThingReference = FirebaseUtils.getDatabase().getReference()
                .child(FirebaseConstants.THINGS)
                .child(mThingKey);
    }

    private void fetchIntent(Bundle savedInstanceState) {
        mThingKey = getIntent().getStringExtra(KEY_THING_KEY);
        mStartPosition = getIntent().getIntExtra(ThingDetailsActivity.EXTRA_START_POSITION, 0);
        if (savedInstanceState == null) {
            mCurrentPosition = mStartPosition;
        } else {
            mCurrentPosition = savedInstanceState.getInt(ThingDetailsActivity.EXTRA_UPDATED_POSITION);
        }
    }

    private void displayDescriptionPhotosPager() {
        mDescriptionPhotoPagerAdapter = new DescriptionPhotoPagerAdapter(getSupportFragmentManager(),
                mDescriptionPhotosList);
        mDescriptionPhotoPagerAdapter.setPrimaryItemChangeListener(new DescriptionPhotoPagerAdapter.PrimaryItemChangeListener() {
            @Override
            public void onPrimaryItemChanged(FragmentDescriptionPhoto fragmentDescriptionPhoto) {
                mFragmentDescriptionPhoto = fragmentDescriptionPhoto;
            }
        });
        vpDescriptionPhotos.setAdapter(mDescriptionPhotoPagerAdapter);
        vpDescriptionPhotos.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPosition = position;
            }
        });
        vpDescriptionPhotos.setCurrentItem(mStartPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThingReference.addValueEventListener(mThingListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mThingReference.removeEventListener(mThingListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(ThingDetailsActivity.EXTRA_UPDATED_POSITION, mCurrentPosition);
    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        Intent intent = new Intent();
        intent.putExtra(ThingDetailsActivity.EXTRA_START_POSITION, mStartPosition);
        intent.putExtra(ThingDetailsActivity.EXTRA_UPDATED_POSITION, mCurrentPosition);
        setResult(RESULT_OK, intent);
        super.finishAfterTransition();
    }
}
