package com.sanislo.lostandfound.view.things;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.ThingAdapter;
import com.sanislo.lostandfound.interfaces.ThingsView;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.presenter.ThingsPresenter;
import com.sanislo.lostandfound.presenter.ThingsPresenterImpl;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.addThing.AddThingActivity;
import com.sanislo.lostandfound.view.profile.ProfileActivity;
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
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(ThingsActivity.this,
                            view.findViewById(R.id.iv_thing_photo),
                            getString(R.string.transition_description_photo));
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
        mThingsPresenter.getThings();
        mThingsPresenter.getProfile(ThingsActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private Drawer.OnDrawerItemClickListener mOnDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            Log.d(TAG, "onItemClick: position: " + position);
            return true;
        }
    };

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
        drawerItems.add(things);
        drawerItems.add(settings);
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
        mThingAdapter.setThingList(thingList);
        mThingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProfileLoaded(User user) {
        mUser = user;
        setupDrawer();
    }
}
