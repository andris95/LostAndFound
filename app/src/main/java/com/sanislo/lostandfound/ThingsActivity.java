package com.sanislo.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.interfaces.ThingsView;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.presenter.ThingsPresenter;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.utils.Utils;
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
        mThingsPresenter = new ThingsPresenter();
        initFirebase();
        initToolbar();
        initThings();

        String originalUrl = "https://wallpaperscraft.com/image/joy_jennifer_lawrence_2015_105464_1920x1080.jpg";
        String croppedUrl = Utils.getCropedImageUrl(originalUrl);
        Log.d(TAG, "onCreate: croppedUrl: " + croppedUrl);
        Log.d(TAG, "onCreate: ~~~~~~");
        croppedUrl = Utils.getCroppedImageUrlShort(originalUrl);
        Log.d(TAG, "onCreate: croppedUrl: " + croppedUrl);
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
                mThingQuery = FirebaseUtils.getDatabase().getReference()
                        .child(FirebaseConstants.THINGS)
                        .orderByChild("type")
                        .equalTo(1);
                mThingAdapter.notifyDataSetChanged();
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
