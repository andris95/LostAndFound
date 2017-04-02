package com.sanislo.lostandfound.view.things;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sanislo.lostandfound.FakeDataGenerator;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.interfaces.ThingsView;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.presenter.ThingsPresenter;
import com.sanislo.lostandfound.presenter.ThingsPresenterImpl;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.addThing.AddThingActivity;
import com.sanislo.lostandfound.view.map.MapActivity;
import com.sanislo.lostandfound.view.profile.ProfileActivity;
import com.sanislo.lostandfound.view.search.SearchActivity;
import com.sanislo.lostandfound.view.thingDetails.ThingDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThingsActivity extends BaseActivity implements ThingsView {
    public static final String TAG = ThingsActivity.class.getSimpleName();

    @BindView(R.id.rv_things)
    RecyclerView rvThings;

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @BindView(R.id.refresh_things)
        SwipeRefreshLayout swipeRefreshThings;

    private FirebaseAuth mFirebaseAuth;
    private ThingAdapter mThingAdapter;

    private ThingsPresenter mThingsPresenter;
    private Drawer mDrawer;
    private User mUser;

    private ThingAdapter.OnClickListener mThingClickListener = new ThingAdapter.OnClickListener() {

        @Override
        public void onClickRootView(View view, String thingKey) {

        }

        @Override
        public void onClickRootView(View view, Thing thing) {
            Intent intent = new Intent(ThingsActivity.this, ThingDetailsActivity.class);
            View ivThingPhoto = ButterKnife.findById(view, R.id.iv_thing_photo);
            View ivAvatar = ButterKnife.findById(view, R.id.iv_thing_author_avatar);
            Pair<View, String> p1 = Pair.create(ivAvatar, getString(R.string.transition_avatar));
            Pair<View, String> p2 = Pair.create(ivThingPhoto, getString(R.string.transition_description_photo));
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(ThingsActivity.this, p1, p2);
            intent.putExtra(ThingDetailsActivity.EXTRA_THING, thing);
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
        initThingsAdapter();
        setupSwipeRefresh();
        mThingsPresenter.getThings();
        mThingsPresenter.getProfile(ThingsActivity.this);
    }

    private void setupSwipeRefresh() {
        swipeRefreshThings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mThingAdapter != null) {
                    mThingAdapter.clear();
                    mThingAdapter.notifyDataSetChanged();
                }
                mThingsPresenter.getThings();
            }
        });
    }

    private Drawer.OnDrawerItemClickListener mOnDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            Log.d(TAG, "onItemClick: position: " + position);
            switch (position) {
                case 3:
                    openSearchActivity();
                    return true;
                case 4:
                    Intent intent = new Intent(ThingsActivity.this, MapActivity.class);
                    startActivity(intent);
                    break;
                case 5:
                    FakeDataGenerator fakeDataGenerator = new FakeDataGenerator(ThingsActivity.this, mUser);
                    fakeDataGenerator.postFakeThings();
                    break;
            }
            return true;
        }
    };

    private void openSearchActivity() {
        Intent intent = new Intent(ThingsActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(ThingsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void setupDrawer() {
        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                .withName(mUser.getFullName())
                .withEmail(mUser.getEmailAddress())
                .withTextColor(Color.BLACK);
        if (!TextUtils.isEmpty(mUser.getAvatarURL())) profileDrawerItem.withIcon(mUser.getAvatarURL());

        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
                .withActivity(ThingsActivity.this)
                .withProfileImagesVisible(TextUtils.isEmpty(mUser.getAvatarURL()))
                .addProfiles(profileDrawerItem)
                .withTextColor(Color.BLACK)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        openProfileActivity();
                        return true;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
                .withOnlyMainProfileImageVisible(true);

        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(ThingsActivity.this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(accountHeaderBuilder.build())
                .withOnDrawerItemClickListener(mOnDrawerItemClickListener)
                .withDrawerItems(getDrawerItems());
        mDrawer = drawerBuilder.build();
    }

    private List<IDrawerItem> getDrawerItems() {
        List<IDrawerItem> drawerItems = new ArrayList<>();
        PrimaryDrawerItem things = new PrimaryDrawerItem()
                .withName(R.string.drawer_things);
        PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName(R.string.drawer_settings);
        PrimaryDrawerItem search = new PrimaryDrawerItem()
                .withName(R.string.drawer_search);
        PrimaryDrawerItem nearbyThings = new PrimaryDrawerItem()
                .withName(R.string.drawer_nearby_things);
        PrimaryDrawerItem fakeThings = new PrimaryDrawerItem()
                .withName("Create 500 fake things");
        drawerItems.add(things);
        drawerItems.add(settings);
        drawerItems.add(search);
        drawerItems.add(nearbyThings);
        drawerItems.add(fakeThings);
        return drawerItems;
    }

    private void initFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TAG);
    }

    private void initThingsAdapter() {
        mThingAdapter = new ThingAdapter(ThingsActivity.this, null, null);
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

    private void startAddThingActivity() {
        Intent intent = new Intent(ThingsActivity.this, AddThingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onThingsLoaded(List<Thing> thingList) {
        if (swipeRefreshThings.isRefreshing()) {
            swipeRefreshThings.setRefreshing(false);
        }
        mThingAdapter.setThingList(thingList);
        mThingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProfileLoaded(User user) {
        mUser = user;
        setupDrawer();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
