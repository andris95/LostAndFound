package com.sanislo.lostandfound;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.interfaces.ThingsView;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.presenter.ThingsPresenter;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.view.ThingViewHolder;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThingsActivity extends BaseActivity implements ThingsView {
    public static final String TAG = ThingsActivity.class.getSimpleName();
    private final String KEY_POSITION = "KEY_POSITION";
    private final String KEY_CURRENT_POSITION = "KEY_CURRENT_POSITION";

    @BindView(R.id.rv_things)
    RecyclerView rvThings;

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    private FirebaseAuth mFirebaseAuth;
    private Query mThingQuery;
    private ThingAdapter mThingAdapter;

    private boolean mIsDescriptionPhotosActivityStarted;
    private Bundle mTmpReenterState;

    @InjectPresenter
    ThingsPresenter mThingsPresenter;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                int startingPosition = mTmpReenterState.getInt(KEY_POSITION);
                int currentPosition = mTmpReenterState.getInt(KEY_CURRENT_POSITION);
                if (startingPosition != currentPosition) {
                    // If startingPosition != currentPosition the user must have swiped to a
                    // different page in the DetailsActivity. We must update the shared element
                    // so that the correct one falls into place.
                    View newSharedElement = mThingAdapter.getSharedView(currentPosition);
                    if (newSharedElement != null) {
                        names.clear();
                        names.add(newSharedElement.getTransitionName());
                        sharedElements.clear();
                        sharedElements.put(newSharedElement.getTransitionName(), newSharedElement);
                    }
                }
                mTmpReenterState = null;
            }
        }
    };

    private ThingAdapter.OnClickListener mThingClickListener = new ThingAdapter.OnClickListener() {
        @Override
        public void onClickAddComment(Thing thing, String text) {
            mThingsPresenter.addComment(thing, text);
        }

        @Override
        public void onClickDescription(int position) {
            boolean isExpaned = (position == mThingAdapter.getExpandedPosition());
            int expandedPosition = isExpaned ? RecyclerView.NO_POSITION : position;
            mThingAdapter.setExpandedPosition(expandedPosition);
            //TransitionManager.beginDelayedTransition(rvThings);
            mThingAdapter.notifyDataSetChanged();
        }

        @Override
        public void onClickDescriptionPhoto(View view, int position, String thingKey) {
            Intent intent = new Intent(ThingsActivity.this, DescriptionPhotosActivity.class);
            intent.putExtra("THING_KEY", thingKey);
            intent.putExtra(KEY_POSITION, position);
            
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(ThingsActivity.this,
                            view,
                            view.getTransitionName());
            startActivity(intent, options.toBundle());
        }

        @Override
        public void onScrolledDescriptionList() {
            startPostponedEnterTransition();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setExitSharedElementCallback(mCallback);
        mThingsPresenter = new ThingsPresenter();
        initFirebase();
        initToolbar();
        initThings();
    }

    private void initFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TAG);
    }

    private void initThings() {
        mThingQuery = FirebaseUtils.getDatabase().getReference()
                .child(FirebaseConstants.THINGS);
        mThingAdapter = new ThingAdapter(Thing.class,
                R.layout.item_thing,
                ThingViewHolder.class,
                mThingQuery);
        mThingAdapter.setOnClickListener(mThingClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvThings.setLayoutManager(layoutManager);
        rvThings.setAdapter(mThingAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_thing:
                startAddThingActivity();
                return true;
            case R.id.menu_sign_out:
                mFirebaseAuth.signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThingAdapter.cleanup();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        Log.d(TAG, "onActivityReenter: " + data.getExtras().toString());
        mTmpReenterState = new Bundle(data.getExtras());
        int startingPosition = mTmpReenterState.getInt(KEY_POSITION);
        int currentPosition = mTmpReenterState.getInt(KEY_CURRENT_POSITION);
        if (startingPosition != currentPosition) {
            mThingAdapter.scrollDescriptionPhotosList(currentPosition);
        }
        postponeEnterTransition();

    }

    private void startAddThingActivity() {
        Intent intent = new Intent(ThingsActivity.this, AddThingActivity.class);
        startActivity(intent);
    }
}
