package com.sanislo.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
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

    @InjectPresenter
    ThingsPresenter mThingsPresenter;

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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

    private void startAddThingActivity() {
        Intent intent = new Intent(ThingsActivity.this, AddThingActivity.class);
        startActivity(intent);
    }
}
