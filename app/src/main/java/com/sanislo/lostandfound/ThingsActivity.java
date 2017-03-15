package com.sanislo.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.interfaces.ThingsView;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.presenter.ThingsPresenter;
import com.sanislo.lostandfound.presenter.ThingsPresenterImpl;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.view.ThingViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThingsActivity extends BaseActivity implements ThingsView {
    public static final String TAG = ThingsActivity.class.getSimpleName();

    @BindView(R.id.rv_things)
    RecyclerView rvThings;

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    private FirebaseAuth mFirebaseAuth;
    private Query mThingQuery;
    private ThingAdapter mThingAdapter;

    private ThingsPresenter mThingsPresenter;

    private ThingAdapter.OnClickListener mThingClickListener = new ThingAdapter.OnClickListener() {

        @Override
        public void onClickRootView(View view, String thingKey) {
            Intent intent = new Intent(ThingsActivity.this, ThingDetailsActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(ThingsActivity.this,
                            view.findViewById(R.id.iv_thing_photo),
                            getString(R.string.transition_description_photo));
            intent.putExtra(ThingDetailsActivity.EXTRA_THING_PATH, thingKey);
            startActivity(intent, options.toBundle());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mThingsPresenter = new ThingsPresenterImpl(this);
        initFirebase();
        initToolbar();
        initThings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThingsPresenter.getThings();
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
                R.layout.item_thing_simple,
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
            case R.id.menu_find_thing:
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

    private void startAddThingActivity() {
        Intent intent = new Intent(ThingsActivity.this, AddThingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onThingsLoaded(List<com.sanislo.lostandfound.model.api.Thing> thingList) {

    }
}
