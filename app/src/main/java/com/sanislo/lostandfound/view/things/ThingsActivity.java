package com.sanislo.lostandfound.view.things;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.utils.ImageUtils;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.addThing.AddThingActivity;
import com.sanislo.lostandfound.view.chatHeader.ChatHeaderActivity;
import com.sanislo.lostandfound.view.map.MapActivity;
import com.sanislo.lostandfound.view.profile.ProfileActivity;
import com.sanislo.lostandfound.view.search.SearchActivity;
import com.sanislo.lostandfound.view.thingDetails.ThingDetailsActivity;
import com.sanislo.lostandfound.view.things.adapter.ThingAdapter;
import com.sanislo.lostandfound.view.things.profile.ProfileContract;
import com.sanislo.lostandfound.view.things.profile.ProfilePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThingsActivity extends BaseActivity implements ThingsContract.View {
    public static final String TAG = ThingsActivity.class.getSimpleName();
    public static final String EXTRA_USER = "EXTRA_USER";

    @BindView(R.id.rv_things)
    RecyclerView rvThings;

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @BindView(R.id.refresh_things)
    SwipeRefreshLayout swipeRefreshThings;

    private ProfilePresenter mProfilePresenter;
    private FirebaseAuth mFirebaseAuth;
    private ThingAdapter mThingAdapter;

    private LinearLayoutManager mLinearLayoutManager;
    private Drawer mDrawer;
    private AccountHeader mAccountHeader;
    private User mUser = new User();
    private int mClickedDrawerItemPos = -100;

    private Bitmap mLeftBitmap;
    private Bitmap mRightBitmap;

    private void initBitmapsForSwipe() {
        Drawable drawable = ContextCompat.getDrawable(ThingsActivity.this, R.drawable.bookmark_check);
        mLeftBitmap = ImageUtils.drawableToBitmap(drawable);

        drawable = ContextCompat.getDrawable(ThingsActivity.this, R.drawable.delete);
        mRightBitmap = ImageUtils.drawableToBitmap(drawable);
    }

    private ProfileContract.View mProfileView = new ProfileContract.View() {
        @Override
        public void onProfileLoaded(User user) {
            mUser = user;
            updateProfile();
        }

        @Override
        public void onError() {
            makeToast("Error getting user profile");
        }

        @Override
        public void setPresenter(ProfileContract.Presenter presenter) {

        }
    };

    private ThingAdapter.OnClickListener mThingClickListener = new ThingAdapter.OnClickListener() {
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

    private boolean isLoadFromSwipeRefresh;
    private boolean isLoading;
    private boolean isLastPage;
    private int mCurrentPage = 1;
    private static final int PAGE_SIZE = 10;
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLinearLayoutManager.getChildCount();
            int totalItemCount = mLinearLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    loadMoreItems();
                }
            }
        }
    };

    private void loadMoreItems() {
        isLoading = true;
        mCurrentPage++;
        mThingsContractPresenter.loadThings(mCurrentPage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initBitmapsForSwipe();
        initFirebase();
        initToolbar();
        initCloseAppDialog();
        setupDrawer();
        initThingsAdapter();
        setupSwipeRefresh();
        mProfilePresenter = new ProfilePresenter(mProfileView);
        if (getIntent().getParcelableExtra(EXTRA_USER) != null) {
            mUser = getIntent().getParcelableExtra(EXTRA_USER);
            updateProfile();
        } else {
            mProfilePresenter.loadProfile(getAuthenticatedUserUID());
        }
        mThingsContractPresenter = new com.sanislo.lostandfound.view.things.ThingsPresenter(this);
        mThingsContractPresenter.loadThings(0);
    }

    private void setupSwipeRefresh() {
        swipeRefreshThings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mThingAdapter != null) {
                    mThingAdapter.clear();
                    mThingAdapter.notifyDataSetChanged();
                }
                mThingsContractPresenter.loadThings(1);
            }
        });
    }

    private Drawer.OnDrawerItemClickListener mOnDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            Log.d(TAG, "onItemClick: position: " + position);
            Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
            mClickedDrawerItemPos = position;
            mDrawer.closeDrawer();
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

    private void openMapsActivity() {
        Intent intent = new Intent(ThingsActivity.this, MapActivity.class);
        startActivity(intent);
    }

    private void openChatHeaders() {
        Intent intent = new Intent(ThingsActivity.this, ChatHeaderActivity.class);
        startActivity(intent);
    }

    private void setupDrawer() {
        setupAccountHeader();
        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(ThingsActivity.this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(mAccountHeader)
                .withOnDrawerItemClickListener(mOnDrawerItemClickListener)
                .withStickyDrawerItems(getStickyFooterItems())
                .withDrawerItems(getDrawerItems());
        mDrawer = drawerBuilder.build();
        mDrawer.setSelection(-1);
        addDrawerCloseListener();
    }

    private List<IDrawerItem> getStickyFooterItems() {
        ArrayList<IDrawerItem> list = new ArrayList<>();
        PrimaryDrawerItem logout = new PrimaryDrawerItem()
                .withName(R.string.sign_out)
                .withIcon(R.drawable.logout);
        list.add(logout);
        return list;
    }

    private void addDrawerCloseListener() {
        mDrawer.getDrawerLayout().addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                switch (mClickedDrawerItemPos) {
                    case 1:
                        startAddThingActivity();
                        break;
                    case 2:
                        openSearchActivity();
                        break;
                    case 3:
                        openMapsActivity();
                        break;
                    case 4:
                        openChatHeaders();
                    case 5:
                        FakeDataGenerator fakeDataGenerator = new FakeDataGenerator(ThingsActivity.this, mUser);
                        fakeDataGenerator.postCloseFakeThings(ThingsActivity.this);
                        break;
                    case -1:
                        mFirebaseAuth.signOut();
                        break;
                }
                mClickedDrawerItemPos = -100;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void setupAccountHeader() {
        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                .withName(mUser.getFullName())
                .withEmail(mUser.getEmailAddress())
                .withTextColor(Color.BLACK);
        if (!TextUtils.isEmpty(mUser.getAvatarURL())) profileDrawerItem.withIcon(mUser.getAvatarURL());

        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
                .withActivity(ThingsActivity.this)
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
        mAccountHeader = accountHeaderBuilder.build();
    }

    private List<IDrawerItem> getDrawerItems() {
        List<IDrawerItem> drawerItems = new ArrayList<>();
        PrimaryDrawerItem addThing = new PrimaryDrawerItem()
                .withName(R.string.add_thing)
                .withIcon(R.drawable.plus)
                .withSelectable(false);
        PrimaryDrawerItem search = new PrimaryDrawerItem()
                .withName(R.string.drawer_search)
                .withIcon(R.drawable.magnify_black)
                .withSelectable(false);
        PrimaryDrawerItem nearbyThings = new PrimaryDrawerItem()
                .withName(R.string.drawer_nearby_things)
                .withIcon(R.drawable.map_marker_radius)
                .withSelectable(false);
        PrimaryDrawerItem chatHeaders = new PrimaryDrawerItem()
                .withName(R.string.drawer_chats)
                .withIcon(R.drawable.message_black)
                .withSelectable(false);
        PrimaryDrawerItem fakeThings = new PrimaryDrawerItem()
                .withName("Create 500 fake things");
        drawerItems.add(addThing);
        drawerItems.add(search);
        drawerItems.add(nearbyThings);
        drawerItems.add(chatHeaders);
        drawerItems.add(fakeThings);
        //drawerItems.add(logout);
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
        mThingAdapter = new ThingAdapter(ThingsActivity.this,
                mThingClickListener,
                mThingList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvThings.setLayoutManager(mLinearLayoutManager);
        rvThings.addOnScrollListener(mOnScrollListener);
        rvThings.setAdapter(mThingAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(rvThings);
    }

    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT){
                //mThingAdapter.removeItem(position);
                int id = mThingAdapter.getItem(position).getId();
                mThingsContractPresenter.removeThing(id);
                mThingAdapter.removeItem(position);
                makeToast("DELETE pos" + position + " / " + id);
            } else {
                Thing thing = mThingAdapter.getItem(position);
                mThingsContractPresenter.updateThing(thing.getId(), true);
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            ThingsActivity.this.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void onChildDraw(Canvas c,
                             RecyclerView recyclerView,
                             RecyclerView.ViewHolder viewHolder,
                             float dX,
                             float dY,
                             int actionState,
                             boolean isCurrentlyActive) {
        Paint p = new Paint();
        //right positive, left negative
        Log.d(TAG, "onChildDraw: dX: " + dX);
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            //float width = height / 3;
            float width = mRightBitmap.getWidth();
            Log.d(TAG, "onChildDraw: width: " + width);

            if(dX > 0){
                p.setColor(Color.parseColor("#388E3C"));
                RectF background = new RectF((float) itemView.getLeft(),
                        (float) itemView.getTop(),
                        dX,
                        (float) itemView.getBottom());
                c.drawRect(background,p);
                RectF iconDest = new RectF(
                        (float) itemView.getLeft() + width,
                        (float) itemView.getTop() + width,
                        (float) itemView.getLeft()+ 2*width,
                        (float)itemView.getBottom() - width);
                c.drawBitmap(mLeftBitmap, null, iconDest, p);
            } else {
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF(
                        (float) itemView.getRight() + dX,
                        (float) itemView.getTop(),
                        (float) itemView.getRight(),
                        (float) itemView.getBottom());
                c.drawRect(background, p);
                RectF iconDest = new RectF(
                        (float) itemView.getRight() - 2*width,
                        (float) itemView.getTop() + width,
                        (float) itemView.getRight() - width,
                        (float)itemView.getBottom() - width);
                c.drawBitmap(mRightBitmap, null, iconDest, p);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                Toast.makeText(getApplicationContext(), "Created by: Andras Sanislo", Toast.LENGTH_SHORT)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAddThingActivity() {
        Intent intent = new Intent(ThingsActivity.this, AddThingActivity.class);
        startActivity(intent);
    }

    private void updateProfile() {
        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                .withName(mUser.getFullName())
                .withEmail(mUser.getEmailAddress())
                .withTextColor(Color.BLACK);
        if (!TextUtils.isEmpty(mUser.getAvatarURL())) profileDrawerItem.withIcon(mUser.getAvatarURL());

        mAccountHeader.clear();
        mAccountHeader.setActiveProfile(profileDrawerItem);
    }

    private MaterialDialog mCloseAppDialog;
    private void initCloseAppDialog() {
        mCloseAppDialog = new MaterialDialog.Builder(ThingsActivity.this)
                .title(R.string.close_app_title)
                .content(R.string.close_app_content)
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onBackPressed();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mIsBackPressedOnce = false;
                        mCloseAppDialog.dismiss();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mIsBackPressedOnce = false;
                    }
                })
                .build();
    }

    private boolean mIsBackPressedOnce;
    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            if (mIsBackPressedOnce) {
                mCloseAppDialog.dismiss();
                super.onBackPressed();
            } else {
                mIsBackPressedOnce = true;
                mCloseAppDialog.show();
            }
        }
    }

    private ThingsContract.Presenter mThingsContractPresenter;
    @Override
    public void setPresenter(ThingsContract.Presenter presenter) {
        mThingsContractPresenter = presenter;
    }

    private List<Thing> mThingList = new ArrayList<>();
    @Override
    public void showThings(List<Thing> thingList) {
        if (swipeRefreshThings.isRefreshing()) {
            swipeRefreshThings.setRefreshing(false);
        }
        mThingList.addAll(thingList);
        mThingAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    @Override
    public void showError() {
        makeToast("Oops! Something went wrong...");
    }
}
