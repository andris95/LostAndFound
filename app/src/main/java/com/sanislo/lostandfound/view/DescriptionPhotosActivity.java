package com.sanislo.lostandfound.view;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewParent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sanislo.lostandfound.BaseActivity;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.DescriptionPhotoPagerAdapter;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 02.01.17.
 */

public class DescriptionPhotosActivity extends BaseActivity {
    private final String TAG = DescriptionPhotosActivity.class.getSimpleName();
    private final String KEY_THING_KEY = "THING_KEY";

    private String mThingKey;
    private DatabaseReference mThingReference;
    private DescriptionPhotoPagerAdapter mDescriptionPhotoPagerAdapter;
    private List<String> mDescriptionPhotosList;

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
        mThingKey = getIntent().getStringExtra(KEY_THING_KEY);
        Log.d(TAG, "onCreate: " + mThingKey);
        mThingReference = FirebaseUtils.getDatabase().getReference()
                .child(FirebaseConstants.THINGS).child(mThingKey);
    }

    private void displayDescriptionPhotosPager() {
        mDescriptionPhotoPagerAdapter = new DescriptionPhotoPagerAdapter(getSupportFragmentManager(),
                mDescriptionPhotosList);
        vpDescriptionPhotos.setAdapter(mDescriptionPhotoPagerAdapter);
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
}
